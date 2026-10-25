package com.company.adminapi.feign.client;

import com.company.adminapi.feign.FeignConstants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysUserPasswordApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysUserPassword", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserPasswordFeign extends SysUserPasswordApi {
}
