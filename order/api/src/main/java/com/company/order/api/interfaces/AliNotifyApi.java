package com.company.order.api.interfaces;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface AliNotifyApi {

	/**
	 * 支付宝支付回调
	 */
	@PostMapping("/aliPayNotify")
    Map<String, String> aliPayNotify(@RequestBody Map<String, String> params);
}