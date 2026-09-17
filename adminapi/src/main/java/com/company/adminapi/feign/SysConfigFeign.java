package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysConfigApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysConfig", fallbackFactory = ThrowExceptionFallback.class)
public interface SysConfigFeign extends SysConfigApi {
}
