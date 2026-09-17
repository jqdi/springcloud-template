package com.company.job.feign;

import com.company.job.constants.Constants;
import com.company.job.feign.fallback.WebhookFeignFallback;
import com.company.tool.api.feign.WebhookApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/webhook", fallbackFactory = WebhookFeignFallback.class)
public interface WebhookFeign extends WebhookApi {
}
