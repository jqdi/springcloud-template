package com.company.user.controller;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.sequence.SequenceGenerator;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.enums.OrderEnum.StatusEnum;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.feign.PayFeign;
import com.company.order.api.request.*;
import com.company.order.api.request.OrderReq.ProductReq;
import com.company.order.api.response.PayResp;
import com.company.tool.api.response.RetryerResp;
import com.company.user.api.constant.Constants;
import com.company.user.api.feign.DistributeOrderFeign;
import com.company.user.api.request.DistributeBuyOrderReq;
import com.company.user.api.request.DistributeBuyOrderReq.UserRemarkReq;
import com.company.user.api.response.DistributeBuyOrderResp;
import com.company.user.api.response.DistributeSubOrderDetailResp;
import com.company.user.api.response.DistributeSubOrderResp;
import com.company.user.coupon.UseCouponService;
import com.company.user.coupon.dto.UserCouponCanUse;
import com.company.user.service.ShopCartService;
import com.company.user.service.ShopCartService.ShopCart;
import com.company.user.service.ShopProductService;
import com.company.user.service.ShopProductService.ShopProduct;
import com.company.user.service.ShopService;
import com.company.user.service.ShopService.Shop;
import com.company.user.service.market.UserCouponService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 配送子订单demo
 */
@Slf4j
@RestController
@RequestMapping("/distributeOrder")
public class DistributeOrderController implements DistributeOrderFeign {

	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Autowired
	private OrderFeign orderFeign;

	@Autowired
	private PayFeign payFeign;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private UseCouponService useCouponService;

	@Autowired
	private UserCouponService userCouponService;

	@Autowired
	private AsyncTaskExecutor executor;

	@Autowired
	private ShopCartService shopCartService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ShopProductService shopProductService;

	/**
	 * 购买
	 *
	 * @param distributeBuyOrderReq
	 * @return
	 */
	@Override
	public DistributeBuyOrderResp buy(@RequestBody DistributeBuyOrderReq distributeBuyOrderReq) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		// 参数校验

		// TODO 从购物车获取商品数据
		List<ShopCart> shopCartList = shopCartService.selectByUserId(userId);
		if (shopCartList.isEmpty()) {
			ExceptionUtil.throwException("购物车未找到商品");
		}

		BigDecimal productAmount = BigDecimal.ZERO;
		for (ShopCart shopCart : shopCartList) {
			BigDecimal salesAmount = shopCart.getSalesAmount();
			Integer number = shopCart.getNumber();
			productAmount = productAmount.add(salesAmount.multiply(new BigDecimal(number)));
		}

		// TODO 计算配送费
		BigDecimal distributeAmount = new BigDecimal("2");

		// TODO 计算保温费
		BigDecimal baowenAmount = new BigDecimal("1");

		// 订单总金额
		BigDecimal orderAmount = productAmount.add(distributeAmount).add(baowenAmount);

		BigDecimal reduceAmount = BigDecimal.ZERO;
		Integer userCouponId = distributeBuyOrderReq.getUserCouponId();
		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券
			Map<String, String> runtimeAttach = Maps.newHashMap();
			String productCodes = shopCartList.stream().map(ShopCart::getProductCode).collect(Collectors.joining(","));
			runtimeAttach.put("productCode", productCodes);
			runtimeAttach.put("orderAmount", orderAmount.toPlainString());
			UserCouponCanUse userCouponCanUse = useCouponService.canUse(userCouponId, userId, orderAmount,
					runtimeAttach);
			if (!userCouponCanUse.getCanUse()) {
				ExceptionUtil.throwException("优惠券不可用");
			}
			reduceAmount = userCouponCanUse.getReduceAmount();
		}

		BigDecimal needPayAmount = orderAmount.subtract(reduceAmount);

		BigDecimal payAmount = distributeBuyOrderReq.getPayAmount();
		if (payAmount.compareTo(needPayAmount) != 0) {
			ExceptionUtil.throwException("支付金额不匹配");
		}

		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，锁定优惠券
			Integer affect = userCouponService.updateStatus(userCouponId, "nouse", "used");
			if (affect == 0) {
				ExceptionUtil.throwException("优惠券不可用");
			}
		}

		// TODO 条件校验（下单限制、风控）

		String orderCode = String.valueOf(sequenceGenerator.nextId());
		// TODO 创建业务订单（订单中心子订单）
		// userId、orderCode、userCouponId、门店

		// 注册到‘订单中心’
		RegisterOrderReq registerOrderReq = new RegisterOrderReq();
		registerOrderReq.setUserId(userId);
		registerOrderReq.setOrderCode(orderCode);
		registerOrderReq.setOrderType("distribute");
		registerOrderReq.setSubStatusEnum(OrderEnum.SubStatusEnum.WAIT_PAY);
		registerOrderReq.setProductAmount(productAmount);
		registerOrderReq.setOrderAmount(orderAmount);
		registerOrderReq.setReduceAmount(reduceAmount);
		registerOrderReq.setNeedPayAmount(needPayAmount);
		registerOrderReq.setSubOrderUrl(Constants.feignUrl("/distributeOrder/subOrder"));

		List<RegisterOrderReq.OrderProductReq> orderProductReqList = Lists.newArrayList();

		List<UserRemarkReq> userRemarkList = distributeBuyOrderReq.getUserRemarkList();
		Map<String, String> productCodeUserRemarkMap = userRemarkList.stream()
				.collect(Collectors.toMap(UserRemarkReq::getProductCode, UserRemarkReq::getUserRemark, (a, b) -> b));
		for (ShopCart shopCart : shopCartList) {
			String productCode = shopCart.getProductCode();
			RegisterOrderReq.OrderProductReq orderProductReq = new RegisterOrderReq.OrderProductReq();
			orderProductReq.setNumber(shopCart.getNumber());
			orderProductReq.setOriginAmount(shopCart.getOriginAmount());
			orderProductReq.setSalesAmount(shopCart.getSalesAmount());
			orderProductReq.setProductCode(productCode);
			orderProductReq.setProductName(shopCart.getProductName());
			orderProductReq.setProductImage(shopCart.getProductImage());

			ShopProduct shopProduct = shopProductService.selectByProductCode(productCode);
			Shop shop = shopService.selectByShopCode(shopProduct.getShopCode());
			String shopCode = shop.getShopCode();
			String shopName = shop.getShopName();
			String shopLogo = shop.getShopLogo();

			String payAttach = "{}";
			payAttach = Utils.append2Json(payAttach, "specContent", shopCart.getSpecContent());
			payAttach = Utils.append2Json(payAttach, "userRemark", productCodeUserRemarkMap.get(productCode));
			payAttach = Utils.append2Json(payAttach, "shopCode", shopCode);
			payAttach = Utils.append2Json(payAttach, "shopName", shopName);
			payAttach = Utils.append2Json(payAttach, "shopLogo", shopLogo);
			orderProductReq.setAttach(payAttach);

			orderProductReqList.add(orderProductReq);
		}

		registerOrderReq.setProductList(orderProductReqList);

		orderFeign.registerOrder(registerOrderReq);

		if (needPayAmount.compareTo(BigDecimal.ZERO) == 0) {
			executor.submit(() -> {
				PayNotifyReq payNotifyReq = new PayNotifyReq();
				payNotifyReq.setEvent(PayNotifyReq.EVENT.PAY);
				payNotifyReq.setOrderCode(orderCode);
				payNotifyReq.setPayAmount(needPayAmount);
				payNotifyReq.setTime(LocalDateTime.now());
                RetryerResp buyNotifyResult = buyNotify(payNotifyReq);
				log.info("buyNotify:{}", buyNotifyResult);
			});
			return new DistributeBuyOrderResp().setNeedPay(false);
		}

		// 获取支付参数
		PayReq payReq = new PayReq();
		payReq.setUserId(userId);
		payReq.setOrderCode(orderCode);
		payReq.setBusinessType(OrderPayEnum.BusinessType.DISTRIBUTE);
		payReq.setMethod(OrderPayEnum.Method.of(distributeBuyOrderReq.getPayMethod()));
		payReq.setAppid(distributeBuyOrderReq.getAppid());
		payReq.setAmount(needPayAmount);
		payReq.setBody("配送下单");
		payReq.setSpbillCreateIp(HeaderContextUtil.requestip());
		payReq.setOpenid(HeaderContextUtil.deviceid());
		payReq.setNotifyUrl(Constants.feignUrl("/distributeOrder/buyNotify"));
		PayResp payResp = payFeign.unifiedorder(payReq);
		if (!payResp.getSuccess()) {
			ExceptionUtil.throwException("支付失败，请稍后重试");
		}
		return new DistributeBuyOrderResp().setNeedPay(true).setPayInfo(payResp.getPayInfo());
	}

	/**
	 * 购买回调(使用restTemplate的方式调用)
	 *
	 * @param payNotifyReq
	 * @return
	 */
	@PostMapping("/buyNotify")
	public RetryerResp buyNotify(@RequestBody PayNotifyReq payNotifyReq) {
		String orderCode = payNotifyReq.getOrderCode();
		LocalDateTime time = payNotifyReq.getTime();

		if (Objects.equals(payNotifyReq.getEvent(), PayNotifyReq.EVENT.CLOSE)) { // 超时未支付关闭订单回调
			log.info("超时未支付关闭订单回调");
			// 修改‘订单中心’数据
			orderFeign.cancelByTimeout(new OrderCancelReq().setOrderCode(orderCode).setCancelTime(time));

			Integer userCouponId = 0;// TODO 根据业务订单获得
			if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，释放优惠券
				userCouponService.updateStatus(userCouponId, "used", "nouse");
			}

			return RetryerResp.end();
		}

		// 支付成功

		// 可能存在订单已经因超时取消了，但用户又支付了的场景，所以订单可以由‘已关闭’变为‘已支付’，所以关闭订单的逻辑需要反着处理一次
		Integer userCouponId = 0;// TODO 根据业务订单获得
		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，使用优惠券
			userCouponService.updateStatus(userCouponId, "nouse", "used");
		}

		// 修改‘订单中心’数据
		BigDecimal payAmount = payNotifyReq.getPayAmount();
		OrderPaySuccessReq orderPaySuccessReq = new OrderPaySuccessReq().setOrderCode(orderCode).setPayAmount(payAmount)
				.setPayTime(time);
		Boolean updateSuccess = orderFeign.paySuccess(orderPaySuccessReq);
		if (!updateSuccess) {
			log.warn("paySuccess,修改‘订单中心’数据失败:{}", orderPaySuccessReq);
			return RetryerResp.end();
		}

    	// 发布‘支付成功’事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderCode", orderCode);
		messageSender.sendBroadcastMessage(params, BroadcastConstants.DISTRIBUTE_PAY_SUCCESS.EXCHANGE);

		return RetryerResp.end();
	}

	/**
	 * 根据订单号查询子订单详情(使用restTemplate的方式调用)
	 *
	 * @param orderReq
	 * @return
	 */
	@PostMapping("/subOrder")
	public Object subOrder(@RequestBody OrderReq orderReq) {
		OrderEnum.SubOrderEventEnum subOrderEvent = orderReq.getSubOrderEvent();
		if (subOrderEvent == OrderEnum.SubOrderEventEnum.USER_CANCEL) {
			userCancel(orderReq);
			return detail(orderReq);
		} else if (subOrderEvent == OrderEnum.SubOrderEventEnum.QUERY_ITEM) {
			return item(orderReq);
		} else if (subOrderEvent == OrderEnum.SubOrderEventEnum.QUERY_DETAIL) {
			return detail(orderReq);
		}
		return null;
	}

	private void userCancel(OrderReq orderReq) {
		String orderCode = orderReq.getOrderCode();

		Integer userCouponId = 0;// TODO 根据业务订单获得
		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，释放优惠券
			userCouponService.updateStatus(userCouponId, "used", "nouse");
		}

		if (orderReq.getNeedPayAmount().compareTo(BigDecimal.ZERO) > 0) {
			// 关闭支付订单，不关心结果
			PayCloseReq payCloseReq = new PayCloseReq();
			payCloseReq.setOrderCode(orderCode);
			Void payCloseResp = payFeign.payClose(payCloseReq);
			log.info("关闭订单结果:{}", payCloseResp);
		}
	}

	private DistributeSubOrderResp item(OrderReq orderReq) {
		DistributeSubOrderResp resp = new DistributeSubOrderResp();
		resp.setDistributeAmount(new BigDecimal("2"));
		resp.setBaowenAmount(new BigDecimal("1"));

		// 只有发货之后才有取餐码
		StatusEnum statusEnum = orderReq.getStatus();
		if (statusEnum == StatusEnum.WAIT_RECEIVE || statusEnum == StatusEnum.COMPLETE || statusEnum == StatusEnum.REFUND) {
			resp.setMealCode("123456");
		}

		List<OrderReq.ProductReq> productList = orderReq.getProductList();
		Integer totalNumber = productList.stream().map(OrderReq.ProductReq::getNumber).reduce(Integer::sum).orElse(0);
		resp.setTotalNumber(totalNumber);

		ProductReq productReq = productList.get(0);
		resp.setProductCode(productReq.getProductCode());
		resp.setProductName(productReq.getProductName());
		resp.setProductImage(productReq.getProductImage());

		String attach = productReq.getAttach();

		String shopCode = Utils.getByJson(attach, "shopCode");
		String shopName = Utils.getByJson(attach, "shopName");
		String shopLogo = Utils.getByJson(attach, "shopLogo");

		resp.setShopCode(shopCode);
		resp.setShopName(shopName);
		resp.setShopLogo(shopLogo);

		List<DistributeSubOrderResp.BottonResp> bottonList = Lists.newArrayList();
		if (OrderEnum.StatusEnum.WAIT_RECEIVE == statusEnum || OrderEnum.StatusEnum.COMPLETE == statusEnum
				|| OrderEnum.StatusEnum.REFUND == statusEnum) { // 发货成功之后才有物流信息
			bottonList.add(new DistributeSubOrderResp.BottonResp("查看物流", "delivery", "orderCode=" + orderReq.getOrderCode(), 40));
			bottonList.add(new DistributeSubOrderResp.BottonResp("保险服务", "baoxian", "orderCode=" + orderReq.getOrderCode(), 60));
		}
		resp.setBottonList(bottonList);
		return resp;
	}

	private DistributeSubOrderDetailResp detail(OrderReq orderReq) {
		DistributeSubOrderDetailResp resp = new DistributeSubOrderDetailResp();
		resp.setDistributeAmount(new BigDecimal("2"));
		resp.setBaowenAmount(new BigDecimal("1"));

		// 只有发货之后才有取餐码
		StatusEnum statusEnum = orderReq.getStatus();
		if (statusEnum == StatusEnum.WAIT_RECEIVE || statusEnum == StatusEnum.COMPLETE || statusEnum == StatusEnum.REFUND) {
			resp.setMealCode("123456");
		}

		List<OrderReq.ProductReq> productList = orderReq.getProductList();
		Map<DistributeSubOrderDetailResp.ShopResp, List<OrderReq.ProductReq>> shopCodeProductListMap = productList
				.stream().collect(Collectors.groupingBy(v -> {
					String attach = v.getAttach();

					String shopCode = Utils.getByJson(attach, "shopCode");
					String shopName = Utils.getByJson(attach, "shopName");
					String shopLogo = Utils.getByJson(attach, "shopLogo");

					DistributeSubOrderDetailResp.ShopResp shopResp = new DistributeSubOrderDetailResp.ShopResp();
					shopResp.setShopCode(shopCode);
					shopResp.setShopName(shopName);
					shopResp.setShopLogo(shopLogo);
					return shopResp;
				}, LinkedHashMap::new, Collectors.toList()));

		List<DistributeSubOrderDetailResp.ShopResp> shopRespList = Lists.newArrayList();
		Set<Entry<DistributeSubOrderDetailResp.ShopResp, List<ProductReq>>> entrySet = shopCodeProductListMap
				.entrySet();
		for (Entry<DistributeSubOrderDetailResp.ShopResp, List<ProductReq>> entry : entrySet) {
			DistributeSubOrderDetailResp.ShopResp key = entry.getKey();
			List<ProductReq> value = entry.getValue();

			List<DistributeSubOrderDetailResp.ShopResp.ProductDetailResp> productDetailRespList = value.stream()
					.map(v -> {
						DistributeSubOrderDetailResp.ShopResp.ProductDetailResp productDetailResp = new DistributeSubOrderDetailResp.ShopResp.ProductDetailResp();
						productDetailResp.setNumber(v.getNumber());
						productDetailResp.setOriginAmount(v.getOriginAmount());
						productDetailResp.setSalesAmount(v.getSalesAmount());
						productDetailResp.setAmount(v.getAmount());
						productDetailResp.setProductCode(v.getProductCode());
						productDetailResp.setProductName(v.getProductName());
						productDetailResp.setProductImage(v.getProductImage());

						String attach = v.getAttach();
						if (StringUtils.isNotBlank(attach)) {
							String specContent = Utils.getByJson(attach, "specContent");
							String userRemark = Utils.getByJson(attach, "userRemark");

							productDetailResp.setSpecContent(specContent);
							productDetailResp.setUserRemark(userRemark);
						}
						return productDetailResp;
					}).collect(Collectors.toList());

			DistributeSubOrderDetailResp.ShopResp shopResp = new DistributeSubOrderDetailResp.ShopResp();
			shopResp.setShopCode(key.getShopCode());
			shopResp.setShopName(key.getShopName());
			shopResp.setShopLogo(key.getShopLogo());
			shopResp.setProductList(productDetailRespList);
			shopRespList.add(shopResp);
		}

		resp.setShopList(shopRespList);

		List<DistributeSubOrderDetailResp.TextValueResp> textValueList = Lists.newArrayList();
		textValueList.add(new DistributeSubOrderDetailResp.TextValueResp("补一条数据展示", "bbbbb"));
		resp.setTextValueList(textValueList);
		return resp;
	}
}
