package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysDictTypeReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysDictTypeResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysDictType", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDictTypeFeign {

	@GetMapping("/page")
	PageResp<SysDictTypeResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "dictName", required = false) String dictName, @RequestParam(value = "dictType", required = false) String dictType, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "dictRemark", required = false) String dictRemark);

	@GetMapping("/list")
	List<SysDictTypeResp> list(@RequestParam(value = "dictName", required = false) String dictName, @RequestParam(value = "dictType", required = false) String dictType, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "dictRemark", required = false) String dictRemark);

	@GetMapping("/query")
	SysDictTypeResp query(@RequestParam("id") Integer id);

	@PostMapping("/save")
	Boolean save(@RequestBody SysDictTypeReq sysDictTypeReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysDictTypeReq sysDictTypeReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

}
