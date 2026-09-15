package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.PopupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/popup", fallbackFactory = ThrowExceptionFallback.class)
public interface PopupFeign extends PopupApi {
}
