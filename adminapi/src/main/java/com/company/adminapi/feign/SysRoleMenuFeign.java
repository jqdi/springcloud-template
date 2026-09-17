package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysRoleMenuApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysRoleMenu", fallbackFactory = ThrowExceptionFallback.class)
public interface SysRoleMenuFeign extends SysRoleMenuApi {
}
