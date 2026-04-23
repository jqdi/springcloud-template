package com.company.tool.api.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.NavFeignFallback;
import com.company.tool.api.request.NavReq;
import com.company.tool.api.response.NavResp;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/nav", fallbackFactory = NavFeignFallback.class)
public interface NavFeign {

	@RequestMapping("/list")
	List<NavResp> list(@RequestBody NavReq navReq);
}
