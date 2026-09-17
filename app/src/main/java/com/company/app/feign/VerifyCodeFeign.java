package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.VerifyCodeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/verifyCode", fallbackFactory = ThrowExceptionFallback.class)
public interface VerifyCodeFeign extends VerifyCodeApi {
}
