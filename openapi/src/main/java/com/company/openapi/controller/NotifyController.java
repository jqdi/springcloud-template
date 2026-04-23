package com.company.openapi.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.framework.util.WebUtil;
import com.company.openapi.annotation.NoSign;
import com.company.order.api.feign.AliActivityNotifyFeign;
import com.company.order.api.feign.AliNotifyFeign;
import com.company.order.api.feign.WxNotifyFeign;
import com.company.order.api.response.SpiOrderSendNotifyResp;
import com.feiniaojin.gracefulresponse.api.ExcludeFromGracefulResponse;

/**
 * 接收第三方回调请求
 */
@ExcludeFromGracefulResponse
@NoSign // 第三方回调请求有自己的验签，所以这里跳过验签，由后续业务进行验签
@RestController
@RequestMapping(value = "/notify")
public class NotifyController {

	@Autowired
	private WxNotifyFeign wxNotifyFeign;
	@Autowired
	private AliNotifyFeign aliNotifyFeign;
	@Autowired
	private AliActivityNotifyFeign aliActivityNotifyFeign;

	@PostMapping(value = "/wxPay", produces = MediaType.APPLICATION_XML_VALUE)
	public String wxPay(@RequestBody String xmlString) {
		return wxNotifyFeign.wxPayNotify(xmlString).values().iterator().next();
	}

	@PostMapping(value = "/wxPayRefund", produces = MediaType.APPLICATION_XML_VALUE)
	public String wxPayRefund(@RequestBody String xmlString) {
		return wxNotifyFeign.wxPayRefundNotify(xmlString).values().iterator().next();
	}

	@PostMapping(value = "/aliPay")
	public String aliPay(HttpServletRequest request) {
		Map<String, String> params = WebUtil.getReqParam(request);
		return aliNotifyFeign.aliPayNotify(params).values().iterator().next();
	}

	/**
	 * spi.alipay.marketing.activity.order.send(订单券发放)
	 */
	@PostMapping("/spiOrderSend")
	public SpiOrderSendNotifyResp spiOrderSend(HttpServletRequest request) {
		Map<String, String> params = WebUtil.getReqParam(request);
		return aliActivityNotifyFeign.spiOrderSendNotify(params);
	}

	/**
	 * From平台消息
	 */
	@PostMapping("/from")
	public String from(HttpServletRequest request) {
		Map<String, String> params = WebUtil.getReqParam(request);
		return aliActivityNotifyFeign.fromNotify(params).values().iterator().next();
	}
}
