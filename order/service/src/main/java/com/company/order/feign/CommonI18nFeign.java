package com.company.order.feign;

import com.company.order.feign.fallback.CommonI18nFeignFallback;
import com.company.tool.api.feign.CommonI18nApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/commonI18n", fallbackFactory = CommonI18nFeignFallback.class)
public interface CommonI18nFeign extends CommonI18nApi {
}
