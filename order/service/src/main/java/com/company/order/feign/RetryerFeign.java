package com.company.order.feign;

import com.company.order.constant.Constants;
import com.company.order.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.RetryerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/retryer", fallbackFactory = ThrowExceptionFallback.class)
public interface RetryerFeign extends RetryerApi {
}
