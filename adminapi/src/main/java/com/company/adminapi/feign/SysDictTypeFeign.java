package com.company.adminapi.feign;

import com.company.adminapi.constants.Constants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysDictTypeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.SYSTEM, path = "/sysDictType", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDictTypeFeign extends SysDictTypeApi {
}
