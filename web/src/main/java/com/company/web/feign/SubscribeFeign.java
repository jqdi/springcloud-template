package com.company.web.feign;

import com.company.web.constants.Constants;
import com.company.web.feign.fallback.SubscribeFeignFallback;
import com.company.tool.api.feign.SubscribeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/subscribe", fallbackFactory = SubscribeFeignFallback.class)
public interface SubscribeFeign extends SubscribeApi {
}
