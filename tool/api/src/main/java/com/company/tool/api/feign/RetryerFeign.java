package com.company.tool.api.feign;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.request.RetryerInfoReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/retryer", fallbackFactory = ThrowExceptionFallback.class)
public interface RetryerFeign {
	@PostMapping("/call")
	Void call(@RequestBody RetryerInfoReq req);

	@GetMapping("/selectId4Call")
	List<Integer> selectId4Call();

	@PostMapping("/callById")
	Void callById(@RequestParam("id") Integer id);
}
