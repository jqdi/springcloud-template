package com.company.job.feign;

import com.company.job.feign.fallback.WebhookFeignFallback;
import com.company.tool.api.interfaces.WebhookApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/webhook", fallbackFactory = WebhookFeignFallback.class)
public interface WebhookFeign extends WebhookApi {
}
