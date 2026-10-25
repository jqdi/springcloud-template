package com.company.order.feign.client;

import com.company.order.feign.FeignConstants;
import com.company.order.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.PayApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/pay", fallbackFactory = ThrowExceptionFallback.class)
public interface PayFeign extends PayApi {
}
