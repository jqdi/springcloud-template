package com.company.order.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.PropertyUtils;
import com.company.framework.util.RetryUtils;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.AliActivityNotifyFeign;
import com.company.order.api.response.SpiOrderSendNotifyResp;
import com.company.order.api.response.SpiOrderSendNotifyResp.Response.SendActivityInfoResultList;
import com.company.order.api.response.SpiOrderSendNotifyResp.Response.SendActivityInfoResultList.SendVoucherInfoResult;
import com.company.order.entity.AliActivityNotify;
import com.company.order.entity.AliActivityPay;
import com.company.order.enums.AliActivityNotifyEnum;
import com.company.order.mapper.AliActivityNotifyMapper;
import com.company.order.mapper.AliActivityPayMapper;
import com.company.order.messagedriven.Constants;
import com.company.order.messagedriven.strategy.StrategyConstants;
import com.company.order.pay.aliactivity.AliActivityConstants;
import com.company.order.pay.aliactivity.config.AliActivityPayConfiguration;
import com.company.order.pay.aliactivity.config.AliActivityPayProperties;
import com.company.order.pay.aliactivity.dto.SendActivityInfoList;
import com.company.order.pay.aliactivity.notify.FromMessage;
import com.company.order.pay.aliactivity.notify.FromMessageBeanFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/aliactivitynotify")
public class AliActivityNotifyController implements AliActivityNotifyFeign {
	@Autowired
	private AliActivityNotifyMapper aliActivityNotifyMapper;

	@Autowired
	private AliActivityPayMapper aliActivityPayMapper;

	@Autowired
	private MessageSender messageSender;
	@Autowired
	private MessagedrivenProperties messagedrivenProperties;

	@Autowired
	private AliActivityPayConfiguration aliActivityPayConfiguration;

	@Value("${aliactivitynotify.useVoucherCodeUrl:false}")
	private Boolean useVoucherCodeUrl;

	@Value("${aliactivitynotify.merchantOrderUrl.page.prefix:pagesMember/myOrder/order_detail?orderId=}")
	private String merchantOrderUrlPagePrefix;

	/**
	 * <pre>
	 * 官方文档：https://opendocs.alipay.com/pre-open/02cqrw
	 * 支付宝开放平台设置：API管理-实现官方标准API-(专享产品-商家兑换券)-去开发-官方定义的标准API列表（SPI）
	 * </pre>
	 */
	@Override
	public SpiOrderSendNotifyResp spiOrderSendNotify(@RequestBody Map<String, String> aliParams) {
		/**
		 * <pre>
		 *
		{
			"order_no": "2015042321001004720200028594",
			"charset": "UTF-8",
			"method": "spi.alipay.marketing.activity.order.send",
			"utc_timestamp": "1672221612",
			"sign": "dzqCc/jxlN7YM2NK0jDaTcbaqq8eV9ainG2xpudG9zsDrw28NgW5aACZZzizsiBKRzX60wS7fwR4h+L9k28zphiC0+8ym0tEUFv1pMUSVxm9wwjtdIqeyXuY0MdIQ+p7DhfV9lkZQyxDaF0mi9an5ek/j3qPUMvAb2Vv7Y676dJhiWnx7QCIPf6+7V2yWYlVenOItyVgsHL+octOOeiTTw8d6h/TZX/HkmRPaeOmyxbTpAL3tUUveujBhovxuCgvLJvvUy6Sr0qG9Y8EYO42BCDwnsCtWH59LLbdOpiwOIXhKmtjtMj5/JT+QxAovzgLH3zWER/WQ8XW0Q+C4YZSIw==",
			"out_order_no": "201234354323423",
			"buyer_id": "2088xxxx",
			"version": "1.0",
			"sign_type": "RSA2",
			"send_activity_info_list": "[{\"quantity\":2,\"activity_id\":\"2016042700826004508401111111\"}]"
		}
		 * </pre>
		 */
		String notifyData = JsonUtil.toJsonString(aliParams);
		// 记录原始数据
		log.info("ali notify data:{}", notifyData);
		AliActivityNotify aliActivityNotify = new AliActivityNotify().setMethod(AliActivityNotifyEnum.Method.SPIORDERSEND.getCode()).setNotifyData(notifyData);
		aliActivityNotifyMapper.insert(aliActivityNotify);

		Integer payNotifyId = aliActivityNotify.getId();

		String appid = "2021003111112476";

		AliActivityPayProperties.PayConfig payConfig = aliActivityPayConfiguration.getPayConfig(appid);
		try {
			// 调用SDK验证签名
			boolean signVerified = AlipaySignature.rsaCheckV1(aliParams, payConfig.getPubKey(),
					AliActivityConstants.CHARSET, AliActivityConstants.SIGNTYPE);
			if (!signVerified) {
				aliActivityNotifyMapper.updateRemarkById("验签失败", payNotifyId);

				SpiOrderSendNotifyResp resp = new SpiOrderSendNotifyResp();

				SpiOrderSendNotifyResp.Response response = new SpiOrderSendNotifyResp.Response();
				response.setCode("40004");
				response.setMsg("Business Failed");
				response.setSub_code("ISV-VERIFICATION-FAILED");
				response.setSub_msg("验签失败");
				resp.setResponse(response);
				resp.setSign(sign(response, payConfig.getPrivateKey()));

				return resp;
			}
		} catch (AlipayApiException e) {
			log.error(">>>解析支付宝回调参数异常，直接返回", e);
			aliActivityNotifyMapper.updateRemarkById(e.getMessage(), payNotifyId);

			SpiOrderSendNotifyResp resp = new SpiOrderSendNotifyResp();
			SpiOrderSendNotifyResp.Response response = new SpiOrderSendNotifyResp.Response();
			response.setCode("40004");
			response.setMsg("Business Failed");
			response.setSub_code("ISV-VERIFICATION-FAILED");
			response.setSub_msg("验签失败");
			resp.setResponse(response);
			resp.setSign(sign(response, payConfig.getPubKey()));

			return resp;
		}

		// 回调数据落库
		String orderNo = aliParams.get("order_no");
		String outOrderNo = aliParams.get("out_order_no");
		String buyerId = aliParams.get("buyer_id");

		String send_activity_info_list = aliParams.get("send_activity_info_list");
		List<SendActivityInfoList> sendActivityInfoList = JsonUtil.toList(send_activity_info_list,
				SendActivityInfoList.class);
		// 活动应该只有1个元素，如果有多个与响应值对不上
		SendActivityInfoList sendActivityInfo = sendActivityInfoList.get(0);
		String activityId = sendActivityInfo.getActivityId();

		AliActivityPay aliActivityPay4Update = new AliActivityPay().setTradeStatus(AliActivityConstants.TradeStatus.TRADE_SUCCESS)
				.setOrderNo(orderNo).setGmtPayment(DateUtil.formatDateTime(new Date()));

		UpdateWrapper<AliActivityPay> wrapper = new UpdateWrapper<AliActivityPay>();
		wrapper.eq("out_order_no", outOrderNo);
		wrapper.and(i -> i.isNull("trade_status")
				.or(i2 -> i2.ne("trade_status", AliActivityConstants.TradeStatus.TRADE_SUCCESS)));
		int affect = aliActivityPayMapper.update(aliActivityPay4Update, wrapper);
		if (affect == 0) {
			// 订单回调已处理完成，无需重复处理，可直接查询券码
			aliActivityNotifyMapper.updateRemarkById("订单回调已处理完成，无需重复处理", payNotifyId);

			// 查询券码，查到的情况下直接返回响应给支付宝（需要重试机制）
			SpiOrderSendNotifyResp resp = new SpiOrderSendNotifyResp();
			SpiOrderSendNotifyResp.Response response = retryToResponse(outOrderNo, orderNo, buyerId, activityId);
			resp.setResponse(response);
			resp.setSign(sign(response, payConfig.getPubKey()));

			String responseMsg = Optional.ofNullable(response.getSub_msg()).orElse(response.getMsg());
			aliActivityNotifyMapper.updateRemarkById(responseMsg, payNotifyId);

			return resp;
		}

		// MQ异步处理
		Map<String, Object> params = Maps.newHashMap();
		params.put("outOrderNo", outOrderNo);

		LocalDateTime time = LocalDateTime.now();
		params.put("time", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		AliActivityPay aliActivityPay = aliActivityPayMapper.selectByOutOrderNo(outOrderNo);

		// 财务流水信息
		params.put("amount", aliActivityPay.getTotalAmount());
		params.put("orderPayMethod", OrderPayEnum.Method.ALIACTIVITY.getCode());
		params.put("merchantNo", "未配置");
		params.put("tradeNo", aliActivityPay.getTradeNo());

		messageSender.sendNormalMessage(StrategyConstants.PAY_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				Constants.QUEUE.PAY_NOTIFY.KEY);

		// 支付成功后回调，开始异步发码，最终保存到券码表，在下面的代码需要轮询券码表获取券码响应给支付宝（需要重试机制）
		SpiOrderSendNotifyResp resp = new SpiOrderSendNotifyResp();
		SpiOrderSendNotifyResp.Response response = retryToResponse(outOrderNo, orderNo, buyerId, activityId);
		resp.setResponse(response);
		resp.setSign(sign(response, payConfig.getPubKey()));

		String responseMsg = Optional.ofNullable(response.getSub_msg()).orElse(response.getMsg());
		aliActivityNotifyMapper.updateRemarkById(responseMsg, payNotifyId);

		return resp;
	}

	/**
	 * 生成签名
	 *
	 * @param response
	 * @param privateKey
	 * @return
	 */
	@SneakyThrows
	private static String sign(SpiOrderSendNotifyResp.Response response, String privateKey) {
		@SuppressWarnings("unchecked")
		Map<String, String> responseMap = PropertyUtils.copyProperties(response, Map.class);
		responseMap.put("send_activity_info_result_list", JsonUtil.toJsonString(response.getSend_activity_info_result_list()));
		String sign = AlipaySignature.sign(responseMap, privateKey, AliActivityConstants.CHARSET, AliActivityConstants.SIGNTYPE);
		return sign;
	}

	private static SpiOrderSendNotifyResp.Response error(String subCode, String subMsg) {
		SpiOrderSendNotifyResp.Response response = new SpiOrderSendNotifyResp.Response();
		response.setCode(AliActivityConstants.Response.CODE_40004);
		response.setMsg(AliActivityConstants.Response.MSG_BUSINESS_FAILED);
		response.setSub_code(subCode);
		response.setSub_msg(subMsg);
		return response;
	}

	/**
	 * 支付宝API超时时间：3秒，尽量保留一些时间，返回error的情况下支付宝会重试10次
	 */
	private SpiOrderSendNotifyResp.Response retryToResponse(String outOrderNo, String orderNo, String buyerId, String activityId) {
		SpiOrderSendNotifyResp.Response response = RetryUtils.retry(() -> {
			return toResponse(outOrderNo, orderNo, buyerId, activityId);
		}, null, 300, 5);// 300毫秒后重试，总共重试5次

		if (response == null) {
			response = error("SYSTEM_ERROR", "未获取到券码");
		}
		return response;
	}

	/**
	 * <pre>
	{
		"response": {
			"code": "10000",
			"msg": "Success",
			"order_no": "2015042321001004720200028594",
			"buyer_id": "2088xxxx",
			"custom_send_time": "2017-01-01 00:00:01",
			"send_activity_info_result_list": {
				"activity_id": "2016042700826004508401111111",
				"send_voucher_info_result_list": [
					{
						"voucher_code": "123AB",
						"voucher_code_url": "alipays://platformapi/startapp?appId=XXX",
						"merchant_order_url": "alipays://platformapi/startapp?appId=XXX"
					}
				]
			}
		},
		"sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
	}
	 * </pre>
	 */
	private SpiOrderSendNotifyResp.Response toResponse(String outOrderNo, String orderNo, String buyerId, String activityId) {
		// 根据outOrderNo查询到券码
		List<String> aliActivityVoucherCodeList = Lists.newArrayList();
		if (CollectionUtils.isEmpty(aliActivityVoucherCodeList)) {
			// 查不到券码返回null进行重试
			return null;
		}

		SpiOrderSendNotifyResp.Response response = new SpiOrderSendNotifyResp.Response();
		response.setCode(AliActivityConstants.Response.CODE_SUCCESS);
		response.setMsg(AliActivityConstants.Response.MSG_SUCCESS);
		response.setOrder_no(orderNo);
		response.setBuyer_id(buyerId);
		response.setCustom_send_time(DateUtil.formatDateTime(new Date()));

		SendActivityInfoResultList sendActivityInfoResultList = new SendActivityInfoResultList();
		sendActivityInfoResultList.setActivity_id(activityId);

		List<SendVoucherInfoResult> sendVoucherInfoResultList = aliActivityVoucherCodeList.stream().map(v -> {
			SendVoucherInfoResult sendVoucherInfoResult = new SendVoucherInfoResult();
			sendVoucherInfoResult.setVoucher_code(v);
			if (useVoucherCodeUrl) {
				/**
				 * <pre>
				 * 这里测试了支付宝会使用该链接去生成二维码，所以不能传voucherCodeUrl（怀疑是支付宝的bug，确认修复后可放开）
				 * 不传的情况下支付宝会使用VoucherCode去生成二维码
				 * </pre>
				 */
				sendVoucherInfoResult.setVoucher_code_url("填写券码对应的二维码");
			}
			String appid = "2021003111112476";
			/**
			 * <pre>
			 * 支付宝直接跳转到小程序订单详情页或券码详情页（可配置，需要UrlEncode编码）
			 * 订单详情页：alipays://platformapi/startapp?appId=2021003111112476&page=pagesMember/myOrder/order_detail?orderId=82924417
			 * 券码详情页：alipays://platformapi/startapp?appId=2021003111112476&page=pagesMember/myOrder/cardQR?id=82923939
			 * </pre>
			 */
			String merchantOrderUrl = "alipays://platformapi/startapp?appId=" + appid + "&page="
					+ URLUtil.encodeAll(merchantOrderUrlPagePrefix) + v;
			sendVoucherInfoResult.setMerchant_order_url(merchantOrderUrl);
			return sendVoucherInfoResult;
		}).collect(Collectors.toList());

		sendActivityInfoResultList.setSend_voucher_info_result_list(sendVoucherInfoResultList);

		response.setSend_activity_info_result_list(sendActivityInfoResultList);
		return response;
	}

	/**
	 * <pre>
	 * 官方文档：https://opendocs.alipay.com/common/02km9j
	 * 支付宝开放平台设置：开发设置-消息服务-FROM平台
	 * 支付宝开放平台设置：开发设置-开发信息-应用网关
	 * </pre>
	 */
	@Override
	public Map<String, String> fromNotify(@RequestBody Map<String, String> aliParams) {
		/**
		 * <pre>
		{
			"notify_id": "2020122800222204607000921452504952",
			"utc_timestamp": "1514210452731",
			"msg_method": "alipay.marketing.activity.ordermessage.received",
			"app_id": "3021451196634505",
			"msg_type": "sys",
			"msg_uid": "2088102165945162",
			"biz_content": "{\"id\":\"ORDERSEND_2021042400826001508407723739\",\"order_no\":\"2014081801502300000000140007771420\",\"out_order_no\":\"20123214235435\",\"event_time\":\"12342425435232423\",\"send_status\":\"SUCCESS\"}",
			"sign": "gGnICAIp4OyAlnqWgESVFGlPPRfg6NDwblVvC7eZAmnJOBHZKb5OodZHfSs96NM2ff+4sKex5GF6czpzJGZ1VZiYehpZHb6bnoZfRFA5KkoNj5mMkSfzdmnjj/Mmzu5aSz/qUwbtOhF9wR4MUaI1W7xURJNKantewlSRnRAmAbweJgMMVBvNYpGsOkl6wzkXW9GEZPXFQb+NTZeuFcKpPkDurOAGU0aX9YpjE1ouaCxNYqTdsV7+FqTJDaQeYdLWquOFJnDuvxNiC5AOC0m3H19ud8DDvgJi2fvGNkYJ+SlRpPL6sz70dL2HiugnfBEgV0EQJaTTLPiU344BtY68Zg==",
			"sign_type": "RSA2",
			"encrypt_type": "AES",
			"charset": "UTF-8",
			"notify_type": "trade_status",
			"notify_time": "2020-12-28 20:46:07",
			"auth_app_id": "3021451196634505"
		}
		 * </pre>
		 */
		String notifyData = JsonUtil.toJsonString(aliParams);
		// 记录原始数据
		log.info("ali notify data:{}", notifyData);
		AliActivityNotify aliActivityNotify = new AliActivityNotify().setMethod(AliActivityNotifyEnum.Method.FROM.getCode()).setNotifyData(notifyData);
		aliActivityNotifyMapper.insert(aliActivityNotify);

		String appid = aliParams.get("app_id");

		AliActivityPayProperties.PayConfig payConfig = aliActivityPayConfiguration.getPayConfig(appid);
		try {
			// 调用SDK验证签名
			boolean signVerified = AlipaySignature.rsaCheckV1(aliParams, payConfig.getPubKey(),
					AliActivityConstants.CHARSET, AliActivityConstants.SIGNTYPE);
			if (!signVerified) {
				aliActivityNotifyMapper.updateRemarkById("验签失败", aliActivityNotify.getId());
				return Collections.singletonMap("value", "fail");
			}
		} catch (AlipayApiException e) {
			log.error(">>>解析支付宝回调参数异常，直接返回", e);
			aliActivityNotifyMapper.updateRemarkById(e.getMessage(), aliActivityNotify.getId());
			return Collections.singletonMap("value", "fail");
		}

		String msgMethod = aliParams.get("msg_method");
		FromMessage fromMessage = FromMessageBeanFactory.of(msgMethod);
		fromMessage.handle(aliActivityNotify.getId(), aliParams);

        return Collections.singletonMap("value", "success");
	}
}
