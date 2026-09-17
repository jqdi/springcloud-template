package com.company.openapi.feign;

import com.company.openapi.constants.Constants;
import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.AliNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/alinotify", fallbackFactory = ThrowExceptionFallback.class)
public interface AliNotifyFeign extends AliNotifyApi {
}
