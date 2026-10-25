package com.company.openapi.feign.client;

import com.company.openapi.feign.FeignConstants;
import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.WxNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/wxnotify", fallbackFactory = ThrowExceptionFallback.class)
public interface WxNotifyFeign extends WxNotifyApi {
}
