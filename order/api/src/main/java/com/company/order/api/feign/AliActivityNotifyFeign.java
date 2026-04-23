package com.company.order.api.feign;


import com.company.order.api.constant.Constants;
import com.company.order.api.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.response.SpiOrderSendNotifyResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/aliactivitynotify", fallbackFactory = ThrowExceptionFallback.class)
public interface AliActivityNotifyFeign {

	/**
	 * 支付宝营销活动SPI回调(订单券发放)
	 */
	@PostMapping("/spiOrderSendNotify")
	SpiOrderSendNotifyResp spiOrderSendNotify(@RequestBody Map<String, String> aliParams);
	/**
	 * 支付宝营销活动FROM回调
	 */
	@PostMapping("/fromNotify")
    Map<String, String> fromNotify(@RequestBody Map<String, String> params);
}
