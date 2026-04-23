package com.company.user.controller;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.sequence.SequenceGenerator;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.feign.PayFeign;
import com.company.order.api.request.*;
import com.company.order.api.response.PayResp;
import com.company.tool.api.response.RetryerResp;
import com.company.user.api.constant.Constants;
import com.company.user.api.enums.WalletEnum;
import com.company.user.api.enums.WalletEnum.Type;
import com.company.user.api.feign.MemberBuyFeign;
import com.company.user.api.request.MemberBuyOrderReq;
import com.company.user.api.response.CalcCanRefundAmountResp;
import com.company.user.api.response.MemberBuyOrderResp;
import com.company.user.api.response.MemberBuySubOrderDetailResp;
import com.company.user.api.response.MemberBuySubOrderResp;
import com.company.user.coupon.UseCouponService;
import com.company.user.coupon.dto.UserCouponCanUse;
import com.company.user.entity.MemberBuyOrder;
import com.company.user.entity.RechargeOrder;
import com.company.user.service.*;
import com.company.user.service.ChargeConfigService.ChargeGiftData;
import com.company.user.service.MemberService.MemberData;
import com.company.user.service.market.UserCouponService;
import com.company.user.wallet.IWallet;
import com.company.user.wallet.dto.*;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 购买会员子订单demo
 */
@Slf4j
@RestController
@RequestMapping("/memberBuy")
public class MemberBuyController implements MemberBuyFeign {

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
	private MemberService memberService;
	@Autowired
	private MemberBuyOrderService memberBuyOrderService;

	@Autowired
	private IWallet<MainChargeGiftWalletId, MainChargeGiftAmount, FreezeMainChargeGiftAmount, FreezeMainChargeGiftBalance> freezeMainChargeGiftWallet;
	@Autowired
	private WalletFreezeService walletFreezeService;
	@Autowired
	private WalletUseSeqService walletUseSeqService;
	@Autowired
	private RechargeOrderService rechargeOrderService;
	@Autowired
	private ChargeConfigService chargeConfigService;

	/**
	 * 购买
	 *
	 * @param memberBuyOrderReq
	 * @return
	 */
	@Override
	public MemberBuyOrderResp buy(@RequestBody MemberBuyOrderReq memberBuyOrderReq) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		// 参数校验
		Integer number = memberBuyOrderReq.getNumber();

		String productCode = memberBuyOrderReq.getProductCode();
		MemberData memberData = memberService.selectByProductCode(productCode);
		if (memberData == null) {
			ExceptionUtil.throwException("商品不存在");
		}
		// TODO 通过productCode得到
		BigDecimal productAmount = memberData.getProductAmount();

		// 订单总金额
		BigDecimal orderAmount = productAmount.multiply(new BigDecimal(number));

		BigDecimal reduceAmount = BigDecimal.ZERO;
		Integer userCouponId = memberBuyOrderReq.getUserCouponId();
		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券
			Map<String, String> runtimeAttach = Maps.newHashMap();
			runtimeAttach.put("productCode", productCode);
			runtimeAttach.put("orderAmount", orderAmount.toPlainString());
			UserCouponCanUse userCouponCanUse = useCouponService.canUse(userCouponId, userId, orderAmount,
					runtimeAttach);
			if (!userCouponCanUse.getCanUse()) {
				ExceptionUtil.throwException("优惠券不可用");
			}
			reduceAmount = userCouponCanUse.getReduceAmount();
		}

		BigDecimal orderNeedPayAmount = orderAmount.subtract(reduceAmount);
		BigDecimal needPayAmount = orderNeedPayAmount;

		String payAttach = "{}";

		String rechargeCode = memberBuyOrderReq.getRechargeCode();
		BigDecimal walletPayAmount = memberBuyOrderReq.getWalletPayAmount();
		BigDecimal sumChargeGiftAmount = BigDecimal.ZERO;
		if (StringUtils.isNotBlank(rechargeCode)) {
			// 如果走充值钱包，需修改需付款金额计算逻辑
			ChargeGiftData chargeGiftData = chargeConfigService.selectByRechargeCode(rechargeCode);
			BigDecimal rechargeAmountDB = chargeGiftData.getChargeAmount();
			BigDecimal rechargeAmount = memberBuyOrderReq.getRechargeAmount();
			if (rechargeAmountDB.compareTo(rechargeAmount) != 0) {
				ExceptionUtil.throwException("充值金额不匹配");
			}
			payAttach = Utils.append2Json(payAttach, "rechargeAmount", rechargeAmount.toPlainString());
			sumChargeGiftAmount = rechargeAmount.add(chargeGiftData.getGiftAmount());
			needPayAmount = rechargeAmount;
		} else {// 未选择充值的情况
			needPayAmount = needPayAmount.subtract(walletPayAmount);
		}

		BigDecimal payAmount = memberBuyOrderReq.getPayAmount();
		if (payAmount.compareTo(needPayAmount) != 0) {
			ExceptionUtil.throwException("支付金额不匹配");
		}

		BigDecimal needUseChargeAmount = BigDecimal.ZERO;
		BigDecimal needUseGiftAmount = BigDecimal.ZERO;
		// 检验钱包余额
		if (walletPayAmount.compareTo(BigDecimal.ZERO) > 0) {
			// 1.普通余额校验
			MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(userId, WalletEnum.Type.TO_MAIN,
					WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT);
			FreezeMainChargeGiftBalance freezeMainChargeGiftBalance = freezeMainChargeGiftWallet.balance(walletId);
			FreezeBalance mainBalance = freezeMainChargeGiftBalance.getMainBalance();
			BigDecimal balance = mainBalance.getBalance().add(sumChargeGiftAmount);
			if (balance.compareTo(walletPayAmount) < 0) {
				ExceptionUtil.throwException("钱包余额不足");
			}
			payAttach = Utils.append2Json(payAttach, "walletPayAmount", walletPayAmount.toPlainString());

			// 2.充值、赠送余额校验
			// 优先使用充值的金额，充值金额用完后在使用赠送金额，根据充值记录，查询出充值金额和赠送金额应该各扣取多少
			Map<WalletEnum.Type, BigDecimal> typeAmountMap = walletUseSeqService.calcAndUse(userId,
					Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT), walletPayAmount);

			BigDecimal walletUseSeqChargeAmount = typeAmountMap.get(WalletEnum.Type.TO_CHARGE);
			BigDecimal walletUseSeqGiftAmount = typeAmountMap.get(WalletEnum.Type.TO_GIFT);

			BigDecimal sumWalletUseSeqChargeGiftAmount = walletUseSeqChargeAmount.add(walletUseSeqGiftAmount);
			// 记录修改的金额，取消订单或退款时归还
			payAttach = Utils.append2Json(payAttach, "calcUseFma", sumWalletUseSeqChargeGiftAmount.toPlainString());

			// 优先使用充值金额，再使用赠送金额
			FreezeBalance chargeBalance = freezeMainChargeGiftBalance.getChargeBalance();
			FreezeBalance giftBalance = freezeMainChargeGiftBalance.getGiftBalance();

			needUseChargeAmount = walletPayAmount.subtract(walletUseSeqGiftAmount);
			if (needUseChargeAmount.compareTo(chargeBalance.getBalance()) > 0) {
				needUseChargeAmount = chargeBalance.getBalance();
			}
			needUseGiftAmount = walletPayAmount.subtract(needUseChargeAmount);
			if (needUseGiftAmount.compareTo(giftBalance.getBalance()) > 0) {
				needUseGiftAmount = giftBalance.getBalance();
			}

			BigDecimal sumNeedUseChargeGiftAmount = needUseChargeAmount.add(needUseGiftAmount);
			BigDecimal balance2 = sumNeedUseChargeGiftAmount.add(sumChargeGiftAmount);
			if (balance2.compareTo(walletPayAmount) < 0) {
				walletUseSeqService.calcAndReturn(userId,
						Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT),
						typeAmountMap.get(WalletEnum.Type.TO_CHARGE).add(typeAmountMap.get(WalletEnum.Type.TO_GIFT)));
				ExceptionUtil.throwException("钱包余额不足");
			}
		}

		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，锁定优惠券
			Integer affect = userCouponService.updateStatus(userCouponId, "nouse", "used");
			if (affect == 0) {
				ExceptionUtil.throwException("优惠券不可用");
			}
		}

		// TODO 条件校验（下单限制、风控）

		String orderCode = String.valueOf(sequenceGenerator.nextId());

		OrderPayEnum.BusinessType payBusinessType = OrderPayEnum.BusinessType.MEMBER;
		String payBody = "购买会员";
		if (StringUtils.isNotBlank(rechargeCode)) {
			BigDecimal rechargeAmount = memberBuyOrderReq.getRechargeAmount();
			ChargeGiftData chargeGiftData = chargeConfigService.selectByRechargeCode(rechargeCode);
			BigDecimal giftAmount = chargeGiftData.getGiftAmount();

			// 创建业务订单（订单中心子订单）
			RechargeOrder rechargeOrder = new RechargeOrder();
			rechargeOrder.setUserId(userId);
			rechargeOrder.setOrderCode(orderCode);
			rechargeOrder.setRechargeCode(rechargeCode);
			rechargeOrder.setAmount(rechargeAmount);
			rechargeOrder.setGiftAmount(giftAmount);
			rechargeOrderService.save(rechargeOrder);

			payBusinessType = OrderPayEnum.BusinessType.RECHARGE;
			payBody = "充值";
		}

		// 创建业务订单（订单中心子订单）
		MemberBuyOrder memberBuyOrder = new MemberBuyOrder();
		memberBuyOrder.setUserId(userId);
		memberBuyOrder.setOrderCode(orderCode);
		memberBuyOrder.setUserCouponId(userCouponId);
		memberBuyOrder.setProductCode(memberData.getProductCode());
		memberBuyOrder.setAmount(memberData.getProductAmount());
		memberBuyOrder.setAddDays(memberData.getAddDays());
		memberBuyOrderService.save(memberBuyOrder);

		if (walletPayAmount.compareTo(BigDecimal.ZERO) > 0) {
			String uniqueCode = orderCode;

			MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(userId, WalletEnum.Type.TO_MAIN,
					WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT);

			BigDecimal freezeChargeAmount = needUseChargeAmount;
			BigDecimal freezeGiftAmount = needUseGiftAmount;
			BigDecimal freezeMainAmount = freezeChargeAmount.add(freezeGiftAmount);
			FreezeMainChargeGiftAmount amount = new FreezeMainChargeGiftAmount(uniqueCode, freezeMainAmount, freezeChargeAmount, freezeGiftAmount, true);

			Map<String, Object> attachMap = Maps.newHashMap();
			attachMap.put("businessType", "use_freeze");
			attachMap.put("businessId", memberBuyOrder.getId());

			Integer affect = freezeMainChargeGiftWallet.outcome(uniqueCode, walletId, amount, attachMap);
			if (affect > 0) {// 冻结成功
				payAttach = Utils.append2Json(payAttach, "freezeChargeAmount", freezeChargeAmount.toPlainString());
				payAttach = Utils.append2Json(payAttach, "freezeGiftAmount", freezeGiftAmount.toPlainString());
				payAttach = Utils.append2Json(payAttach, "freezeMainAmount", freezeMainAmount.toPlainString());
			} else {// 冻结失败，订单已创建，需保证流程继续
				log.warn("冻结失败，订单已创建，需保证流程继续,uniqueCode:{}", uniqueCode);
				payAttach = Utils.append2Json(payAttach, "freezeChargeAmount", BigDecimal.ZERO.toPlainString());
				payAttach = Utils.append2Json(payAttach, "freezeGiftAmount", BigDecimal.ZERO.toPlainString());
				payAttach = Utils.append2Json(payAttach, "freezeMainAmount", BigDecimal.ZERO.toPlainString());
			}
		}

		// 注册到‘订单中心’
		RegisterOrderReq registerOrderReq = new RegisterOrderReq();
		registerOrderReq.setUserId(userId);
		registerOrderReq.setOrderCode(orderCode);
		registerOrderReq.setOrderType("buy_member");
		registerOrderReq.setSubStatusEnum(OrderEnum.SubStatusEnum.WAIT_PAY);
		registerOrderReq.setProductAmount(productAmount);
		registerOrderReq.setOrderAmount(orderAmount);
		registerOrderReq.setReduceAmount(reduceAmount);
		registerOrderReq.setNeedPayAmount(orderNeedPayAmount);
		registerOrderReq.setSubOrderUrl(Constants.feignUrl("/memberBuy/subOrder"));

		String memberBuyAttach = Utils.append2Json(payAttach, "userRemark", memberBuyOrderReq.getUserRemark());
		registerOrderReq.setAttach(memberBuyAttach);

		List<RegisterOrderReq.OrderProductReq> orderProductReqList = Lists.newArrayList();

		RegisterOrderReq.OrderProductReq orderProductReq = new RegisterOrderReq.OrderProductReq();
		orderProductReq.setNumber(number);
		orderProductReq.setOriginAmount(productAmount);
		orderProductReq.setSalesAmount(productAmount);
		orderProductReq.setProductCode(productCode);
		// TODO 通过productCode得到
		String productName = "会员月卡";
		String productImage = "http://www.image.com/member_month.png";
		orderProductReq.setProductName(productName);
		orderProductReq.setProductImage(productImage);
		orderProductReqList.add(orderProductReq);

		registerOrderReq.setProductList(orderProductReqList);

		orderFeign.registerOrder(registerOrderReq);

		if (needPayAmount.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal finalNeedPayAmount = needPayAmount;
			String finalPayAttach = payAttach;
			executor.submit(() -> {
				PayNotifyReq payNotifyReq = new PayNotifyReq();
				payNotifyReq.setEvent(PayNotifyReq.EVENT.PAY);
				payNotifyReq.setOrderCode(orderCode);
				payNotifyReq.setPayAmount(finalNeedPayAmount);
				payNotifyReq.setTime(LocalDateTime.now());
				payNotifyReq.setAttach(finalPayAttach);
                RetryerResp buyNotifyResult = buyNotify(payNotifyReq);
				log.info("buyNotify:{}", buyNotifyResult);
			});
			return new MemberBuyOrderResp().setNeedPay(false);
		}

		// 获取支付参数
		PayReq payReq = new PayReq();
		payReq.setUserId(userId);
		payReq.setOrderCode(orderCode);
		payReq.setBusinessType(payBusinessType);
		payReq.setMethod(OrderPayEnum.Method.of(memberBuyOrderReq.getPayMethod()));
		payReq.setAppid(memberBuyOrderReq.getAppid());
		payReq.setAmount(needPayAmount);
		payReq.setBody(payBody);
		payReq.setSpbillCreateIp(HeaderContextUtil.requestip());
//		payReq.setProductId(productId);
		payReq.setOpenid(HeaderContextUtil.deviceid());
		payReq.setNotifyUrl(Constants.feignUrl("/memberBuy/buyNotify"));
		payReq.setAttach(payAttach);
//		payReq.setTimeoutSeconds(timeoutSeconds);
//		payReq.setRemark(remark);
		PayResp payResp = payFeign.unifiedorder(payReq);
		if (!payResp.getSuccess()) {
			ExceptionUtil.throwException("支付失败，请稍后重试");
		}
		return new MemberBuyOrderResp().setNeedPay(true).setPayInfo(payResp.getPayInfo());
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

			MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);

			String freezeMainAmountStr = Utils.getByJson(payNotifyReq.getAttach(), "freezeMainAmount");
			if (StringUtils.isNotBlank(freezeMainAmountStr)) {// 有冻结的钱包金额
				walletFreezeService.update2Return(orderCode);
			}

			// 还原wallet_use_seq
			String calcUseFmaStr = Utils.getByJson(payNotifyReq.getAttach(), "calcUseFma");
			if (StringUtils.isNotBlank(calcUseFmaStr)) {// 有冻结的钱包金额
				BigDecimal calcUseFma = new BigDecimal(calcUseFmaStr);
				walletUseSeqService.calcAndReturn(memberBuyOrder.getUserId(),
						Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT), calcUseFma);
			}

			Integer userCouponId = memberBuyOrder.getUserCouponId();
			if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，释放优惠券
				userCouponService.updateStatus(userCouponId, "used", "nouse");
			}

			return RetryerResp.end();
		}

		MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);
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

		// 可能存在订单已经因超时取消了，但用户又支付了的场景，所以订单可以由‘已关闭’变为‘已支付’，所以关闭订单的逻辑需要反着处理一次
		Integer userCouponId = memberBuyOrder.getUserCouponId();
		if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，使用优惠券
			userCouponService.updateStatus(userCouponId, "nouse", "used");
		}

		Integer userId = memberBuyOrder.getUserId();
		String rechargeAmountStr = Utils.getByJson(payNotifyReq.getAttach(), "rechargeAmount");
		if (StringUtils.isNotBlank(rechargeAmountStr)) {// 钱包有充值
			RechargeOrder rechargeOrder = rechargeOrderService.selectByOrderCode(orderCode);

			BigDecimal chargeAmount = rechargeOrder.getAmount();
			BigDecimal giftAmount = rechargeOrder.getGiftAmount();
			BigDecimal mainAmount = chargeAmount.add(giftAmount);

			MainChargeGiftAmount amount = new MainChargeGiftAmount(mainAmount, chargeAmount, giftAmount);

			String uniqueCode = orderCode;

			MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(userId, WalletEnum.Type.TO_MAIN,
					WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT);

			Map<String, Object> attachMap = Maps.newHashMap();
			attachMap.put("businessType", "recharge");
			attachMap.put("businessId", memberBuyOrder.getId());

			freezeMainChargeGiftWallet.income(uniqueCode, walletId, amount, attachMap);

			// TODO 记录入账顺序，后续消耗余额的时候需要通过顺序计算充值余额和赠送余额的扣减
			walletUseSeqService.save("charge-" + uniqueCode, userId, Type.TO_CHARGE, chargeAmount);
			walletUseSeqService.save("gift-" + uniqueCode, userId, Type.TO_GIFT, giftAmount);
		}

		String walletPayAmountStr = Utils.getByJson(payNotifyReq.getAttach(), "walletPayAmount");
		if (StringUtils.isNotBlank(walletPayAmountStr)) {// 有钱包支付的金额
			BigDecimal walletPayAmount = new BigDecimal(walletPayAmountStr);

			String freezeMainAmountStr = Utils.getByJson(payNotifyReq.getAttach(), "freezeMainAmount");
			BigDecimal freezeMainAmount = new BigDecimal(freezeMainAmountStr);
//			String freezeChargeAmountStr = Utils.getByJson(payNotifyReq.getAttach(), "freezeChargeAmount");
//			BigDecimal freezeChargeAmount = new BigDecimal(freezeChargeAmountStr);
//			String freezeGiftAmountStr = Utils.getByJson(payNotifyReq.getAttach(), "freezeGiftAmount");
//			BigDecimal freezeGiftAmount = new BigDecimal(freezeGiftAmountStr);

			BigDecimal leftMainAmount = walletPayAmount.subtract(freezeMainAmount);

			// 优先使用充值的金额，充值金额用完后在使用赠送金额，根据充值记录，查询出充值金额和赠送金额应该各扣取多少
			Map<WalletEnum.Type, BigDecimal> typeAmountMap = walletUseSeqService.calcAndUse(userId,
					Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT), leftMainAmount);

//			BigDecimal leftChargeAmount = typeAmountMap.get(WalletEnum.Type.TO_CHARGE);
			BigDecimal leftGiftAmount = typeAmountMap.get(WalletEnum.Type.TO_GIFT);

			MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(userId, WalletEnum.Type.TO_MAIN,
					WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT);
			FreezeMainChargeGiftBalance freezeMainChargeGiftBalance = freezeMainChargeGiftWallet.balance(walletId);
//			FreezeBalance mainBalance = freezeMainChargeGiftBalance.getMainBalance();

			// 优先使用充值金额，再使用赠送金额
			FreezeBalance chargeBalance = freezeMainChargeGiftBalance.getChargeBalance();
			FreezeBalance giftBalance = freezeMainChargeGiftBalance.getGiftBalance();

			BigDecimal needUseChargeAmount = walletPayAmount.subtract(leftGiftAmount);
			if (needUseChargeAmount.compareTo(chargeBalance.getBalance()) > 0) {
				needUseChargeAmount = chargeBalance.getBalance();
			}
			BigDecimal needUseGiftAmount = walletPayAmount.subtract(needUseChargeAmount);
			if (needUseGiftAmount.compareTo(giftBalance.getBalance()) > 0) {
				needUseGiftAmount = giftBalance.getBalance();
			}
			BigDecimal sumNeedUseChargeGiftAmount = needUseChargeAmount.add(needUseGiftAmount);
			if (sumNeedUseChargeGiftAmount.compareTo(leftMainAmount) < 0) {
				// 余额不足扣减,得卡主订单,同时告警
				log.warn("余额不足扣减,leftMainAmount:{},sumNeedUseChargeGiftAmount:{}", leftMainAmount,
						sumNeedUseChargeGiftAmount);
				return RetryerResp.end();
			}

			String uniqueCode = orderCode;

			FreezeMainChargeGiftAmount amount = new FreezeMainChargeGiftAmount(uniqueCode, sumNeedUseChargeGiftAmount,
					needUseChargeAmount, needUseGiftAmount, false);

			Map<String, Object> attachMap = Maps.newHashMap();
			attachMap.put("businessType", "use");
			attachMap.put("businessId", memberBuyOrder.getId());

			Integer affect = freezeMainChargeGiftWallet.outcome(uniqueCode, walletId, amount, attachMap);
			if (affect == 0) {// 如果钱包出账失败,得卡主订单,同时告警
				log.warn("支付回调后钱包余额扣减失败，存在资损风险,orderCode:{}", orderCode);
				return RetryerResp.end();
			}
		}

    	// 发布‘支付成功’事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderCode", orderCode);
		messageSender.sendBroadcastMessage(params, BroadcastConstants.MEMBER_BUY_PAY_SUCCESS.EXCHANGE);

		// TODO 会员过期时间续期，根据业务订单获得‘续期时间长’
//		Integer addDays = memberBuyOrder.getAddDays();

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
		} else if (subOrderEvent == OrderEnum.SubOrderEventEnum.CALC_CANREFUNDAMOUNT) { // 可不处理
			return calcCanRefundAmount(orderReq);
		}
		return null;
	}

	private void userCancel(OrderReq orderReq) {
		String orderCode = orderReq.getOrderCode();

		MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);
		String freezeAmountStr = Utils.getByJson(orderReq.getAttach(), "freezeAmount");
		if (StringUtils.isNotBlank(freezeAmountStr)) {// 有冻结的钱包金额
			walletFreezeService.update2Return(orderCode);
		}

		// 还原wallet_use_seq
		String calcUseFmaStr = Utils.getByJson(orderReq.getAttach(), "calcUseFma");
		if (StringUtils.isNotBlank(calcUseFmaStr)) {// 有冻结的钱包金额
			BigDecimal calcUseFma = new BigDecimal(calcUseFmaStr);
			walletUseSeqService.calcAndReturn(memberBuyOrder.getUserId(),
					Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT), calcUseFma);
		}

		Integer userCouponId = memberBuyOrder.getUserCouponId();
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

	private MemberBuySubOrderResp item(OrderReq orderReq) {
		MemberBuySubOrderResp resp = new MemberBuySubOrderResp();

		String orderCode = orderReq.getOrderCode();
		MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);
		Integer addDays = memberBuyOrder.getAddDays();
		resp.setAddDays(addDays);

		return resp;
	}

	private MemberBuySubOrderDetailResp detail(OrderReq orderReq) {
		MemberBuySubOrderDetailResp resp = new MemberBuySubOrderDetailResp();

		String orderCode = orderReq.getOrderCode();
		MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);
		Integer addDays = memberBuyOrder.getAddDays();
		resp.setAddDays(addDays);

		String attach = orderReq.getAttach();
		if (StringUtils.isNotBlank(attach)) {
			String userRemark = Utils.getByJson(attach, "userRemark");
			resp.setUserRemark(userRemark);
		}

		return resp;
	}

	private CalcCanRefundAmountResp calcCanRefundAmount(OrderReq orderReq) {
		BigDecimal needPayAmount = orderReq.getNeedPayAmount();
		BigDecimal refundAmount = orderReq.getRefundAmount();
		// BigDecimal canRefundAmount = payAmount.subtract(refundAmount);// 原逻辑
		// TODO 可能需要扣除一些手续费
		BigDecimal serviceAmount = needPayAmount.multiply(new BigDecimal("0.1"));// 扣除10%作为服务费，如12306，根据时间来计算手续费
		memberBuyOrderService.updateRefundServiceAmountByOrderCode(serviceAmount, orderReq.getOrderCode());// 这个费用最好记录到子业务
		BigDecimal canRefundAmount = needPayAmount.subtract(serviceAmount).subtract(refundAmount);
		if (canRefundAmount.compareTo(BigDecimal.ZERO) < 0) {
			canRefundAmount = BigDecimal.ZERO;
		}

		// 先退钱包，再退支付平台
		BigDecimal walletRefundAmount = BigDecimal.ZERO;
		String walletPayAmountStr = Utils.getByJson(orderReq.getAttach(), "walletPayAmount");
		if (StringUtils.isNotBlank(walletPayAmountStr)) {// 有钱包支付的金额
			BigDecimal walletPayAmount = new BigDecimal(walletPayAmountStr);
			if (refundAmount.compareTo(walletPayAmount) < 0) {// 钱包金额已退完
				walletRefundAmount = walletPayAmount.subtract(refundAmount);
			}
		}

		String attach = "{}";
		CalcCanRefundAmountResp resp = new CalcCanRefundAmountResp();
		if (canRefundAmount.compareTo(walletRefundAmount) < 0) {
			resp.setCanRefundAmount(BigDecimal.ZERO);
			attach = Utils.append2Json(attach, "walletRefundAmount", canRefundAmount.toPlainString());
		} else {
			resp.setCanRefundAmount(canRefundAmount.subtract(walletRefundAmount));
			attach = Utils.append2Json(attach, "walletRefundAmount", walletRefundAmount.toPlainString());
		}
		resp.setAttach(attach);
		return resp;
	}
}
