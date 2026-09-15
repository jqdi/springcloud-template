package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.constant.Constants;
import com.company.system.api.feign.SysRoleDeptApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysRoleDept", fallbackFactory = ThrowExceptionFallback.class)
public interface SysRoleDeptFeign extends SysRoleDeptApi {
}
