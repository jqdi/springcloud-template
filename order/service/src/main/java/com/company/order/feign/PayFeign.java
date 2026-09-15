package com.company.order.feign;

import com.company.order.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.constant.Constants;
import com.company.order.api.feign.PayApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/pay", fallbackFactory = ThrowExceptionFallback.class)
public interface PayFeign extends PayApi {
}
