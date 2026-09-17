package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.PayApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/pay", fallbackFactory = ThrowExceptionFallback.class)
public interface PayFeign extends PayApi {
}
