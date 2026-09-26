package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.VerifyCodeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/verifyCode", fallbackFactory = ThrowExceptionFallback.class)
public interface VerifyCodeFeign extends VerifyCodeApi {
}
