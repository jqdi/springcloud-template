package com.company.tool.api.feign;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.request.SendSmsReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sms", fallbackFactory = ThrowExceptionFallback.class)
public interface SmsFeign {

	@GetMapping("/select4PreTimeSend")
	List<Integer> select4PreTimeSend(@RequestParam("limit") Integer limit);

	@GetMapping("/exePreTimeSend")
	Void exePreTimeSend(@RequestParam("id") Integer id);

	@PostMapping("/send")
	Void send(@RequestBody SendSmsReq sendSmsReq);
}
