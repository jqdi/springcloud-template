package com.company.web.feign;

import com.company.web.constants.Constants;
import com.company.web.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.feign.FileApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.TOOL, path = "/file", fallbackFactory = ThrowExceptionFallback.class)
public interface FileFeign extends FileApi {
}
