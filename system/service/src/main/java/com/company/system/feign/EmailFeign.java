package com.company.system.feign;

import com.company.system.feign.fallback.EmailFeignFallback;
import com.company.tool.api.feign.EmailApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/email", fallbackFactory = EmailFeignFallback.class)
public interface EmailFeign extends EmailApi {
}
