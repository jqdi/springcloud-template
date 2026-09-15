package com.company.openapi.feign;

import com.company.openapi.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.constant.Constants;
import com.company.order.api.feign.WxNotifyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/wxnotify", fallbackFactory = ThrowExceptionFallback.class)
public interface WxNotifyFeign extends WxNotifyApi {
}
