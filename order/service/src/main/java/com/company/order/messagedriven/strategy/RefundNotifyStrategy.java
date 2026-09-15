package com.company.order.messagedriven.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.company.tool.api.enums.RetryerEnum;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.company.framework.messagedriven.BaseStrategy;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.enums.OrderPayRefundEnum;
import com.company.order.api.request.RefundNotifyReq;
import com.company.order.entity.OrderPayRefund;
import com.company.order.service.FinancialFlowService;
import com.company.order.service.OrderPayRefundService;
import com.company.order.feign.RetryerFeign;
import com.company.tool.api.request.RetryerInfoReq;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(StrategyConstants.REFUND_NOTIFY_STRATEGY)
public class RefundNotifyStrategy implements BaseStrategy<Map<String, Object>> {

	@Autowired
	private OrderPayRefundService orderPayRefundService;
	@Autowired
	private RetryerFeign retryerFeign;

	@Autowired
	private FinancialFlowService financialFlowService;

	@Override
	public void doStrategy(Map<String, Object> params) {
		String outTradeNo = MapUtils.getString(params, "outTradeNo");
		String outRefundNo = MapUtils.getString(params, "outRefundNo");
		Boolean success = MapUtils.getBoolean(params, "success");
		OrderPayRefund orderPayRefund = orderPayRefundService.selectByRefundOrderCode(outRefundNo);

		if (success) {
			// 退款成功
			OrderPayRefund orderPayRefund4Update = new OrderPayRefund();
			orderPayRefund4Update.setStatus(OrderPayRefundEnum.Status.REFUND_SUCCESS.getCode());
			UpdateWrapper<OrderPayRefund> wrapper = new UpdateWrapper<>();
			wrapper.eq("id", orderPayRefund.getId());
			wrapper.eq("status", OrderPayRefundEnum.Status.WAIT_APPLY.getCode());
			boolean affect = orderPayRefundService.update(orderPayRefund4Update, wrapper);
			if (!affect) {
				log.warn("修改退款订单状态失败,可能存在重复回调,outRefundNo:{}", outRefundNo);
				return;
			}

			financialFlow(params, outTradeNo, outRefundNo);

		} else {
			// 退款失败
			OrderPayRefund orderPayRefund4Update = new OrderPayRefund();
			orderPayRefund4Update.setStatus(OrderPayRefundEnum.Status.REFUND_FAIL.getCode());
			UpdateWrapper<OrderPayRefund> wrapper = new UpdateWrapper<>();
			wrapper.eq("id", orderPayRefund.getId());
			wrapper.eq("status", OrderPayRefundEnum.Status.WAIT_APPLY.getCode());
			boolean affect = orderPayRefundService.update(orderPayRefund4Update, wrapper);
			if (!affect) {
				log.warn("修改退款订单状态失败,可能存在重复回调,outRefundNo:{}", outRefundNo);
				return;
			}
		}

		// 回调退款到对应业务中
		String notifyUrl = orderPayRefund.getNotifyUrl();
		if (StringUtils.isBlank(notifyUrl)) {
			log.warn("无回调URL,outRefundNo:{}", outRefundNo);
			return;
		}
		
		RefundNotifyReq refundNotifyReq = new RefundNotifyReq();
		refundNotifyReq.setSuccess(success);
		String message = MapUtils.getString(params, "message");
		refundNotifyReq.setMessage(message);
		refundNotifyReq.setOrderCode(outTradeNo);
		refundNotifyReq.setRefundOrderCode(outRefundNo);
		refundNotifyReq.setAttach(orderPayRefund.getNotifyAttach());
		refundNotifyReq.setPayAmount(orderPayRefund.getAmount());
		refundNotifyReq.setThisRefundAmount(orderPayRefund.getRefundAmount());

		List<OrderPayRefund> refundOrderList = orderPayRefundService.listByOrderCode(outTradeNo);
		// 该订单所有的‘退款成功’订单金额求和
		BigDecimal totalRefundAmount = refundOrderList.stream()
				.filter(o -> OrderPayRefundEnum.Status.REFUND_SUCCESS == OrderPayRefundEnum.Status.of(o.getStatus()))
				.map(OrderPayRefund::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		refundNotifyReq.setTotalRefundAmount(totalRefundAmount);
		refundNotifyReq.setRefundAll(totalRefundAmount.compareTo(orderPayRefund.getAmount()) == 0);

		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder().feignUrl(notifyUrl).jsonParams(refundNotifyReq).waitStrategy(RetryerEnum.WaitStrategy.WECHAT).build();
		retryerFeign.call(retryerInfoReq);
	}

	private void financialFlow(Map<String, Object> params, String outTradeNo, String outRefundNo) {
		//数据落库, 生成财务流水(出账)
		try {
			String tradeNo = MapUtils.getString(params, "tradeNo");
			String amount = MapUtils.getString(params, "amount");
			String orderPayRefundMethod = MapUtils.getString(params, "orderPayRefundMethod");
			String merchantNo =  MapUtils.getString(params, "merchantNo");

			financialFlowService.outAccount(outTradeNo, outRefundNo, tradeNo, new BigDecimal(amount), OrderPayEnum.Method.of(orderPayRefundMethod), merchantNo);
		} catch (Exception e) {
			log.error("生成财务流水(出账), error: {}", e);
		}
	}
}