package com.company.adminapi.feign.client;

import com.company.adminapi.feign.FeignConstants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysRoleApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysRole", fallbackFactory = ThrowExceptionFallback.class)
public interface SysRoleFeign extends SysRoleApi {
}
