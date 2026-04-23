package com.company.order.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.WxNotifyFeign;
import com.company.order.entity.PayNotify;
import com.company.order.entity.WxPay;
import com.company.order.entity.WxPayRefund;
import com.company.order.mapper.PayNotifyMapper;
import com.company.order.mapper.WxPayMapper;
import com.company.order.mapper.WxPayRefundMapper;
import com.company.order.messagedriven.Constants;
import com.company.order.messagedriven.strategy.StrategyConstants;
import com.company.order.pay.wx.config.WxPayConfiguration;
import com.company.order.pay.wx.config.WxPayProperties;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult.ReqInfo;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.util.SignUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/wxnotify")
public class WxNotifyController implements WxNotifyFeign {

	@Autowired
	private WxPayMapper wxPayMapper;
	@Autowired
	private WxPayRefundMapper wxPayRefundMapper;

	@Autowired
	private PayNotifyMapper payNotifyMapper;

	@Autowired
	private MessageSender messageSender;
	
	@Autowired
	private MessagedrivenProperties messagedrivenProperties;

	@Autowired
	private WxPayConfiguration wxPayConfiguration;
	
	/**
	 * 微信只有支付成功才会回调
	 */
	@Override
	public Map<String, String> wxPayNotify(@RequestBody String xmlString) {
		/**
		 * <pre>
		 * 官方文档：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7&index=8
		<xml>
		  <appid><![CDATA[wx8888888888888888]]></appid>
		  <bank_type><![CDATA[OTHERS]]></bank_type>
		  <cash_fee><![CDATA[2500]]></cash_fee>
		  <fee_type><![CDATA[CNY]]></fee_type>
		  <is_subscribe><![CDATA[N]]></is_subscribe>
		  <mch_id><![CDATA[1900000109]]></mch_id>
		  <nonce_str><![CDATA[cg2gzMG24010243l]]></nonce_str>
		  <openid><![CDATA[oQvXm5SsVx8OjLaZ_zpDJB1rSMss]]></openid>
		  <out_trade_no><![CDATA[203783232649990144]]></out_trade_no>
		  <result_code><![CDATA[SUCCESS]]></result_code>
		  <return_code><![CDATA[SUCCESS]]></return_code>
		  <sign><![CDATA[FCCE0E03BA54987B4BEEEE90DA9361AE]]></sign>
		  <time_end><![CDATA[20201224000006]]></time_end>
		  <total_fee>2500</total_fee>
		  <trade_type><![CDATA[JSAPI]]></trade_type>
		  <transaction_id><![CDATA[4200000808202012244418326253]]></transaction_id>
		</xml>
		 * </pre>
		 */

		// 记录原始数据
		log.info("wx notify data:{}", xmlString);
		PayNotify payNotify = new PayNotify().setMethod(OrderPayEnum.Method.WX.getCode()).setNotifyData(xmlString);
		payNotifyMapper.insert(payNotify);

		// 解析微信支付返回报文
		WxPayOrderNotifyResult orderNotifyResult = WxPayOrderNotifyResult.fromXML(xmlString);

		String outTradeNo = orderNotifyResult.getOutTradeNo();
		if (StringUtils.isBlank(outTradeNo)) {
			payNotifyMapper.updateRemarkById("缺少out_trade_no", payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("缺少out_trade_no"));
		}

		WxPay wxPay = wxPayMapper.selectByOutTradeNo(outTradeNo);

		WxPayProperties.MchConfig mchConfig = wxPayConfiguration.getMchConfig(wxPay.getMchid());
		String mchKey = mchConfig.getMchKey();
		
		// 校验返回结果签名
		Map<String, String> map = orderNotifyResult.toMap();
		if (!SignUtils.checkSign(map, null, mchKey)) {
			payNotifyMapper.updateRemarkById("参数格式校验错误！", payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("参数格式校验错误！"));
        }

		// 校验返回的订单金额是否与商户侧的订单金额一致
		if (!Objects.equals(wxPay.getTotalFee(), orderNotifyResult.getTotalFee())) {
			payNotifyMapper.updateRemarkById("订单金额不一致", payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("订单金额不一致"));
		}

		String returnCode = orderNotifyResult.getReturnCode();
		if (!Objects.equals(returnCode, WxPayConstants.ResultCode.SUCCESS)) {
			String message = orderNotifyResult.getReturnMsg();
			log.warn("returnCode不是SUCCESS:{},{}", returnCode, message);
			payNotifyMapper.updateRemarkById(message, payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("returnCode不是SUCCESS"));
		}
		
		String resultCode = orderNotifyResult.getResultCode();
		if (!Objects.equals(resultCode, WxPayConstants.ResultCode.SUCCESS)) {
			String message = orderNotifyResult.getErrCodeDes();
			log.warn("resultCode不是SUCCESS:{},{}", resultCode, message);
			payNotifyMapper.updateRemarkById(message, payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("resultCode不是SUCCESS"));
		}
		
		// 回调数据落库
		WxPay wxPay4Update = new WxPay().setTradeState(WxPayConstants.WxpayTradeStatus.SUCCESS)
				.setTransactionId(orderNotifyResult.getTransactionId()).setTimeEnd(orderNotifyResult.getTimeEnd());

		UpdateWrapper<WxPay> wrapper = new UpdateWrapper<WxPay>();
		wrapper.eq("out_trade_no", outTradeNo);
		wrapper.and(i -> i.isNull("trade_state")
				.or(i2 -> i2.ne("trade_state", WxPayConstants.WxpayTradeStatus.SUCCESS)));
		int affect = wxPayMapper.update(wxPay4Update, wrapper);
		if (affect == 0) {
			// 订单回调已处理完成，无需重复处理
			payNotifyMapper.updateRemarkById("订单回调已处理完成，无需重复处理", payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.success("OK"));
		}

		// MQ异步处理
		Map<String, Object> params = Maps.newHashMap();
		params.put("outTradeNo", outTradeNo);

		LocalDateTime time = LocalDateTime.now();
		if (StringUtils.isNotBlank(orderNotifyResult.getTimeEnd())) {
			time = LocalDateTime.parse(orderNotifyResult.getTimeEnd(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		}
		params.put("time", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		// 财务流水信息
		params.put("amount", new BigDecimal(wxPay.getTotalFee())
				.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toPlainString());
		params.put("orderPayMethod", OrderPayEnum.Method.WX.getCode());
		params.put("merchantNo", wxPay.getMchid());
		params.put("tradeNo", orderNotifyResult.getTransactionId());

		messageSender.sendNormalMessage(StrategyConstants.PAY_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				Constants.QUEUE.PAY_NOTIFY.KEY);
        return Collections.singletonMap("value", WxPayNotifyResponse.success("OK"));
	}
	
	@Override
	public Map<String, String> wxPayRefundNotify(@RequestBody String xmlString) {
		/**
		 * <pre>
		 * 官方文档：https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=9_16&index=10
		<xml>
		  <return_code>SUCCESS</return_code>
		  <appid><![CDATA[wx8888888888888888]]></appid>
		  <mch_id><![CDATA[1900000109]]></mch_id>
		  <nonce_str><![CDATA[5K8264ILTKCH16CQ2502SI8ZNMTM67VS]]></nonce_str>
		  <req_info><![CDATA[T87GAHG17TGAHG1TGHAHAHA1Y1CIOA9UGJH1GAHV871HAGAGQYQQPOOJMXNBCXBVNMNMAJAA]]></req_info>
		  <return_msg><![CDATA[90]]></return_msg>
		</xml>
		
		req_info解密后的示例： 
		<root>
		  <out_refund_no><![CDATA[131811191610442717309]]></out_refund_no>
		  <out_trade_no><![CDATA[71106718111915575302817]]></out_trade_no>
		  <refund_account><![CDATA[REFUND_SOURCE_RECHARGE_FUNDS]]></refund_account>
		  <refund_fee><![CDATA[3960]]></refund_fee>
		  <refund_id><![CDATA[50000408942018111907145868882]]></refund_id>
		  <refund_recv_accout><![CDATA[支付用户零钱]]></refund_recv_accout>
		  <refund_request_source><![CDATA[API]]></refund_request_source>
		  <refund_status><![CDATA[SUCCESS]]></refund_status>
		  <settlement_refund_fee><![CDATA[3960]]></settlement_refund_fee>
		  <settlement_total_fee><![CDATA[3960]]></settlement_total_fee>
		  <success_time><![CDATA[2018-11-19 16:24:13]]></success_time>
		  <total_fee><![CDATA[3960]]></total_fee>
		  <transaction_id><![CDATA[4200000215201811190261405420]]></transaction_id>
		  <cash_refund_fee><![CDATA[90]]></cash_refund_fee>
		</root>
		 * </pre>
		 */
		// 记录原始数据
		log.info("wx refund notify data:{}", xmlString);
		PayNotify payNotify = new PayNotify().setMethod(OrderPayEnum.Method.WX.getCode()).setNotifyData(xmlString);
		payNotifyMapper.insert(payNotify);

		// 解析微信支付返回报文
		WxPayRefundNotifyResult refundNotifyResult = BaseWxPayResult.fromXML(xmlString, WxPayRefundNotifyResult.class);

		String returnCode = refundNotifyResult.getReturnCode();
		if (!Objects.equals(returnCode, WxPayConstants.ResultCode.SUCCESS)) {
			String message = refundNotifyResult.getReturnMsg();
			log.warn("returnCode不是SUCCESS:{},{}", returnCode, message);
			payNotifyMapper.updateRemarkById(message, payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("returnCode不是SUCCESS"));
		}
		
		String mchId = refundNotifyResult.getMchId();
		if (StringUtils.isBlank(mchId)) {
			payNotifyMapper.updateRemarkById("缺少mch_id", payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail("缺少mch_id"));
		}
		
		WxPayProperties.MchConfig mchConfig = wxPayConfiguration.getMchConfig(mchId);
		String mchKey = mchConfig.getMchKey();

		try {
			// 校验返回结果签名
			refundNotifyResult = WxPayRefundNotifyResult.fromXML(xmlString, mchKey);
		} catch (WxPayException e) {
			log.error("校验返回结果签名异常", e);
			payNotifyMapper.updateRemarkById(e.getMessage(), payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.fail(e.getMessage()));
		}

		ReqInfo reqInfo = refundNotifyResult.getReqInfo();
		String outRefundNo = reqInfo.getOutRefundNo();

		// 回调数据落库
		WxPayRefund wxPay4Update = new WxPayRefund().setRefundStatus(reqInfo.getRefundStatus());

		UpdateWrapper<WxPayRefund> wrapper = new UpdateWrapper<WxPayRefund>();
		wrapper.eq("out_refund_no", outRefundNo);
		wrapper.and(i -> i.isNull("refund_status")
				.or(i2 -> i2.ne("refund_status", WxPayConstants.RefundStatus.SUCCESS)));
		int affect = wxPayRefundMapper.update(wxPay4Update, wrapper);
		if (affect == 0) {
			// 订单回调已处理完成，无需重复处理
			payNotifyMapper.updateRemarkById("订单回调已处理完成，无需重复处理", payNotify.getId());
            return Collections.singletonMap("value", WxPayNotifyResponse.success("OK"));
		}

		String refundStatus = reqInfo.getRefundStatus();

		// MQ异步处理
		Map<String, Object> params = Maps.newHashMap();
		params.put("outTradeNo", reqInfo.getOutTradeNo());
		params.put("outRefundNo", outRefundNo);

		boolean success = WxPayConstants.RefundStatus.SUCCESS.equals(refundStatus);
		params.put("success", success);
		if (!success) {
			params.put("message", refundNotifyResult.getErrCodeDes());
		}

		//财务流水信息
		params.put("amount", new BigDecimal(reqInfo.getRefundFee())
				.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toPlainString());
		params.put("merchantNo", refundNotifyResult.getMchId());
		params.put("orderPayMethod", OrderPayEnum.Method.WX.getCode());
		params.put("tradeNo", reqInfo.getRefundId());

		messageSender.sendNormalMessage(StrategyConstants.REFUND_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				messagedrivenProperties.getQueue().getCommon().getKey());
        return Collections.singletonMap("value", WxPayNotifyResponse.success("OK"));
	}
}
