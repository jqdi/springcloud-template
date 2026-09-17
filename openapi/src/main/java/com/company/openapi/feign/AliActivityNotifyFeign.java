package com.company.openapi.feign;

import com.company.openapi.constants.Constants;
import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.AliActivityNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/aliactivitynotify", fallbackFactory = ThrowExceptionFallback.class)
public interface AliActivityNotifyFeign extends AliActivityNotifyApi {
}
