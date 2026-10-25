package com.company.job.feign.client;

import com.company.job.feign.FeignConstants;
import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign extends SmsApi {
}
