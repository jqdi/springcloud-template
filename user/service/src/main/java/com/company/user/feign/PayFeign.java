package com.company.user.feign;

import com.company.user.constant.Constants;
import com.company.user.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.PayApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/pay", fallbackFactory = ThrowExceptionFallback.class)
public interface PayFeign extends PayApi {
}
