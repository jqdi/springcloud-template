package com.company.adminapi.feign.client;

import com.company.adminapi.feign.FeignConstants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.interfaces.SysDeptApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.SYSTEM, path = "/sysDept", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDeptFeign extends SysDeptApi {
}
