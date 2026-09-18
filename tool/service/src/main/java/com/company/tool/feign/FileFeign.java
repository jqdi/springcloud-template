package com.company.tool.feign;

import com.company.tool.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.interfaces.FileApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.TOOL, path = "/file", fallbackFactory = ThrowExceptionFallback.class)
public interface FileFeign extends FileApi {}
