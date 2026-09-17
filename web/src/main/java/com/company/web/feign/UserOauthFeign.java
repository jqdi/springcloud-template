package com.company.web.feign;

import com.company.web.constants.Constants;
import com.company.web.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.feign.UserOauthApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.USER, path = "/useroauth", fallbackFactory = ThrowExceptionFallback.class)
public interface UserOauthFeign extends UserOauthApi {
}
