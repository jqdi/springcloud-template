package com.company.order.api.feign;


import com.company.order.api.constant.Constants;
import com.company.order.api.feign.fallback.ThrowExceptionFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/iosnotify", fallbackFactory = ThrowExceptionFallback.class)
public interface IosNotifyFeign {

	/**
	 * 支付回调
	 */
	@PostMapping("/iosPayNotify")
    Map<String, String> iosPayNotify(@RequestBody Map<String, String> params);
}
