package com.company.adminapi.feign;

import com.company.adminapi.constants.Constants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.VerifyCodeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/verifyCode", fallbackFactory = ThrowExceptionFallback.class)
public interface VerifyCodeFeign extends VerifyCodeApi {
}
