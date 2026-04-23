package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysConfigReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysConfigResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysConfig", fallbackFactory = ThrowExceptionFallback.class)
public interface SysConfigFeign {

	@GetMapping("/page")
	PageResp<SysConfigResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "code", required = false) String code, @RequestParam(value = "value", required = false) String value, @RequestParam(value = "configRemark", required = false) String configRemark);

	@GetMapping("/list")
	List<SysConfigResp> list(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "code", required = false) String code, @RequestParam(value = "value", required = false) String value, @RequestParam(value = "configRemark", required = false) String configRemark);

	@GetMapping("/query")
	SysConfigResp query(@RequestParam("id") Integer id);

	@PostMapping("/save")
	Boolean save(@RequestBody SysConfigReq sysConfigReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysConfigReq sysConfigReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

	@GetMapping("/getValueByCode")
    Map<String, String> getValueByCode(@RequestParam("code") String code);

	@PostMapping("/updateValueByCode")
	Boolean updateValueByCode(@RequestParam("value") String value, @RequestParam("code") String code);

}
