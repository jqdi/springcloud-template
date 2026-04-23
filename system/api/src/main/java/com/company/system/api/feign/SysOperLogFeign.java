package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysOperLogReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysOperLogResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysOperLog", fallbackFactory = ThrowExceptionFallback.class)
public interface SysOperLogFeign {

	@GetMapping("/page")
	PageResp<SysOperLogResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "sysUserId", required = false) Integer sysUserId, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "businessType", required = false) Integer businessType, @RequestParam(value = "method", required = false) String method, @RequestParam(value = "requestMethod", required = false) String requestMethod, @RequestParam(value = "operUrl", required = false) String operUrl, @RequestParam(value = "operIp", required = false) String operIp, @RequestParam(value = "operLocation", required = false) String operLocation, @RequestParam(value = "operParam", required = false) String operParam, @RequestParam(value = "jsonResult", required = false) String jsonResult, @RequestParam(value = "status", required = false) Integer status, @RequestParam(value = "errorMsg", required = false) String errorMsg, @RequestParam(value = "costTime", required = false) Integer costTime, @RequestParam(value = "operTimeStart", required = false) String operTimeStart, @RequestParam(value = "operTimeEnd", required = false) String operTimeEnd, @RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd, @RequestParam(value = "updateTimeStart", required = false) String updateTimeStart, @RequestParam(value = "updateTimeEnd", required = false) String updateTimeEnd);

	@GetMapping("/list")
	List<SysOperLogResp> list(@RequestParam(value = "sysUserId", required = false) Integer sysUserId, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "businessType", required = false) Integer businessType, @RequestParam(value = "method", required = false) String method, @RequestParam(value = "requestMethod", required = false) String requestMethod, @RequestParam(value = "operUrl", required = false) String operUrl, @RequestParam(value = "operIp", required = false) String operIp, @RequestParam(value = "operLocation", required = false) String operLocation, @RequestParam(value = "operParam", required = false) String operParam, @RequestParam(value = "jsonResult", required = false) String jsonResult, @RequestParam(value = "status", required = false) Integer status, @RequestParam(value = "errorMsg", required = false) String errorMsg, @RequestParam(value = "costTime", required = false) Integer costTime, @RequestParam(value = "operTimeStart", required = false) String operTimeStart, @RequestParam(value = "operTimeEnd", required = false) String operTimeEnd, @RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd, @RequestParam(value = "updateTimeStart", required = false) String updateTimeStart, @RequestParam(value = "updateTimeEnd", required = false) String updateTimeEnd);

	@GetMapping("/query")
	SysOperLogResp query(@RequestParam("id") Integer id);

	@PostMapping("/save")
	Boolean save(@RequestBody SysOperLogReq sysOperLogReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysOperLogReq sysOperLogReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

}
