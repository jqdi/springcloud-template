package com.company.tool.api.feign;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import com.company.tool.api.request.NavReq;
import com.company.tool.api.response.NavResp;

public interface NavApi {

	@RequestMapping("/list")
	List<NavResp> list(@RequestBody NavReq navReq);
}