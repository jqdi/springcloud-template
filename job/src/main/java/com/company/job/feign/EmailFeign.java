package com.company.job.feign;

import com.company.job.feign.fallback.EmailFeignFallback;
import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.EmailApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/email", fallbackFactory = EmailFeignFallback.class)
public interface EmailFeign extends EmailApi {
}
