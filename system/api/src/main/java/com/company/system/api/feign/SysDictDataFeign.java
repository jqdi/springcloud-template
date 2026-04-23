package com.company.system.api.feign;


import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysDictDataReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SysDictDataResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysDictData", fallbackFactory = ThrowExceptionFallback.class)
public interface SysDictDataFeign {

	@GetMapping("/page")
	PageResp<SysDictDataResp> page(@RequestParam(value = "current") Long current, @RequestParam(value = "size") Long size, @RequestParam(value = "dictType", required = false) String dictType, @RequestParam(value = "dictCode", required = false) String dictCode, @RequestParam(value = "dictValue", required = false) String dictValue, @RequestParam(value = "dictSort", required = false) Integer dictSort, @RequestParam(value = "isDefault", required = false) String isDefault, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "dictRemark", required = false) String dictRemark);

	@GetMapping("/list")
	List<SysDictDataResp> list(@RequestParam(value = "dictType", required = false) String dictType, @RequestParam(value = "dictCode", required = false) String dictCode, @RequestParam(value = "dictValue", required = false) String dictValue, @RequestParam(value = "dictSort", required = false) Integer dictSort, @RequestParam(value = "isDefault", required = false) String isDefault, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "dictRemark", required = false) String dictRemark);

	@GetMapping("/query")
	SysDictDataResp query(@RequestParam("id") Integer id);

	@PostMapping("/save")
	Boolean save(@RequestBody SysDictDataReq sysDictDataReq);

	@PostMapping("/update")
	Boolean update(@RequestBody SysDictDataReq sysDictDataReq);

	@PostMapping("/remove")
	Boolean remove(@RequestBody RemoveReq<Integer> req);

	@GetMapping("/getByType")
	List<SysDictDataResp> getByType(@RequestParam("type") String type);

	@GetMapping("/getValueByTypeCode")
    Map<String, String> getValueByTypeCode(@RequestParam("type") String type, @RequestParam("code") String code);
}
