package com.company.job.feign;

import com.company.job.constants.Constants;
import com.company.job.feign.fallback.SubscribeFeignFallback;
import com.company.tool.api.feign.SubscribeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/subscribe", fallbackFactory = SubscribeFeignFallback.class)
public interface SubscribeFeign extends SubscribeApi {
}
