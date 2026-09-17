package com.company.job.feign;

import com.company.job.constants.Constants;
import com.company.job.feign.fallback.EmailFeignFallback;
import com.company.tool.api.feign.EmailApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/email", fallbackFactory = EmailFeignFallback.class)
public interface EmailFeign extends EmailApi {
}
