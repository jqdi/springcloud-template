package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.PopupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/popup", fallbackFactory = ThrowExceptionFallback.class)
public interface PopupFeign extends PopupApi {
}
