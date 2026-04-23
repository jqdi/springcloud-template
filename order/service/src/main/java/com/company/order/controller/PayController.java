package com.company.order.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.lock.LockClient;
import com.company.framework.lock.annotation.Lock;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.framework.util.JsonUtil;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.enums.OrderPayRefundEnum;
import com.company.order.api.feign.PayFeign;
import com.company.order.api.request.*;
import com.company.order.api.response.*;
import com.company.order.entity.OrderPay;
import com.company.order.entity.OrderPayRefund;
import com.company.order.messagedriven.Constants;
import com.company.order.messagedriven.strategy.StrategyConstants;
import com.company.order.pay.PayFactory;
import com.company.order.pay.core.PayClient;
import com.company.order.pay.dto.PayParams;
import com.company.order.service.OrderPayRefundService;
import com.company.order.service.OrderPayService;
import com.company.tool.api.feign.RetryerFeign;
import com.company.tool.api.request.RetryerInfoReq;
import com.company.tool.api.response.RetryerResp;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 收银台
 */
@Slf4j
@RestController
@RequestMapping(value = "/pay")
public class PayController implements PayFeign {

	@Autowired
	private OrderPayService orderPayService;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private MessagedrivenProperties messagedrivenProperties;

	@Autowired
	private OrderPayRefundService orderPayRefundService;

	@Autowired
	private RetryerFeign retryerFeign;

	@Autowired
	private LockClient lockClient;

	private static final String NOTIFY_URL_REFUND = com.company.order.api.constant.Constants.feignUrl("/pay/refundWithRetry");
	private static final String NOTIFY_URL_TIMEOUT = com.company.order.api.constant.Constants.feignUrl("/pay/timeoutWithRetry");
	private static final String NOTIFY_URL_PAYRESULT = com.company.order.api.constant.Constants.feignUrl("/pay/pollingPayResult");
	private static final String NOTIFY_URL_REFUNDRESULT = com.company.order.api.constant.Constants.feignUrl("/pay/pollingRefundResult");

    @Lock("'lock:orderpay:ordercode:'+#payReq.orderCode")
	@Override
	public PayResp unifiedorder(@RequestBody @Valid PayReq payReq) {
		String orderCode = payReq.getOrderCode();
        OrderPay orderPay = orderPayService.selectByOrderCode(orderCode);
        if (orderPay == null) {
            orderPay = new OrderPay();
            orderPay.setUserId(payReq.getUserId());
            orderPay.setOrderCode(orderCode);
            orderPay.setBusinessType(payReq.getBusinessType().getCode());
            orderPay.setMethod(payReq.getMethod().getCode());
            orderPay.setAmount(payReq.getAmount());
            orderPay.setBody(payReq.getBody());
            orderPay.setProductId(payReq.getProductId());
            orderPay.setStatus(OrderPayEnum.Status.WAITPAY.getCode());
            orderPay.setNotifyUrl(payReq.getNotifyUrl());
            orderPay.setNotifyAttach(payReq.getAttach());
            orderPay.setRemark(payReq.getRemark());

            orderPayService.save(orderPay);
        } else {
            OrderPay orderPay4Update = new OrderPay();
            orderPay4Update.setId(orderPay.getId());
            orderPay4Update.setBusinessType(payReq.getBusinessType().getCode());
            orderPay4Update.setMethod(payReq.getMethod().getCode());
            orderPay4Update.setAmount(payReq.getAmount());
            orderPay4Update.setBody(payReq.getBody());
            orderPay4Update.setProductId(payReq.getProductId());
            orderPay4Update.setStatus(OrderPayEnum.Status.WAITPAY.getCode());
            orderPay4Update.setNotifyUrl(payReq.getNotifyUrl());
            orderPay4Update.setNotifyAttach(payReq.getAttach());
            orderPay4Update.setRemark(payReq.getRemark());

            orderPayService.updateById(orderPay4Update);
        }

        payTimeout(orderCode, payReq.getTimeoutSeconds());// 订单超时处理

        // 支付参数
        PayParams payParams = new PayParams();
        payParams.setAppid(payReq.getAppid());
        payParams.setMchid(payReq.getMchid());
        payParams.setAmount(payReq.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));// 向上取整，保留2位小数
        payParams.setBody(payReq.getBody());
        payParams.setOutTradeNo(orderCode);
        payParams.setSpbillCreateIp(payReq.getSpbillCreateIp());
        payParams.setProductId(payReq.getProductId());
        payParams.setOpenid(payReq.getOpenid());

        PayClient tradeClient = PayFactory.of(payReq.getMethod());
        PayResp payResp = tradeClient.pay(payParams);
        if (!payResp.getSuccess()) {
            ExceptionUtil.throwException(payResp.getMessage());
        }

        payResult(orderCode);// 主动查询支付结果

		return payResp;
	}

	/**
	 * 订单超时未支付延时任务
	 *
	 * @param orderCode
	 *            订单号
	 * @param timeoutSeconds
	 *            超时秒数
	 */
	private void payTimeout(String orderCode, Integer timeoutSeconds) {
		// 异步处理 ========> 逻辑上等同于直接调用 timeoutWithRetry
		PayTimeoutReq payTimeoutReq = new PayTimeoutReq();
		payTimeoutReq.setOrderCode(orderCode);

		if (timeoutSeconds == null) {
			timeoutSeconds = 1800;// 默认30分钟,1800秒
		}
		LocalDateTime timeoutTime = LocalDateTime.now().plusSeconds(timeoutSeconds);
		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder()
				.feignUrl(NOTIFY_URL_TIMEOUT)
				.jsonParams(payTimeoutReq)
				.nextDisposeTime(timeoutTime)
				.build();
		retryerFeign.call(retryerInfoReq);
	}

	/**
	 * 订单超时处理(使用restTemplate的方式调用)
	 *
	 * @param payTimeoutReq
	 * @return
	 */
	@PostMapping("/timeoutWithRetry")
	public RetryerResp timeoutWithRetry(@RequestBody @Valid PayTimeoutReq payTimeoutReq) {
		String orderCode = payTimeoutReq.getOrderCode();

		OrderPay orderPay = orderPayService.selectByOrderCode(orderCode);
		if (orderPay == null) {
            return RetryerResp.goon("数据不存在");
		}
		OrderPayEnum.Status status = OrderPayEnum.Status.of(orderPay.getStatus());
		if (status != OrderPayEnum.Status.WAITPAY) {
			// 订单不是未支付状态，不允许关闭
			return RetryerResp.end();
		}

		PayClient payClient = PayFactory.of(OrderPayEnum.Method.of(orderPay.getMethod()));
		PayOrderQueryResp payOrderQuery = payClient.orderQuery(orderCode);
		if (payOrderQuery.getResult()) {
			if (payOrderQuery.getPaySuccess()) {
				// 订单已支付
                return RetryerResp.end();
			}
			// 出结果是未支付成功，走关闭逻辑
		} else {
			// 未出结果则认为是未支付成功，走关闭逻辑
		}

		PayCloseResp payCloseResp = payClient.payClose(orderCode);
		log.info("关闭订单结果:{}", payCloseResp);
		if (!payCloseResp.getSuccess()) {
            return RetryerResp.goon(payCloseResp.getMessage());
		}

		// 修改状态为已关闭
		OrderPay orderPay4Update = new OrderPay();
		orderPay4Update.setStatus(OrderPayEnum.Status.CLOSED.getCode());
		LocalDateTime time = LocalDateTime.now();
		orderPay4Update.setPayTime(time);
		UpdateWrapper<OrderPay> wrapper = new UpdateWrapper<>();
		wrapper.eq("id", orderPay.getId());
		wrapper.eq("status", OrderPayEnum.Status.WAITPAY.getCode());
		boolean affect = orderPayService.update(orderPay4Update, wrapper);
		if (!affect) {// 更新不成功，说明订单不是处理中状态
			log.info("update订单不是处理中状态，不操作关闭:{}", orderCode);
            return RetryerResp.end();
		}

		String notifyUrl = orderPay.getNotifyUrl();
		if (StringUtils.isBlank(notifyUrl)) {
			log.info("无回调URL");
            return RetryerResp.end();
		}

		// 回调超时未支付关闭订单到对应业务中
		PayNotifyReq payNotifyReq = new PayNotifyReq();
		payNotifyReq.setEvent(PayNotifyReq.EVENT.CLOSE);
		payNotifyReq.setOrderCode(orderCode);
		payNotifyReq.setTime(time);
		payNotifyReq.setAttach(orderPay.getNotifyAttach());

		log.info("超时未支付关闭订单回调,请求地址:{},参数:{}", notifyUrl, payNotifyReq);
		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder().feignUrl(notifyUrl).jsonParams(payNotifyReq).build();
		retryerFeign.call(retryerInfoReq);

        return RetryerResp.end();
	}

	/**
	 * 支付结果查询
	 *
	 * @param orderCode
	 *            订单号
	 */
	private void payResult(String orderCode) {
		// 异步处理 ========> 逻辑上等同于直接调用 pollingPayResult
		PayResultReq payResultReq = new PayResultReq();
		payResultReq.setOrderCode(orderCode);
		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder().feignUrl(NOTIFY_URL_PAYRESULT).jsonParams(payResultReq)
				.maxFailure(15/* 预计总时长9小时 */).build();
		retryerFeign.call(retryerInfoReq);
	}

	/**
	 * <pre>
	 * 轮询支付结果(使用restTemplate的方式调用)
	 * 注：需与第三方回调做好并发/幂等处理
	 * </pre>
	 *
	 * @param payResultReq
	 * @return
	 */
	@PostMapping("/pollingPayResult")
	public RetryerResp pollingPayResult(@RequestBody @Valid PayResultReq payResultReq) {
		String orderCode = payResultReq.getOrderCode();

		OrderPay orderPay = orderPayService.selectByOrderCode(orderCode);
		if (orderPay == null) {
            return RetryerResp.goon("数据不存在");
		}
		OrderPayEnum.Status status = OrderPayEnum.Status.of(orderPay.getStatus());
		if (status != OrderPayEnum.Status.WAITPAY) {
			// 订单不是未支付状态，结束查询
            return RetryerResp.end("订单不是未支付状态，结束查询");
		}

		PayClient payClient = PayFactory.of(OrderPayEnum.Method.of(orderPay.getMethod()));
		PayOrderQueryResp payOrderQuery = payClient.orderQuery(orderCode);
		if (!payOrderQuery.getResult()) {
			ExceptionUtil.throwException(payOrderQuery.getMessage());
		}
		if (!payOrderQuery.getPaySuccess()) {
			// 有结果但不是支付成功，结束查询
            return RetryerResp.end("有结果但不是支付成功，结束查询");
		}

		// MQ异步处理
		Map<String, Object> params = Maps.newHashMap();
		params.put("outTradeNo", orderCode);

		LocalDateTime payTime = payOrderQuery.getPayTime();
		params.put("time", payTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		// 财务流水信息
		params.put("amount", payOrderQuery.getPayAmount().toPlainString());
		params.put("orderPayMethod", orderPay.getMethod());
		params.put("merchantNo", payOrderQuery.getMerchantNo());
		params.put("tradeNo", payOrderQuery.getTradeNo());

		messageSender.sendNormalMessage(StrategyConstants.PAY_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				Constants.QUEUE.PAY_NOTIFY.KEY);

		return RetryerResp.end("支付成功");
	}

	@Override
	public Void payClose(@RequestBody @Valid PayCloseReq payCloseReq) {
		String orderCode = payCloseReq.getOrderCode();
		OrderPay orderPay = orderPayService.selectByOrderCode(orderCode);
		if (orderPay == null) {
			ExceptionUtil.throwException("数据不存在");
		}
		OrderPayEnum.Status status = OrderPayEnum.Status.of(orderPay.getStatus());
		if (status != OrderPayEnum.Status.WAITPAY) {
			// 订单不是未支付状态，不允许关闭
			ExceptionUtil.throwException("订单不是待支付状态，不允许关闭");
		}

		// 修改状态为已关闭
		OrderPay orderPay4Update = new OrderPay();
		orderPay4Update.setStatus(OrderPayEnum.Status.CLOSED.getCode());
		UpdateWrapper<OrderPay> wrapper = new UpdateWrapper<>();
		wrapper.eq("id", orderPay.getId());
		wrapper.eq("status", OrderPayEnum.Status.WAITPAY.getCode());
		boolean affect = orderPayService.update(orderPay4Update, wrapper);
		if (!affect) {// 更新不成功，说明订单不是处理中状态
			log.info("update订单不是处理中状态，不操作关闭:{}", orderCode);
			return null;
		}

		Map<String, Object> params = new HashMap<>();
		params.put("outTradeNo", orderCode);

		LocalDateTime minPayCloseTime = orderPay.getCreateTime().plusMinutes(5);
		LocalDateTime now = LocalDateTime.now();
		// 微信订单不能创建后马上关闭，延迟调用微信关闭接口时间 单位：s
		// 官方文档：https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=9_3&index=3
		Integer delay = 0;
		if (minPayCloseTime.compareTo(now) > 0) {
			delay = (int) LocalDateTimeUtil.between(now, minPayCloseTime, ChronoUnit.SECONDS);
		}

		messageSender.sendDelayMessage(StrategyConstants.PAY_CLOSE_STRATEGY, params, messagedrivenProperties.getExchange().getXdelayed(),
				messagedrivenProperties.getQueue().getXdelayed().getKey(), delay);

		return null;
	}

	@Override
	public PayResp toPay(@RequestBody ToPayReq toPayReq) {
		String orderCode = toPayReq.getOrderCode();
		OrderPay orderPay = orderPayService.selectByOrderCode(orderCode);
		if (orderPay == null) {
			ExceptionUtil.throwException("数据不存在");
		}

		if (OrderPayEnum.Status.of(orderPay.getStatus()) != OrderPayEnum.Status.WAITPAY) {
			ExceptionUtil.throwException("订单不是待支付状态，不允许支付");
		}

		OrderPayEnum.Method method = toPayReq.getMethod();
		if (method == null) {
			method = OrderPayEnum.Method.of(orderPay.getMethod());
		} else {// 更换支付方式
			OrderPay orderPay4Update = new OrderPay();
			orderPay4Update.setId(orderPay.getId());
			orderPay4Update.setMethod(method.getCode());
			orderPayService.updateById(orderPay4Update);
		}

		// 支付参数
		PayParams payParams = new PayParams();
		payParams.setAppid(toPayReq.getAppid());
		payParams.setAmount(orderPay.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));// 向上取整，保留2位小数
		payParams.setBody(orderPay.getBody());
		payParams.setOutTradeNo(orderCode);
		payParams.setSpbillCreateIp(toPayReq.getSpbillCreateIp());
		payParams.setProductId(orderPay.getProductId());
		payParams.setOpenid(toPayReq.getOpenid());

		PayClient tradeClient = PayFactory.of(method);
		PayResp payResp = tradeClient.pay(payParams);
		if (!payResp.getSuccess()) {
			ExceptionUtil.throwException(payResp.getMessage());
		}

		return payResp;
	}

	@Override
	public Void refund(@RequestBody @Valid PayRefundReq payRefundReq) {
		String outTradeNo = payRefundReq.getOrderCode();
		OrderPay orderPay = orderPayService.selectByOrderCode(outTradeNo);
		if (orderPay == null) {
			ExceptionUtil.throwException("订单不存在");
		}

		if (OrderPayEnum.Status.of(orderPay.getStatus()) != OrderPayEnum.Status.PAYED) {
			// 原订单不是已支付状态，不允许退款
			ExceptionUtil.throwException("订单不是完成状态，不允许退款");
		}

		String refundOrderCode = payRefundReq.getRefundOrderCode();
		String key = String.format("lock:orderrefund:ordercode:%s", refundOrderCode);
		String message = lockClient.doInLock(key, () -> {
			OrderPayRefund refundOrderPay = orderPayRefundService.selectByRefundOrderCode(refundOrderCode);
			if (refundOrderPay != null) {
				return null;
			}
			// 已退款金额是否已达到订单总金额
			BigDecimal sumRefundAmount = orderPayRefundService.sumRefundAmount(outTradeNo);
			if (orderPay.getAmount().compareTo(sumRefundAmount) <= 0) {
				return "已全额退款";
			}

			BigDecimal leftAmount = orderPay.getAmount().subtract(sumRefundAmount);// 剩余金额
			// 退款金额与剩余金额比较
			BigDecimal refundAmount = payRefundReq.getRefundAmount();
			if (refundAmount != null) {
				if (refundAmount.compareTo(leftAmount) > 0) {
					return "退款金额超出可退款金额";
				}
			} else {
				refundAmount = leftAmount;
			}

			// 创建退款订单
			refundOrderPay = new OrderPayRefund();
			refundOrderPay.setUserId(orderPay.getUserId());
			refundOrderPay.setBusinessType(payRefundReq.getBusinessType().getCode());
			refundOrderPay.setMethod(orderPay.getMethod());
			refundOrderPay.setOrderCode(outTradeNo);
			refundOrderPay.setRefundOrderCode(refundOrderCode);
			refundOrderPay.setAmount(orderPay.getAmount());
			refundOrderPay.setRefundAmount(refundAmount);
			refundOrderPay.setStatus(OrderPayRefundEnum.Status.WAIT_APPLY.getCode());
			refundOrderPay.setRemark(payRefundReq.getRefundRemark());
			refundOrderPay.setNotifyUrl(payRefundReq.getNotifyUrl());
			refundOrderPay.setNotifyAttach(payRefundReq.getAttach());
			orderPayRefundService.save(refundOrderPay);
			return null;
		});

		if (message != null) {
			ExceptionUtil.throwException(message);
		}

		// 异步处理 ========> 逻辑上等同于直接调用 refundWithRetry
		RefundReq refundReq = new RefundReq();
		refundReq.setRefundOrderCode(refundOrderCode);

		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder().feignUrl(NOTIFY_URL_REFUND).jsonParams(refundReq)
				.build();
		retryerFeign.call(retryerInfoReq);

		return null;
	}

	/**
	 * 退款处理(使用restTemplate的方式调用)
	 *
	 * @param refundReq
	 * @return
	 */
	@PostMapping("/refundWithRetry")
	public RetryerResp refundWithRetry(@RequestBody RefundReq refundReq) {
		OrderPayRefund orderPayRefund = orderPayRefundService.selectByRefundOrderCode(refundReq.getRefundOrderCode());
		if (orderPayRefund == null) {
            return RetryerResp.goon("数据不存在");
		}

		String outTradeNo = orderPayRefund.getOrderCode();
		BigDecimal refundAmount = orderPayRefund.getRefundAmount().setScale(2, BigDecimal.ROUND_HALF_UP);// 向上取整，保留2位小数

		PayClient payClient = PayFactory.of(OrderPayEnum.Method.of(orderPayRefund.getMethod()));
		PayRefundResp payRefundResp = payClient.refund(outTradeNo, orderPayRefund.getOrderCode(), refundAmount);

		if (!payRefundResp.getSuccess()) {
            return RetryerResp.goon(payRefundResp.getMessage());
		}

		refundResult(refundReq.getRefundOrderCode());// 主动查询退款结果

		return RetryerResp.end();
	}

	/**
	 * 退款结果查询
	 *
	 * @param refundOrderCode
	 *            退款订单号
	 */
	private void refundResult(String refundOrderCode) {
		// 异步处理 ========> 逻辑上等同于直接调用 pollingRefundResult
		RefundResultReq refundResultReq = new RefundResultReq();
		refundResultReq.setRefundOrderCode(refundOrderCode);
		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder().feignUrl(NOTIFY_URL_REFUNDRESULT)
				.jsonParams(refundResultReq).maxFailure(15/* 预计总时长9小时 */).build();
		retryerFeign.call(retryerInfoReq);
	}

	/**
	 * <pre>
	 * 轮询退款结果(使用restTemplate的方式调用)
	 * 注：需与第三方回调做好并发/幂等处理
	 * </pre>
	 *
	 * @param refundResultReq
	 * @return
	 */
	@PostMapping("/pollingRefundResult")
	public RetryerResp pollingRefundResult(@RequestBody @Valid RefundResultReq refundResultReq) {
		String refundOrderCode = refundResultReq.getRefundOrderCode();

		OrderPayRefund orderPayRefund = orderPayRefundService.selectByRefundOrderCode(refundOrderCode);
		if (orderPayRefund == null) {
            return RetryerResp.goon("数据不存在");
		}
		OrderPayRefundEnum.Status status = OrderPayRefundEnum.Status.of(orderPayRefund.getStatus());
		if (status != OrderPayRefundEnum.Status.WAIT_APPLY) {
			// 订单不是未退款状态，结束查询
            return RetryerResp.end("订单不是未退款状态，结束查询");
		}

		PayClient payClient = PayFactory.of(OrderPayEnum.Method.of(orderPayRefund.getMethod()));
		PayRefundQueryResp payRefundQuery = payClient.refundQuery(refundOrderCode);
		if (!payRefundQuery.getResult()) {
            return RetryerResp.goon(payRefundQuery.getMessage());
		}
		if (!payRefundQuery.getRefundSuccess()) {
			// 有结果但不是退款成功，结束查询
            return RetryerResp.end("有结果但不是退款成功，结束查询");
		}

		// MQ异步处理
		Map<String, Object> params = Maps.newHashMap();
		params.put("outTradeNo", orderPayRefund.getOrderCode());
		params.put("outRefundNo", refundOrderCode);

		boolean success = payRefundQuery.getRefundSuccess();
		params.put("success", success);
		if (!success) {
			params.put("message", payRefundQuery.getMessage());
		}

		//财务流水信息
		params.put("amount", payRefundQuery.getRefundAmount().toPlainString());
		params.put("orderPayMethod", orderPayRefund.getMethod());
		params.put("merchantNo", payRefundQuery.getMerchantNo());
		params.put("tradeNo", payRefundQuery.getTradeNo());

		messageSender.sendNormalMessage(StrategyConstants.REFUND_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				messagedrivenProperties.getQueue().getCommon().getKey());

        return RetryerResp.end("退款成功");
	}

}
