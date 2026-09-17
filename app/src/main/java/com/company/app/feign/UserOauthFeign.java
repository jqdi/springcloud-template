package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.feign.UserOauthApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.USER, path = "/useroauth", fallbackFactory = ThrowExceptionFallback.class)
public interface UserOauthFeign extends UserOauthApi {
}
