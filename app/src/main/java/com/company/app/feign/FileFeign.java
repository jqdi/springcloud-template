package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.FileApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/file", fallbackFactory = ThrowExceptionFallback.class)
public interface FileFeign extends FileApi {
}
