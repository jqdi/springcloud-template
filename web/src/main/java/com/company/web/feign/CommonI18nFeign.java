package com.company.web.feign;

import com.company.web.constants.Constants;
import com.company.web.feign.fallback.CommonI18nFeignFallback;
import com.company.tool.api.feign.CommonI18nApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/commonI18n", fallbackFactory = CommonI18nFeignFallback.class)
public interface CommonI18nFeign extends CommonI18nApi {
}
