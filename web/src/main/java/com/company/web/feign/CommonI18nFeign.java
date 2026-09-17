package com.company.web.feign;

import com.company.web.feign.fallback.CommonI18nFeignFallback;
import com.company.tool.api.interfaces.CommonI18nApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/commonI18n", fallbackFactory = CommonI18nFeignFallback.class)
public interface CommonI18nFeign extends CommonI18nApi {
}
