package com.company.order.api.feign;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface WxNotifyApi {

	/**
	 * 微信支付回调
	 */
	@PostMapping("/wxPayNotify")
    Map<String, String> wxPayNotify(@RequestBody String xmlString);
	
	/**
	 * 退款回调
	 * 
	 * @param xmlString
	 * @return
	 */
	@PostMapping("/wxPayRefundNotify")
    Map<String, String> wxPayRefundNotify(@RequestBody String xmlString);
}