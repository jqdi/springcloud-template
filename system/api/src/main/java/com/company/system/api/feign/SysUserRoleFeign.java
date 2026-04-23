package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysUserRoleReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysUserRoleResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysUserRole", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserRoleFeign {

	@GetMapping("/page")
	PageResp<SysUserRoleResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "userId", required = false) Integer userId, @RequestParam(value = "roleId", required = false) Integer roleId, @RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd, @RequestParam(value = "updateTimeStart", required = false) String updateTimeStart, @RequestParam(value = "updateTimeEnd", required = false) String updateTimeEnd);

	@GetMapping("/list")
	List<SysUserRoleResp> list(@RequestParam(value = "userId", required = false) Integer userId, @RequestParam(value = "roleId", required = false) Integer roleId, @RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "createTimeEnd", required = false) String createTimeEnd, @RequestParam(value = "updateTimeStart", required = false) String updateTimeStart, @RequestParam(value = "updateTimeEnd", required = false) String updateTimeEnd);

	@GetMapping("/query")
	SysUserRoleResp query(@RequestParam("id") Integer id);

	@GetMapping("/listRoleIdByUserId")
	Set<Integer> listRoleIdByUserId(@RequestParam("userId") Integer userId);

	@PostMapping("/save")
	Boolean save(@RequestBody SysUserRoleReq sysUserRoleReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysUserRoleReq sysUserRoleReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

	@GetMapping("/hasPermission")
	Boolean hasPermission(@RequestParam("userId") Integer userId,
			@RequestParam("permission") String permission);
}
