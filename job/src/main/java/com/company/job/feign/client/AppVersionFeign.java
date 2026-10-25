package com.company.job.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.company.job.feign.FeignConstants;
import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.AppVersionApi;

@FeignClient(value = FeignConstants.TOOL, path = "/appVersion", fallbackFactory = ThrowExceptionFallback.class)
public interface AppVersionFeign extends AppVersionApi {
}
