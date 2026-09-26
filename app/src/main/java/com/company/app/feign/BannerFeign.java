package com.company.app.feign;

import com.company.app.feign.fallback.BannerFeignFallback;
import com.company.tool.api.interfaces.BannerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/banner", fallbackFactory = BannerFeignFallback.class)
public interface BannerFeign extends BannerApi {
}
