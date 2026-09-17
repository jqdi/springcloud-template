package com.company.openapi.feign;

import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.AliActivityNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/aliactivitynotify", fallbackFactory = ThrowExceptionFallback.class)
public interface AliActivityNotifyFeign extends AliActivityNotifyApi {
}
