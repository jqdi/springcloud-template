package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysMenuApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysMenu", fallbackFactory = ThrowExceptionFallback.class)
public interface SysMenuFeign extends SysMenuApi {
}
