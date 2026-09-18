package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysUserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysUser", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserFeign extends SysUserApi {
}
