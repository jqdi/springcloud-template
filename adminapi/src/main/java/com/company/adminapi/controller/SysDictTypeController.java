package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysDictTypeExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysDictTypeFeign;
import com.company.system.api.request.SysDictTypeReq;
import com.company.system.api.response.SysDictTypeResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysDictType")
public class SysDictTypeController {

	@Autowired
	private SysDictTypeFeign sysDictTypeFeign;

	@RequirePermissions("system:sysDictType:query")
	@GetMapping("/page")
	public PageResp<SysDictTypeResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, String dictName, String dictType, String status, String dictRemark) {
		return sysDictTypeFeign.page(current, size, dictName, dictType, status, dictRemark);
	}

	@RequirePermissions("system:sysDictType:query")
	@GetMapping("/query")
	public SysDictTypeResp query(@NotNull Integer id) {
		return sysDictTypeFeign.query(id);
	}

    @OperationLog(title = "字典类型保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysDictType:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysDictTypeReq sysDictTypeReq) {
		return sysDictTypeFeign.save(sysDictTypeReq);
	}

    @OperationLog(title = "字典类型更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysDictType:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysDictTypeReq sysDictTypeReq) {
		return sysDictTypeFeign.update(sysDictTypeReq);
	}

    @OperationLog(title = "字典类型删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysDictType:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysDictTypeFeign.remove(req);
	}

	@OperationLog(title = "字典类型导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysDictType:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, String dictName, String dictType, String status, String dictRemark) {
		List<SysDictTypeResp> listResp = sysDictTypeFeign.list(dictName, dictType, status, dictRemark);
		List<SysDictTypeExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysDictTypeExcel.class);
		ExcelUtil.write2httpResponse(response, "字典类型", SysDictTypeExcel.class, excelList);
	}

}
