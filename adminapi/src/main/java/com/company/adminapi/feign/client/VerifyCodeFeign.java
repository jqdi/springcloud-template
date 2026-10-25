package com.company.adminapi.feign.client;

import com.company.adminapi.feign.FeignConstants;
import com.company.adminapi.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.VerifyCodeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/verifyCode", fallbackFactory = ThrowExceptionFallback.class)
public interface VerifyCodeFeign extends VerifyCodeApi {
}
