package com.company.web.feign.client;

import com.company.web.feign.FeignConstants;
import com.company.web.feign.fallback.SubscribeFeignFallback;
import com.company.tool.api.interfaces.SubscribeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/subscribe", fallbackFactory = SubscribeFeignFallback.class)
public interface SubscribeFeign extends SubscribeApi {
}
