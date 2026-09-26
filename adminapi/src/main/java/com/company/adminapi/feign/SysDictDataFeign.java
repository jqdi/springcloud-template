package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysDictDataApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysDictData", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDictDataFeign extends SysDictDataApi {
}
