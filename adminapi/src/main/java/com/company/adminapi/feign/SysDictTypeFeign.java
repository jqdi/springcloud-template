package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysDictTypeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysDictType", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDictTypeFeign extends SysDictTypeApi {
}
