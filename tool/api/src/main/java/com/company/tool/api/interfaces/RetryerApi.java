package com.company.tool.api.interfaces;


import com.company.tool.api.request.RetryerInfoReq;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RetryerApi {
	@PostMapping("/call")
	Void call(@RequestBody RetryerInfoReq req);

	@GetMapping("/selectId4Call")
	List<Integer> selectId4Call();

	@PostMapping("/callById")
	Void callById(@RequestParam("id") Integer id);
}