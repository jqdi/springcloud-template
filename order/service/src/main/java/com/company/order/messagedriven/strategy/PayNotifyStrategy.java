package com.company.order.messagedriven.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.company.tool.api.enums.RetryerEnum;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.company.framework.util.JsonUtil;
import com.company.framework.messagedriven.BaseStrategy;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.request.PayNotifyReq;
import com.company.order.entity.OrderPay;
import com.company.order.service.FinancialFlowService;
import com.company.order.service.OrderPayService;
import com.company.tool.api.feign.RetryerFeign;
import com.company.tool.api.request.RetryerInfoReq;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(StrategyConstants.PAY_NOTIFY_STRATEGY)
public class PayNotifyStrategy implements BaseStrategy<Map<String, Object>> {

	@Autowired
	private OrderPayService orderPayService;
	@Autowired
	private RetryerFeign retryerFeign;

	@Autowired
	private FinancialFlowService financialFlowService;

	@Override
	public void doStrategy(Map<String, Object> params) {
		String outTradeNo = MapUtils.getString(params, "outTradeNo");
		// 支付成功
		OrderPay orderPay4Update = new OrderPay();
		orderPay4Update.setStatus(OrderPayEnum.Status.PAYED.getCode());
		UpdateWrapper<OrderPay> wrapper = new UpdateWrapper<OrderPay>();
		wrapper.eq("order_code", outTradeNo);
		// 可能存在订单已经因超时取消了，但用户又支付了的场景，所以订单可以由‘待支付、已关闭’变为‘已支付’
		wrapper.in("status",
				Lists.newArrayList(OrderPayEnum.Status.WAITPAY.getCode(), OrderPayEnum.Status.CLOSED.getCode()));
		boolean affect = orderPayService.update(orderPay4Update, wrapper);
		if (!affect) {
			log.warn("修改支付订单状态失败,可能存在重复回调,outTradeNo:{}", outTradeNo);
			return;
		}

		financialFlow(params, outTradeNo);

		OrderPay orderPay = orderPayService.selectByOrderCode(outTradeNo);

		String notifyUrl = orderPay.getNotifyUrl();
		if (StringUtils.isBlank(notifyUrl)) {
			log.warn("无回调URL,outTradeNo:{}", outTradeNo);
			return;
		}

		// 回调支付到对应业务中
		PayNotifyReq payNotifyReq = new PayNotifyReq();
		payNotifyReq.setEvent(PayNotifyReq.EVENT.PAY);
		payNotifyReq.setOrderCode(outTradeNo);
		payNotifyReq.setPayAmount(orderPay.getAmount());

		LocalDateTime time = LocalDateTime.now();
		String timeStr = MapUtils.getString(params, "time");
		if (StringUtils.isNotBlank(timeStr)) {
			time = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		payNotifyReq.setTime(time);
		payNotifyReq.setAttach(orderPay.getNotifyAttach());

		log.info("支付回调,请求地址:{},参数:{}", notifyUrl, payNotifyReq);
		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder().feignUrl(notifyUrl).jsonParams(payNotifyReq).waitStrategy(RetryerEnum.WaitStrategy.WECHAT).build();
		retryerFeign.call(retryerInfoReq);
	}

	private void financialFlow(Map<String, Object> params, String outTradeNo) {
		// 数据落库, 生成财务流水(入账)
		try {
			String tradeNo = MapUtils.getString(params, "tradeNo");
			String amount = MapUtils.getString(params, "amount");
			String orderPayMethod = MapUtils.getString(params, "orderPayMethod");
			String merchantNo = MapUtils.getString(params, "merchantNo");

			financialFlowService.inAccount(outTradeNo, tradeNo, new BigDecimal(amount),
					OrderPayEnum.Method.of(orderPayMethod), merchantNo);
		} catch (Exception e) {
			log.error("生成财务流水(入账), error: {}", e);
		}
	}
}
