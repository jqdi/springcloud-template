package com.company.system.feign;

import com.company.system.constant.Constants;
import com.company.system.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign extends SmsApi {
}
