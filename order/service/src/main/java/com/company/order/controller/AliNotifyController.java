package com.company.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.framework.util.JsonUtil;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.AliNotifyFeign;
import com.company.order.entity.AliPay;
import com.company.order.entity.AliPayRefund;
import com.company.order.entity.PayNotify;
import com.company.order.mapper.AliPayMapper;
import com.company.order.mapper.AliPayRefundMapper;
import com.company.order.mapper.PayNotifyMapper;
import com.company.order.messagedriven.Constants;
import com.company.order.messagedriven.strategy.StrategyConstants;
import com.company.order.pay.ali.AliConstants;
import com.company.order.pay.ali.config.AliPayConfiguration;
import com.company.order.pay.ali.config.AliPayProperties;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/alinotify")
public class AliNotifyController implements AliNotifyFeign {

	@Autowired
	private AliPayMapper aliPayMapper;

	@Autowired
	private AliPayRefundMapper aliPayRefundMapper;

	@Autowired
	private PayNotifyMapper payNotifyMapper;

	@Autowired
	private MessageSender messageSender;
	@Autowired
	private MessagedrivenProperties messagedrivenProperties;

	@Autowired
	private AliPayConfiguration aliPayConfiguration;

	@Override
	public Map<String, String> aliPayNotify(@RequestBody Map<String, String> aliParams) {
		String notifyData = JsonUtil.toJsonString(aliParams);
		// 记录原始数据
		log.info("ali notify data:{}", notifyData);
		PayNotify payNotify = new PayNotify().setMethod(OrderPayEnum.Method.ALI.getCode()).setNotifyData(notifyData);
		payNotifyMapper.insert(payNotify);

		String appid = aliParams.get("app_id");
		AliPayProperties.PayConfig payConfig = aliPayConfiguration.getPayConfig(appid);
		try {
			// 调用SDK验证签名
			boolean signVerified = AlipaySignature.rsaCheckV1(aliParams, payConfig.getPubKey(),
					AliConstants.CHARSET, AliConstants.SIGNTYPE);
			if (!signVerified) {
				payNotifyMapper.updateRemarkById("验签失败", payNotify.getId());
				return Collections.singletonMap("value", "fail");
			}
		} catch (AlipayApiException e) {
			log.error(">>>解析支付宝回调参数异常，直接返回", e);
			payNotifyMapper.updateRemarkById(e.getMessage(), payNotify.getId());
            return Collections.singletonMap("value", "fail");
		}

		String tradeStatus = aliParams.get("trade_status");
		String outTradeNo = aliParams.get("out_trade_no");

		if (StringUtils.isNotBlank(aliParams.get("out_biz_no"))) {
			// 如果out_biz_no不为空，则认为是退款回调
			/**
			 * <pre>
			全额退款：
			{
				"gmt_create": "2020-12-28 20:46:00",
				"charset": "UTF-8",
				"seller_email": "485564455@qq.com",
				"subject": "订单-205546307409489920",
				"sign": "gGnICAIp4OyAlnqWgESVFGlPPRfg6NDwblVvC7eZAmnJOBHZKb5OodZHfSs96NM2ff+4sKex5GF6czpzJGZ1VZiYehpZHb6bnoZfRFA5KkoNj5mMkSfzdmnjj/Mmzu5aSz/qUwbtOhF9wR4MUaI1W7xURJNKantewlSRnRAmAbweJgMMVBvNYpGsOkl6wzkXW9GEZPXFQb+NTZeuFcKpPkDurOAGU0aX9YpjE1ouaCxNYqTdsV7+FqTJDaQeYdLWquOFJnDuvxNiC5AOC0m3H19ud8DDvgJi2fvGNkYJ+SlRpPL6sz70dL2HiugnfBEgV0EQJaTTLPiU344BtY68Zg==",
				"body": "app支付",
				"buyer_id": "4011402911110921",
				"notify_id": "2020122800222204607000921452504952",
				"notify_type": "trade_status_sync",
				"trade_status": "TRADE_CLOSED",
				"app_id": "3021451196634505",
				"sign_type": "RSA2",
				"seller_id": "2088923443064778",
				"gmt_payment": "2020-12-28 20:46:00",
				"notify_time": "2020-12-28 20:46:07",
				"gmt_refund": "2020-12-28 20:46:06.659",
				"out_biz_no": "m7X2JM0ohj4aR",
				"version": "1.0",
				"out_trade_no": "205546307409489920",
				"total_amount": "0.01",
				"refund_fee": "0.01",
				"trade_no": "2020122822001400921446208697",
				"auth_app_id": "3021451196634505",
				"gmt_close": "2020-12-28 20:46:06",
				"buyer_logon_id": "187***@139.com"
			}
			部分退款：
			{
				"gmt_create": "2021-12-16 14:57:36",
				"charset": "UTF-8",
				"gmt_payment": "2021-12-16 14:57:37",
				"seller_email": "jqd@163.com",
				"notify_time": "2021-12-16 14:59:30",
				"subject": "订单-333381570281132032",
				"gmt_refund": "2021-12-16 14:59:30.378",
				"sign": "gJcVAwky9Ad02uQsTrS/6pyMflK/gasGFpCB8Jp6RQzr7DnCbzXTzXG17ZrryFqg4gmGe9IwzHr6rxaLV7HyFN29VdUSEwCrG17Kpz3f/avbwctT/hahoz+1prxSmzGmSVCM6QZzIST549wjEYPDyoyDt99UiN4USi3xEeqg/gQggYWm5KyjuttSuNTbRa5zjZUJQtMcXauQvgkM9cGrmJMFyvDcBNegki9drPsEZE9m7Cvm+bf2xZ3UtgkRlJYM7ZhDNgVIDa3IMGRBN6XN5h0+Y5rUA+iEqvXpgqMfBPGAG7K43cMTNFcdZsfFiXxIT15VQm77ENek+PxdymHkZQ==",
				"body": "权益商品购买",
				"buyer_id": "2088412687134912",
				"out_biz_no": "333381966069309440",
				"version": "1.0",
				"notify_id": "2021121600222145930034911423265143",
				"notify_type": "trade_status_sync",
				"out_trade_no": "333381570281132032",
				"total_amount": "0.02",
				"trade_status": "TRADE_SUCCESS",
				"refund_fee": "0.01",
				"trade_no": "2021121622001434911451728350",
				"auth_app_id": "3021451196634505",
				"buyer_logon_id": "152****3215",
				"app_id": "3021451196634505",
				"sign_type": "RSA2",
				"seller_id": "2088923443064778"
			}
			 * </pre>
			 */

			// 回调数据落库
			String outRequestNo = aliParams.get("out_biz_no");
			AliPayRefund aliPayRefund4Update = new AliPayRefund().setRefundStatus(AliConstants.RefundStatus.REFUND_SUCCESS)
					.setTradeNo(aliParams.get("trade_no"));

			UpdateWrapper<AliPayRefund> wrapper = new UpdateWrapper<AliPayRefund>();
			wrapper.eq("out_request_no", outRequestNo);
			wrapper.and(i -> i.isNull("refund_status")
					.or(i2 -> i2.ne("refund_status", AliConstants.RefundStatus.REFUND_SUCCESS)));
			int affect = aliPayRefundMapper.update(aliPayRefund4Update, wrapper);
			if (affect == 0) {
				// 订单回调已处理完成，无需重复处理
				payNotifyMapper.updateRemarkById("订单回调已处理完成，无需重复处理", payNotify.getId());
                return Collections.singletonMap("value", "success");
			}

			// MQ异步处理
			Map<String, Object> params = Maps.newHashMap();
			params.put("outTradeNo", outTradeNo);
			params.put("outRefundNo", outRequestNo);
			params.put("success", true);

			//财务流水信息
			params.put("amount", aliParams.get("refund_fee"));
			params.put("orderPayMethod", OrderPayEnum.Method.ALI.getCode());
			params.put("merchantNo", aliParams.get("seller_id"));
			params.put("tradeNo", aliParams.get("trade_no"));

			messageSender.sendNormalMessage(StrategyConstants.REFUND_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
					messagedrivenProperties.getQueue().getCommon().getKey());
		} else if ("TRADE_SUCCESS".equals(tradeStatus)) {
			// trade_status=TRADE_SUCCESS，则认为是支付成功回调
			/**
			 * <pre>
			{
				"gmt_create": "2020-12-28 20:46:00",
				"charset": "UTF-8",
				"seller_email": "485564455@qq.com",
				"subject": "订单-205546307409489920",
				"sign": "bTcikFIOpTcdY1TAv9+g4dy+7ctVpcJmW/7SxrE80ZgG1uXGvj/6JH+tMtpkIxxDl2Wzz/NRq82cACVfDO1WIjeRWhCSx/QKASBijrnLDuUP9LJxCNQlGVUjW3fk9N1GzE8NaCeedpyUHX4ldNW3T3F8t+vcQSF9v6uSucNDFJpdIo/10YT9l/x/5voV82+oFJGFEWYXhT2IyWFspcCD0Wn/5wRuoR67IWDTgzm+w+jF9++XhfNY2mTKcbfvQZCb6B6sQA/SkCLY4ErY7ZkIwlQ70JgdyR3A/adVrbzhkiwszn8B6gTkgbx6hxQSr3AU61V3PjjQfFL1JN7DCMLlTA==",
				"body": "app支付",
				"buyer_id": "4011402911110921",
				"invoice_amount": "0.01",
				"notify_id": "2020122800222204600000921452547067",
				"fund_bill_list": "[{\"amount\":\"0.01\",\"fundChannel\":\"PCREDIT\"}]",
				"notify_type": "trade_status_sync",
				"trade_status": "TRADE_SUCCESS",
				"receipt_amount": "0.01",
				"app_id": "3021451196634505",
				"buyer_pay_amount": "0.01",
				"sign_type": "RSA2",
				"seller_id": "2088923443064778",
				"gmt_payment": "2020-12-28 20:46:00",
				"notify_time": "2020-12-28 20:46:01",
				"version": "1.0",
				"out_trade_no": "205546307409489920",
				"total_amount": "0.01",
				"trade_no": "2020122822001400921446208697",
				"auth_app_id": "3021451196634505",
				"buyer_logon_id": "187***@139.com",
				"point_amount": "0.00"
			}
			 * </pre>
			 */

			// 回调数据落库
			AliPay aliPay4Update = new AliPay().setTradeStatus(tradeStatus).setTradeNo(aliParams.get("trade_no"))
					.setGmtPayment(aliParams.get("gmt_payment"));

			UpdateWrapper<AliPay> wrapper = new UpdateWrapper<AliPay>();
			wrapper.eq("out_trade_no", outTradeNo);
			wrapper.and(i -> i.isNull("trade_status")
					.or(i2 -> i2.ne("trade_status", AliConstants.TradeStatus.TRADE_SUCCESS)));
			int affect = aliPayMapper.update(aliPay4Update, wrapper);
			if (affect == 0) {
				// 订单回调已处理完成，无需重复处理
				payNotifyMapper.updateRemarkById("订单回调已处理完成，无需重复处理", payNotify.getId());
                return Collections.singletonMap("value", "success");
			}

			// MQ异步处理
			Map<String, Object> params = Maps.newHashMap();
			params.put("outTradeNo", outTradeNo);

			params.put("time", aliParams.get("gmt_payment"));

			//财务流水信息
			params.put("amount", aliParams.get("total_amount"));
			params.put("orderPayMethod", OrderPayEnum.Method.ALI.getCode());
			params.put("merchantNo", aliParams.get("seller_id"));
			params.put("tradeNo", aliParams.get("trade_no"));

			messageSender.sendNormalMessage(StrategyConstants.PAY_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
					Constants.QUEUE.PAY_NOTIFY.KEY);
		}
        return Collections.singletonMap("value", "success");
	}
}
