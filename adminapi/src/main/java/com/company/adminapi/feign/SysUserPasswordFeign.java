package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysUserPasswordApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysUserPassword", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserPasswordFeign extends SysUserPasswordApi {
}
