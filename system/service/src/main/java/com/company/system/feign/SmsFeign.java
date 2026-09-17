package com.company.system.feign;

import com.company.system.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign extends SmsApi {
}
