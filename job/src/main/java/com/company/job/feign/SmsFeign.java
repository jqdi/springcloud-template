package com.company.job.feign;

import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign extends SmsApi {
}
