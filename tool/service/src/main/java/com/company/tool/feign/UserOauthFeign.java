package com.company.tool.feign;

import com.company.tool.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.feign.UserOauthApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/useroauth", fallbackFactory = ThrowExceptionFallback.class)
public interface UserOauthFeign extends UserOauthApi {
}
