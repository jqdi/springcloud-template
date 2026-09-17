package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.feign.SysDeptApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysDept", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDeptFeign extends SysDeptApi {
}
