package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.AppVersionApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/appVersion", fallbackFactory = ThrowExceptionFallback.class)
public interface AppVersionFeign extends AppVersionApi {
}
