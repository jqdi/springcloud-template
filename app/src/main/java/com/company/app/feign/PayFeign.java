package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.PayApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/pay", fallbackFactory = ThrowExceptionFallback.class)
public interface PayFeign extends PayApi {
}
