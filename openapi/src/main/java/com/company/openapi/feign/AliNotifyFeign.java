package com.company.openapi.feign;

import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.AliNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/alinotify", fallbackFactory = ThrowExceptionFallback.class)
public interface AliNotifyFeign extends AliNotifyApi {
}
