package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.feign.UserInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.USER, path = "/userinfo", fallbackFactory = ThrowExceptionFallback.class)
public interface UserInfoFeign extends UserInfoApi {
}
