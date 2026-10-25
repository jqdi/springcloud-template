package com.company.adminapi.feign.client;

import com.company.adminapi.feign.FeignConstants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysRoleDeptApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysRoleDept", fallbackFactory = ThrowExceptionFallback.class)
public interface SysRoleDeptFeign extends SysRoleDeptApi {
}
