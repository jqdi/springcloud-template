package com.company.web.feign;

import com.company.web.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.FeignTestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/feignTest", fallbackFactory = ThrowExceptionFallback.class)
public interface FeignTestFeign extends FeignTestApi {
}
