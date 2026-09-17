package com.company.tool.feign;

import com.company.tool.feign.fallback.ThrowExceptionFallback;
import com.company.tool.constant.Constants;
import com.company.tool.api.feign.PopupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/popup", fallbackFactory = ThrowExceptionFallback.class)
public interface PopupFeign extends PopupApi {
}
