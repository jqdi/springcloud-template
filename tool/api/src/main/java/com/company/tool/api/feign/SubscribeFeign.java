package com.company.tool.api.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.SubscribeFeignFallback;
import com.company.tool.api.request.SubscribeGrantReq;
import com.company.tool.api.request.SubscribeSendReq;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/subscribe", fallbackFactory = SubscribeFeignFallback.class)
public interface SubscribeFeign {

	@GetMapping("/selectTemplateCodeByGroup")
	List<String> selectTemplateCodeByGroup(@RequestParam("group") String group);

	/**
	 * 授权（如果实现了SubscribeType接口会同时发送消息）
	 * 
	 * @param subscribeGrantReq
	 * @return
	 */
	@PostMapping("/grant")
	Void grant(@RequestBody SubscribeGrantReq subscribeGrantReq);

	/**
	 * 发送
	 * 
	 * @param subscribeSendReq
	 * @return
	 */
	@PostMapping("/send")
	Void send(@RequestBody SubscribeSendReq subscribeSendReq);

	@GetMapping("/select4PreTimeSend")
	List<Integer> select4PreTimeSend(@RequestParam("limit") Integer limit);

	@GetMapping("/exePreTimeSend")
	Void exePreTimeSend(@RequestParam("id") Integer id);

	@GetMapping("/syncTemplate")
	Void syncTemplate();
}
