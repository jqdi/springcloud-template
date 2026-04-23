package com.company.order.api.feign;


import com.company.order.api.constant.Constants;
import com.company.order.api.feign.fallback.ThrowExceptionFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/wxnotify", fallbackFactory = ThrowExceptionFallback.class)
public interface WxNotifyFeign {

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
