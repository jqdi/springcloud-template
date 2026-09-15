package com.company.web.feign;

import com.company.web.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.constant.Constants;
import com.company.order.api.feign.FeignTestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/feignTest", fallbackFactory = ThrowExceptionFallback.class)
public interface FeignTestFeign extends FeignTestApi {
}
