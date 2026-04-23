package com.company.user.controller;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.sequence.SequenceGenerator;
import com.company.framework.util.JsonUtil;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.feign.PayFeign;
import com.company.order.api.request.*;
import com.company.order.api.response.PayResp;
import com.company.tool.api.response.RetryerResp;
import com.company.user.api.constant.Constants;
import com.company.user.api.enums.WalletEnum.Type;
import com.company.user.api.feign.RechargeOrderFeign;
import com.company.user.api.request.RechargeOrderReq;
import com.company.user.api.response.RechargeOrderResp;
import com.company.user.api.response.RechargeSubOrderDetailResp;
import com.company.user.api.response.RechargeSubOrderResp;
import com.company.user.entity.RechargeOrder;
import com.company.user.service.RechargeOrderService;
import com.company.user.service.WalletUseSeqService;
import com.company.user.wallet.IWallet;
import com.company.user.wallet.dto.FreezeMainChargeGiftAmount;
import com.company.user.wallet.dto.FreezeMainChargeGiftBalance;
import com.company.user.wallet.dto.MainChargeGiftAmount;
import com.company.user.wallet.dto.MainChargeGiftWalletId;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 充值订单demo
 */
@Slf4j
@RestController
@RequestMapping("/rechargeOrder")
public class RechargeOrderController implements RechargeOrderFeign {

	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Autowired
	private OrderFeign orderFeign;

	@Autowired
	private PayFeign payFeign;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private AsyncTaskExecutor executor;

	@Autowired
	private RechargeOrderService rechargeOrderService;

	@Autowired
	private IWallet<MainChargeGiftWalletId, MainChargeGiftAmount, FreezeMainChargeGiftAmount, FreezeMainChargeGiftBalance> freezeMainChargeGiftWallet;
	@Autowired
	private WalletUseSeqService walletUseSeqService;

	/**
	 * 购买
	 *
	 * @param rechargeOrderReq
	 * @return
	 */
	@Override
	public RechargeOrderResp buy(@RequestBody RechargeOrderReq rechargeOrderReq) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		// 参数校验
		BigDecimal rechargeAmount = rechargeOrderReq.getRechargeAmount();
		BigDecimal giftAmount = rechargeOrderReq.getGiftAmount();

		// 订单总金额
		BigDecimal orderAmount = rechargeAmount;

		BigDecimal reduceAmount = BigDecimal.ZERO;

		BigDecimal needPayAmount = orderAmount.subtract(reduceAmount);

		BigDecimal payAmount = rechargeOrderReq.getPayAmount();
		if (payAmount.compareTo(needPayAmount) != 0) {
			ExceptionUtil.throwException("支付金额不匹配");
		}

		// TODO 条件校验（下单限制、风控）

		String orderCode = String.valueOf(sequenceGenerator.nextId());

		// 创建业务订单（订单中心子订单）
		RechargeOrder rechargeOrder = new RechargeOrder();
		rechargeOrder.setUserId(userId);
		rechargeOrder.setOrderCode(orderCode);
		rechargeOrder.setAmount(rechargeAmount);
		rechargeOrder.setGiftAmount(giftAmount);
		rechargeOrderService.save(rechargeOrder);

		// 注册到‘订单中心’
		RegisterOrderReq registerOrderReq = new RegisterOrderReq();
		registerOrderReq.setUserId(userId);
		registerOrderReq.setOrderCode(orderCode);
		registerOrderReq.setOrderType("wallet_recharge");
		registerOrderReq.setSubStatusEnum(OrderEnum.SubStatusEnum.WAIT_PAY);
		registerOrderReq.setProductAmount(rechargeAmount);
		registerOrderReq.setOrderAmount(orderAmount);
		registerOrderReq.setReduceAmount(reduceAmount);
		registerOrderReq.setNeedPayAmount(needPayAmount);
		registerOrderReq.setSubOrderUrl(Constants.feignUrl("/rechargeOrder/subOrder"));
		// registerOrderReq.setAttach(JsonUtil.toJsonString(rechargeOrderAttach));

		List<RegisterOrderReq.OrderProductReq> orderProductReqList = Lists.newArrayList();

		RegisterOrderReq.OrderProductReq orderProductReq = new RegisterOrderReq.OrderProductReq();
		orderProductReq.setNumber(1);
		orderProductReq.setOriginAmount(rechargeAmount);
		orderProductReq.setSalesAmount(rechargeAmount);
		orderProductReq.setProductCode("RW_000001");
		orderProductReq.setProductName("充值");
		orderProductReq.setProductImage("http://www.image.com/wallet_recharge.png");
		orderProductReqList.add(orderProductReq);

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
			return new RechargeOrderResp().setNeedPay(false);
		}

		// 获取支付参数
		PayReq payReq = new PayReq();
		payReq.setUserId(userId);
		payReq.setOrderCode(orderCode);
		payReq.setBusinessType(OrderPayEnum.BusinessType.RECHARGE);
		payReq.setMethod(OrderPayEnum.Method.of(rechargeOrderReq.getPayMethod()));
		payReq.setAppid(rechargeOrderReq.getAppid());
		payReq.setAmount(needPayAmount);
		payReq.setBody("充值");
		payReq.setSpbillCreateIp(HeaderContextUtil.requestip());
		// payReq.setProductId(productId);
		payReq.setOpenid(HeaderContextUtil.deviceid());
		payReq.setNotifyUrl(Constants.feignUrl("/rechargeOrder/buyNotify"));
		// payReq.setAttach(attach);
		// payReq.setTimeoutSeconds(timeoutSeconds);
		// payReq.setRemark(remark);
		PayResp payResp = payFeign.unifiedorder(payReq);
		if (!payResp.getSuccess()) {
			ExceptionUtil.throwException("支付失败，请稍后重试");
		}
		return new RechargeOrderResp().setNeedPay(true).setPayInfo(payResp.getPayInfo());
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
			OrderCancelReq orderCancelReq = new OrderCancelReq().setOrderCode(orderCode).setCancelTime(time);
			Boolean cancelByTimeout = orderFeign.cancelByTimeout(orderCancelReq);
			if (!cancelByTimeout) {
				log.warn("cancelByTimeout,修改‘订单中心’数据失败:{}", orderCancelReq);
				return RetryerResp.end();
			}

            return RetryerResp.end();
		}

		// 支付成功

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
		messageSender.sendBroadcastMessage(params, BroadcastConstants.RECHARGE_PAY_SUCCESS.EXCHANGE);

		RechargeOrder rechargeOrder = rechargeOrderService.selectByOrderCode(orderCode);
		BigDecimal rechargeAmount = rechargeOrder.getAmount();
		BigDecimal giftAmount = rechargeOrder.getGiftAmount();
		Integer userId = rechargeOrder.getUserId();
		// 钱包入账
		MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(userId, Type.TO_MAIN, Type.TO_CHARGE, Type.TO_GIFT);

		MainChargeGiftAmount amount = new MainChargeGiftAmount(rechargeAmount.add(giftAmount), rechargeAmount,
				giftAmount);

		String uniqueCode = orderCode;

		Map<String, Object> attachMap = Maps.newHashMap();
		attachMap.put("businessType", "recharge");
		attachMap.put("businessId", rechargeOrder.getId());

		freezeMainChargeGiftWallet.income(uniqueCode, walletId, amount, attachMap);

		// 记录入账顺序，后续消耗余额的时候需要通过顺序计算充值余额和赠送余额的扣减
		walletUseSeqService.save("charge-" + uniqueCode, userId, Type.TO_CHARGE, rechargeAmount);
		walletUseSeqService.save("gift-" + uniqueCode, userId, Type.TO_GIFT, giftAmount);

		// 修改‘订单中心’数据
		orderFeign.finish(new OrderFinishReq().setOrderCode(orderCode).setFinishTime(time));

        return RetryerResp.end();
	}

	/**
	 * 根据订单号执行/查询子订单(使用restTemplate的方式调用)
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

		if (orderReq.getNeedPayAmount().compareTo(BigDecimal.ZERO) > 0) {
			// 关闭支付订单，不关心结果
			PayCloseReq payCloseReq = new PayCloseReq();
			payCloseReq.setOrderCode(orderCode);
			Void payCloseResp = payFeign.payClose(payCloseReq);
			log.info("关闭订单结果:{}", payCloseResp);
		}
	}

	private RechargeSubOrderResp item(OrderReq orderReq) {
		RechargeSubOrderResp resp = new RechargeSubOrderResp();

		String orderCode = orderReq.getOrderCode();
		RechargeOrder rechargeOrder = rechargeOrderService.selectByOrderCode(orderCode);
		resp.setAmount(rechargeOrder.getAmount());
		resp.setGiftAmount(rechargeOrder.getGiftAmount());

		return resp;
	}

	private RechargeSubOrderDetailResp detail(OrderReq orderReq) {
		RechargeSubOrderDetailResp resp = new RechargeSubOrderDetailResp();

		String orderCode = orderReq.getOrderCode();
		RechargeOrder rechargeOrder = rechargeOrderService.selectByOrderCode(orderCode);
		resp.setAmount(rechargeOrder.getAmount());
		resp.setGiftAmount(rechargeOrder.getGiftAmount());

		return resp;
	}
}
