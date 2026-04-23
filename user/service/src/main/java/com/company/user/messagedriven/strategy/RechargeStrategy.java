package com.company.user.messagedriven.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.messagedriven.BaseStrategy;
import com.company.framework.sequence.SequenceGenerator;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.enums.PayRefundApplyEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.feign.RefundApplyFeign;
import com.company.order.api.request.OrderRefundFailReq;
import com.company.order.api.request.OrderRefundFinishReq;
import com.company.order.api.request.PayRefundApplyReq;
import com.company.order.api.response.Order4Resp;
import com.company.user.api.enums.WalletEnum;
import com.company.user.api.enums.WalletEnum.Type;
import com.company.user.entity.MemberBuyOrder;
import com.company.user.entity.RechargeOrder;
import com.company.user.service.MemberBuyOrderService;
import com.company.user.service.RechargeOrderService;
import com.company.user.service.WalletUseSeqService;
import com.company.user.wallet.IWallet;
import com.company.user.wallet.dto.MainChargeGiftAmount;
import com.company.user.wallet.dto.MainChargeGiftBalance;
import com.company.user.wallet.dto.MainChargeGiftWalletId;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(StrategyConstants.RECHARGE_STRATEGY)
public class RechargeStrategy implements BaseStrategy<Map<String, Object>> {

	@Autowired
	private MemberBuyOrderService memberBuyOrderService;
	@Autowired
	private OrderFeign orderFeign;

	@Autowired
	private RechargeOrderService rechargeOrderService;
	@Autowired
	private IWallet<MainChargeGiftWalletId, MainChargeGiftAmount, MainChargeGiftAmount, MainChargeGiftBalance> mainChargeGiftWallet;
	@Autowired
	private WalletUseSeqService walletUseSeqService;
	@Autowired
	private RefundApplyFeign refundApplyFeign;
	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Override
	public void doStrategy(Map<String, Object> params) {
		String orderCode = MapUtils.getString(params, "orderCode");
		// 根据orderCode查询会员订单，查不到的情况下直接退出
		RechargeOrder rechargeOrder = rechargeOrderService.selectByOrderCode(orderCode);
		if (rechargeOrder == null) {
			log.info("不是充值订单");
			return;
		}

		// 处理订单逻辑
		Boolean success = MapUtils.getBoolean(params, "success");
		if (success) {// 退款成功
			Boolean refundAll = MapUtils.getBoolean(params, "refundAll");

			// 修改‘订单中心’数据
			OrderRefundFinishReq orderRefundFinishReq = new OrderRefundFinishReq();
			orderRefundFinishReq.setOrderCode(orderCode);
			orderRefundFinishReq.setRefundFinishTime(LocalDateTime.now());
			BigDecimal totalRefundAmount = new BigDecimal(MapUtils.getString(params, "totalRefundAmount"));
			orderRefundFinishReq.setTotalRefundAmount(totalRefundAmount);
			orderRefundFinishReq.setRefundAll(refundAll);
			Boolean updateSuccess = orderFeign.refundFinish(orderRefundFinishReq);
			if (!updateSuccess) {
				log.warn("修改‘订单中心’数据失败");
				return;
			}

			if (refundAll) {// 全额退款
				// 扣回余额（注：直接扣回可能存在余额不足导致失败的情况，这种情况下余额有多少扣多少）
				MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(rechargeOrder.getUserId(), Type.TO_MAIN, Type.TO_CHARGE, Type.TO_GIFT);

				MainChargeGiftBalance balance = mainChargeGiftWallet.balance(walletId);
				BigDecimal mainBalance = balance.getMainBalance();
				BigDecimal chargeBalance = balance.getChargeBalance();
				BigDecimal giftBalance = balance.getGiftBalance();

				BigDecimal mainAmount = rechargeOrder.getAmount().add(rechargeOrder.getGiftAmount());
				if (mainAmount.compareTo(mainBalance) > 0) {
					log.warn("{}充值订单退款扣减余额不足,balance:{},amount:{}", Type.TO_MAIN, mainBalance, mainAmount);
					mainAmount = mainBalance;
				}
				BigDecimal chargeAmount = rechargeOrder.getAmount();
				if (chargeAmount.compareTo(chargeBalance) > 0) {
					log.warn("{}充值订单退款扣减余额不足,balance:{},amount:{}", Type.TO_CHARGE, chargeBalance, chargeAmount);
					chargeAmount = chargeBalance;
				}
				BigDecimal giftAmount = rechargeOrder.getGiftAmount();
				if (giftAmount.compareTo(giftBalance) > 0) {
					log.warn("{}充值订单退款扣减余额不足,balance:{},amount:{}", Type.TO_GIFT, giftBalance, giftAmount);
					giftAmount = giftBalance;
				}

				MainChargeGiftAmount amount = new MainChargeGiftAmount(mainAmount, chargeAmount, giftAmount);

				String uniqueCode = MapUtils.getString(params, "refundOrderCode");

				Map<String, Object> attachMap = Maps.newHashMap();
				attachMap.put("businessType", "recharge_refund");
				attachMap.put("businessId", rechargeOrder.getId());

				// 冻结的余额无法处理，所以这里只需要用MainChargeGiftWallet的逻辑就行了
				Integer walletRecordId = mainChargeGiftWallet.outcome(uniqueCode, walletId, amount, attachMap);
				if (walletRecordId <= 0) {
					log.warn("{}充值订单退款扣减余额失败,balance:{},amount:{}", walletId,
							balance, amount);
				}

				// 扣回wallet_use_seq
				walletUseSeqService.calcAndUse(rechargeOrder.getUserId(),
						Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT),
						rechargeOrder.getAmount().add(rechargeOrder.getGiftAmount()));

				// TODO 根据业务需求，充值订单全退的情况下，决定是否要退回业务订单
				MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);
				if (memberBuyOrder != null) {
					Order4Resp order4Resp = orderFeign.selectByOrderCode(orderCode);
					// TODO 这里可以加订单状态、时间等逻辑来判断业务订单是否要退
					BigDecimal needPayAmount = order4Resp.getNeedPayAmount();
					BigDecimal refundAmount = order4Resp.getRefundAmount();
					BigDecimal leftAmount = needPayAmount.subtract(refundAmount);
					if (leftAmount.compareTo(BigDecimal.ZERO) > 0) {
						String refundOrderCode = String.valueOf(sequenceGenerator.nextId());
						PayRefundApplyReq payRefundApplyReq = new PayRefundApplyReq();
						payRefundApplyReq.setOrderCode(refundOrderCode);
						payRefundApplyReq.setOldOrderCode(orderCode);

						payRefundApplyReq.setAmount(BigDecimal.ZERO);
						payRefundApplyReq.setBusinessType(PayRefundApplyEnum.BusinessType.SYS_AUTO);
						payRefundApplyReq.setVerifyStatus(PayRefundApplyEnum.VerifyStatus.PASS);
						payRefundApplyReq.setReason("充值订单全额退款");

						String attach = Utils.append2Json(null, "oldSubStatus",
								String.valueOf(order4Resp.getSubStatus()));
						attach = Utils.append2Json(attach, "attach",
								Utils.append2Json("{}", "walletRefundAmount", leftAmount.toPlainString()));
						payRefundApplyReq.setAttach(attach);

						refundApplyFeign.refundApply(payRefundApplyReq);
					}
				}
			}
		} else {// 退款失败
			// 修改‘订单中心’数据（还原订单状态）
			OrderRefundFailReq orderRefundFailReq = new OrderRefundFailReq();
			orderRefundFailReq.setOrderCode(orderCode);

			String attach = MapUtils.getString(params, "attach");
			Integer oldSubStatusInt = Integer.valueOf(Utils.getByJson(attach, "oldSubStatus"));
			OrderEnum.SubStatusEnum oldSubStatus = OrderEnum.SubStatusEnum.of(oldSubStatusInt);
			orderRefundFailReq.setOldSubStatus(oldSubStatus);
			String message = MapUtils.getString(params, "message");
			orderRefundFailReq.setFailReason(message);

			Boolean updateSuccess = orderFeign.refundFail(orderRefundFailReq);
			if (!updateSuccess) {
				log.warn("修改‘订单中心’数据失败");
			}
		}
	}
}
