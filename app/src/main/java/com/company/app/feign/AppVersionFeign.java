package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.AppVersionApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/appVersion", fallbackFactory = ThrowExceptionFallback.class)
public interface AppVersionFeign extends AppVersionApi {
}
