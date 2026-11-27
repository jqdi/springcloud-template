package com.company.adminapi.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.company.user.api.interfaces.UserOauthApi;
import com.company.adminapi.feign.FeignConstants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;

@FeignClient(value = FeignConstants.USER, path = "/useroauth", fallbackFactory = ThrowExceptionFallback.class)
public interface UserOauthFeign extends UserOauthApi {
}
