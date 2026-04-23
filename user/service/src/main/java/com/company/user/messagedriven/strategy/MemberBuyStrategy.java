package com.company.user.messagedriven.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.messagedriven.BaseStrategy;
import com.company.framework.util.Utils;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.request.OrderRefundFailReq;
import com.company.order.api.request.OrderRefundFinishReq;
import com.company.order.api.response.Order4Resp;
import com.company.user.api.enums.WalletEnum;
import com.company.user.entity.MemberBuyOrder;
import com.company.user.entity.RechargeOrder;
import com.company.user.service.MemberBuyOrderService;
import com.company.user.service.RechargeOrderService;
import com.company.user.service.WalletUseSeqService;
import com.company.user.service.market.UserCouponService;
import com.company.user.wallet.IWallet;
import com.company.user.wallet.dto.FreezeMainChargeGiftAmount;
import com.company.user.wallet.dto.FreezeMainChargeGiftBalance;
import com.company.user.wallet.dto.MainChargeGiftAmount;
import com.company.user.wallet.dto.MainChargeGiftWalletId;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(StrategyConstants.MEMBER_BUY_STRATEGY)
public class MemberBuyStrategy implements BaseStrategy<Map<String, Object>> {

	@Autowired
	private MemberBuyOrderService memberBuyOrderService;
	@Autowired
	private UserCouponService userCouponService;
	@Autowired
	private OrderFeign orderFeign;

	@Autowired
	private RechargeOrderService rechargeOrderService;
	@Autowired
	private IWallet<MainChargeGiftWalletId, MainChargeGiftAmount, FreezeMainChargeGiftAmount, FreezeMainChargeGiftBalance> freezeMainChargeGiftWallet;
	@Autowired
	private WalletUseSeqService walletUseSeqService;

	@Override
	public void doStrategy(Map<String, Object> params) {
		String orderCode = MapUtils.getString(params, "orderCode");
		// 根据orderCode查询会员订单，查不到的情况下直接退出
		MemberBuyOrder memberBuyOrder = memberBuyOrderService.selectByOrderCode(orderCode);
		if (memberBuyOrder == null) {
			log.info("不是购买会员订单");
			return;
		}

		// 处理会员订单逻辑
		Boolean success = MapUtils.getBoolean(params, "success");
		if (success) {// 退款成功
			BigDecimal walletRefundAmount = BigDecimal.ZERO;
			String attach = MapUtils.getString(params, "attach");
			String attachStr = Utils.getByJson(attach, "attach");
			String walletRefundAmountStr = Utils.getByJson(attachStr, "walletRefundAmount");
			if (StringUtils.isNotBlank(walletRefundAmountStr)) {
				// 有钱包金额退款，归还到钱包
				walletRefundAmount = new BigDecimal(walletRefundAmountStr);
			}
			RechargeOrder rechargeOrder = rechargeOrderService.selectByOrderCode(orderCode);
			if (rechargeOrder != null && walletRefundAmount.compareTo(BigDecimal.ZERO) == 0) {
				log.info("是充值的订单但是又没有退还金额给钱包，该次退款是充值订单退款，由充值订单退款逻辑处理");
				return;
			}

//			Boolean refundAll = MapUtils.getBoolean(params, "refundAll");// 如果退款需扣手续费，则需按业务的规则计算是否已全额退款
			// TODO 可能需要扣除一些手续费
			Order4Resp order4Resp = orderFeign.selectByOrderCode(orderCode);
			BigDecimal needPayAmount = order4Resp.getNeedPayAmount();

//			BigDecimal totalRefundAmount = new BigDecimal(MapUtils.getString(params, "totalRefundAmount"));
			BigDecimal totalRefundAmount = order4Resp.getRefundAmount().add(walletRefundAmount);
			BigDecimal serviceAmount = memberBuyOrder.getRefundServiceAmount();
			BigDecimal canRefundAmount = needPayAmount.subtract(serviceAmount).subtract(totalRefundAmount);
			Boolean refundAll = canRefundAmount.compareTo(BigDecimal.ZERO) <= 0;

			// 修改‘订单中心’数据
			OrderRefundFinishReq orderRefundFinishReq = new OrderRefundFinishReq();
			orderRefundFinishReq.setOrderCode(orderCode);
			orderRefundFinishReq.setRefundFinishTime(LocalDateTime.now());
//			BigDecimal totalRefundAmount = new BigDecimal(MapUtils.getString(params, "totalRefundAmount"));
			orderRefundFinishReq.setTotalRefundAmount(totalRefundAmount);
			orderRefundFinishReq.setRefundAll(refundAll);
			Boolean updateSuccess = orderFeign.refundFinish(orderRefundFinishReq);
			if (!updateSuccess) {
				log.warn("修改‘订单中心’数据失败");
				return;
			}

			// 有钱包金额退款，归还到钱包
			if (walletRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
				String uniqueCode = MapUtils.getString(params, "refundOrderCode");

				// 退还wallet_use_seq
				BigDecimal returnTotalAmount = walletRefundAmount;
				Map<WalletEnum.Type, BigDecimal> typeAmountMap = walletUseSeqService.calcAndReturn(
						memberBuyOrder.getUserId(),
						Lists.newArrayList(WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT),
						returnTotalAmount);

				BigDecimal chargeAmount = typeAmountMap.get(WalletEnum.Type.TO_CHARGE);
//				BigDecimal giftAmount = typeAmountMap.get(WalletEnum.Type.TO_GIFT);

				// 优先归还赠送余额
				BigDecimal needReturnGiftAmount = returnTotalAmount.subtract(chargeAmount);
				BigDecimal needReturnChargeAmount = returnTotalAmount.subtract(needReturnGiftAmount);

				Integer userId = memberBuyOrder.getUserId();

				MainChargeGiftWalletId walletId = new MainChargeGiftWalletId(userId, WalletEnum.Type.TO_MAIN,
						WalletEnum.Type.TO_CHARGE, WalletEnum.Type.TO_GIFT);

				MainChargeGiftAmount amount = new MainChargeGiftAmount(returnTotalAmount, needReturnChargeAmount, needReturnGiftAmount);

				Map<String, Object> attachMap = Maps.newHashMap();
				attachMap.put("businessType", "use_refund");
				attachMap.put("businessId", memberBuyOrder.getId());

				freezeMainChargeGiftWallet.income(uniqueCode, walletId, amount, attachMap);
			}

			if (refundAll) {
				// 全额退款，才归还优惠券
				Integer userCouponId = memberBuyOrder.getUserCouponId();
				if (userCouponId != null && userCouponId > 0) {// 有选用优惠券，释放优惠券
					userCouponService.updateStatus(userCouponId, "used", "nouse");
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
