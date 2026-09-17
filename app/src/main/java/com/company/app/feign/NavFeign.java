package com.company.app.feign;

import com.company.app.feign.fallback.NavFeignFallback;
import com.company.tool.api.feign.NavApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/nav", fallbackFactory = NavFeignFallback.class)
public interface NavFeign extends NavApi {
}
