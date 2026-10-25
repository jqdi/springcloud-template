package com.company.job.feign.client;

import com.company.job.feign.FeignConstants;
import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.RetryerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/retryer", fallbackFactory = ThrowExceptionFallback.class)
public interface RetryerFeign extends RetryerApi {
}
