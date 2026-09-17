package com.company.job.feign;

import com.company.job.constants.Constants;
import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.RetryerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/retryer", fallbackFactory = ThrowExceptionFallback.class)
public interface RetryerFeign extends RetryerApi {
}
