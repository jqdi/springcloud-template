package com.company.order.feign;

import com.company.order.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.RetryerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/retryer", fallbackFactory = ThrowExceptionFallback.class)
public interface RetryerFeign extends RetryerApi {
}
