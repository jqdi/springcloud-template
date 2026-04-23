package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysDeptExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysDeptFeign;
import com.company.system.api.request.SysDeptReq;
import com.company.system.api.response.SysDeptResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysDept")
public class SysDeptController {

	@Autowired
	private SysDeptFeign sysDeptFeign;

	@RequirePermissions("system:sysDept:query")
	@GetMapping("/page")
	public PageResp<SysDeptResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer parentId, String parentIds, String name, Integer orderNum, String status) {
		return sysDeptFeign.page(current, size, parentId, parentIds, name, orderNum, status);
	}

	@RequirePermissions("system:sysDept:query")
	@GetMapping("/query")
	public SysDeptResp query(@NotNull Integer id) {
		return sysDeptFeign.query(id);
	}

    @OperationLog(title = "部门保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysDept:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysDeptReq sysDeptReq) {
		return sysDeptFeign.save(sysDeptReq);
	}

    @OperationLog(title = "部门更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysDept:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysDeptReq sysDeptReq) {
		return sysDeptFeign.update(sysDeptReq);
	}

    @OperationLog(title = "部门删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysDept:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysDeptFeign.remove(req);
	}

	@OperationLog(title = "部门导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysDept:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer parentId, String parentIds, String name, Integer orderNum, String status) {
		List<SysDeptResp> listResp = sysDeptFeign.list(parentId, parentIds, name, orderNum, status);
		List<SysDeptExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysDeptExcel.class);
		ExcelUtil.write2httpResponse(response, "部门", SysDeptExcel.class, excelList);
	}

}
