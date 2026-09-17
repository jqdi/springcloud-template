package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.interfaces.UserInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/userinfo", fallbackFactory = ThrowExceptionFallback.class)
public interface UserInfoFeign extends UserInfoApi {
}
