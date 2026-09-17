package com.company.job.feign;

import com.company.job.constants.Constants;
import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign extends SmsApi {
}
