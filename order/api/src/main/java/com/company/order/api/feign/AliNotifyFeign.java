package com.company.order.api.feign;


import com.company.order.api.constant.Constants;
import com.company.order.api.feign.fallback.ThrowExceptionFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/alinotify", fallbackFactory = ThrowExceptionFallback.class)
public interface AliNotifyFeign {

	/**
	 * 支付宝支付回调
	 */
	@PostMapping("/aliPayNotify")
    Map<String, String> aliPayNotify(@RequestBody Map<String, String> params);
}
