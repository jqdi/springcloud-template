package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysOperLogApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysOperLog", fallbackFactory = ThrowExceptionFallback.class)
public interface SysOperLogFeign extends SysOperLogApi {
}
