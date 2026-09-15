package com.company.order.api.feign;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface IosNotifyApi {

	/**
	 * 支付回调
	 */
	@PostMapping("/iosPayNotify")
    Map<String, String> iosPayNotify(@RequestBody Map<String, String> params);
}