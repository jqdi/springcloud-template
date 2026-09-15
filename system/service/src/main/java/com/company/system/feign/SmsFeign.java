package com.company.system.feign;

import com.company.system.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign extends SmsApi {
}
