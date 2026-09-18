package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysUserRoleApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysUserRole", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserRoleFeign extends SysUserRoleApi {
}
