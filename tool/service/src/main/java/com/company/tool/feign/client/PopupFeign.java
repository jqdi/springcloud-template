package com.company.tool.feign.client;

import com.company.tool.feign.FeignConstants;
import com.company.tool.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.PopupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/popup", fallbackFactory = ThrowExceptionFallback.class)
public interface PopupFeign extends PopupApi {
}
