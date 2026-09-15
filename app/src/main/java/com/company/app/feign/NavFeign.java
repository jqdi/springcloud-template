package com.company.app.feign;

import com.company.app.feign.fallback.NavFeignFallback;
import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.NavApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/nav", fallbackFactory = NavFeignFallback.class)
public interface NavFeign extends NavApi {
}
