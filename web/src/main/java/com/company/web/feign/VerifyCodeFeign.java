package com.company.web.feign;

import com.company.web.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.VerifyCodeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/verifyCode", fallbackFactory = ThrowExceptionFallback.class)
public interface VerifyCodeFeign extends VerifyCodeApi {
}
