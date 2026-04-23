package com.company.tool.api.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.WebhookFeignFallback;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/webhook", fallbackFactory = WebhookFeignFallback.class)
public interface WebhookFeign {

	@GetMapping("/select4PreTimeSend")
	List<Integer> select4PreTimeSend(@RequestParam("limit") Integer limit);

	@GetMapping("/exePreTimeSend")
	Void exePreTimeSend(@RequestParam("id") Integer id);
}
