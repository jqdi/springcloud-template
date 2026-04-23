package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysRoleDeptReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysRoleDeptResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysRoleDept", fallbackFactory = ThrowExceptionFallback.class)
public interface SysRoleDeptFeign {

	@GetMapping("/page")
	PageResp<SysRoleDeptResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "roleId", required = false) Integer roleId, @RequestParam(value = "deptId", required = false) Integer deptId);

	@GetMapping("/list")
	List<SysRoleDeptResp> list(@RequestParam(value = "roleId", required = false) Integer roleId, @RequestParam(value = "deptId", required = false) Integer deptId);

	@GetMapping("/query")
	SysRoleDeptResp query(@RequestParam("id") Integer id);

	@PostMapping("/save")
	Boolean save(@RequestBody SysRoleDeptReq sysRoleDeptReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysRoleDeptReq sysRoleDeptReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

}
