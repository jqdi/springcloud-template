package com.company.openapi.feign;

import com.company.openapi.constants.Constants;
import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.WxNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/wxnotify", fallbackFactory = ThrowExceptionFallback.class)
public interface WxNotifyFeign extends WxNotifyApi {
}
