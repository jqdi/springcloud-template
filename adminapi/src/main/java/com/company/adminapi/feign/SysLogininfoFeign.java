package com.company.adminapi.feign;

import com.company.adminapi.constants.Constants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysLogininfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.SYSTEM, path = "/sysLogininfo", fallbackFactory = ThrowExceptionFallback.class)
public interface SysLogininfoFeign extends SysLogininfoApi {
}
