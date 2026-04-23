package com.company.app.controller;

import com.company.app.req.SwaggerReq;
import com.company.app.resp.SwaggerResp;

import com.company.framework.util.PropertyUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/swagger")
public class SwaggerController {

	@PostMapping(value = "/post-body-row-result")
	public Map<String, Object> postbodyrowresult(@RequestBody Map<String, Object> param) {
		return param;
	}

	@PostMapping(value = "/post-body-row2")
	public String postbodyrownoresult2(@RequestBody Map<String, Object> param) {
		return "SUCCESS";
	}

	@PostMapping(value = "/postparam")
	public SwaggerResp postparam(@RequestBody SwaggerReq param) {
		SwaggerResp paramResp = PropertyUtils.copyProperties(param, SwaggerResp.class);
		return paramResp;
	}

	@GetMapping(value = "/getparam")
	public SwaggerResp getparam(SwaggerReq param) {
		SwaggerResp paramResp = PropertyUtils.copyProperties(param, SwaggerResp.class);
		return paramResp;
	}
}
