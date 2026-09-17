package com.company.adminapi.feign;

import com.company.adminapi.constants.Constants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysUserRoleApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.SYSTEM, path = "/sysUserRole", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserRoleFeign extends SysUserRoleApi {
}
