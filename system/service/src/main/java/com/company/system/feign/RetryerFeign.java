package com.company.system.feign;

import com.company.system.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.RetryerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/retryer", fallbackFactory = ThrowExceptionFallback.class)
public interface RetryerFeign extends RetryerApi {
}
