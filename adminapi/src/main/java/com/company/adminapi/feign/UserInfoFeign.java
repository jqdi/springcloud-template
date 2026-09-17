package com.company.adminapi.feign;

import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.feign.UserInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/userinfo", fallbackFactory = ThrowExceptionFallback.class)
public interface UserInfoFeign extends UserInfoApi {
}
