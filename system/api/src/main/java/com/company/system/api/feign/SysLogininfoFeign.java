package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysLogininfoReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysLogininfoResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysLogininfo", fallbackFactory = ThrowExceptionFallback.class)
public interface SysLogininfoFeign {

	@GetMapping("/page")
	PageResp<SysLogininfoResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "sysUserId", required = false) Integer sysUserId, @RequestParam(value = "loginTimeStart", required = false) String loginTimeStart, @RequestParam(value = "loginTimeEnd", required = false) String loginTimeEnd, @RequestParam(value = "account", required = false) String account, @RequestParam(value = "device", required = false) String device, @RequestParam(value = "platform", required = false) String platform, @RequestParam(value = "operator", required = false) String operator, @RequestParam(value = "version", required = false) String version, @RequestParam(value = "deviceid", required = false) String deviceid, @RequestParam(value = "channel", required = false) String channel, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "address", required = false) String address, @RequestParam(value = "source", required = false) String source, @RequestParam(value = "lang", required = false) String lang, @RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd, @RequestParam(value = "updateTimeStart", required = false) String updateTimeStart, @RequestParam(value = "updateTimeEnd", required = false) String updateTimeEnd);

	@GetMapping("/list")
	List<SysLogininfoResp> list(@RequestParam(value = "sysUserId", required = false) Integer sysUserId, @RequestParam(value = "loginTimeStart", required = false) String loginTimeStart, @RequestParam(value = "loginTimeEnd", required = false) String loginTimeEnd, @RequestParam(value = "account", required = false) String account, @RequestParam(value = "device", required = false) String device, @RequestParam(value = "platform", required = false) String platform, @RequestParam(value = "operator", required = false) String operator, @RequestParam(value = "version", required = false) String version, @RequestParam(value = "deviceid", required = false) String deviceid, @RequestParam(value = "channel", required = false) String channel, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "address", required = false) String address, @RequestParam(value = "source", required = false) String source, @RequestParam(value = "lang", required = false) String lang, @RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd, @RequestParam(value = "updateTimeStart", required = false) String updateTimeStart, @RequestParam(value = "updateTimeEnd", required = false) String updateTimeEnd);

	@GetMapping("/query")
	SysLogininfoResp query(@RequestParam("id") Integer id);

	@PostMapping("/save")
	Boolean save(@RequestBody SysLogininfoReq sysLogininfoReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysLogininfoReq sysLogininfoReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

}
