package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysRoleDeptExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysRoleDeptFeign;
import com.company.system.api.request.SysRoleDeptReq;
import com.company.system.api.response.SysRoleDeptResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysRoleDept")
public class SysRoleDeptController {

	@Autowired
	private SysRoleDeptFeign sysRoleDeptFeign;

	@RequirePermissions("system:sysRoleDept:query")
	@GetMapping("/page")
	public PageResp<SysRoleDeptResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer roleId, Integer deptId) {
		return sysRoleDeptFeign.page(current, size, roleId, deptId);
	}

	@RequirePermissions("system:sysRoleDept:query")
	@GetMapping("/query")
	public SysRoleDeptResp query(@NotNull Integer id) {
		return sysRoleDeptFeign.query(id);
	}

    @OperationLog(title = "角色和菜单关联保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysRoleDept:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysRoleDeptReq sysRoleDeptReq) {
		return sysRoleDeptFeign.save(sysRoleDeptReq);
	}

    @OperationLog(title = "角色和菜单关联更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysRoleDept:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysRoleDeptReq sysRoleDeptReq) {
		return sysRoleDeptFeign.update(sysRoleDeptReq);
	}

    @OperationLog(title = "角色和菜单关联删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysRoleDept:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysRoleDeptFeign.remove(req);
	}

	@OperationLog(title = "角色和菜单关联导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysRoleDept:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer roleId, Integer deptId) {
		List<SysRoleDeptResp> listResp = sysRoleDeptFeign.list(roleId, deptId);
		List<SysRoleDeptExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysRoleDeptExcel.class);
		ExcelUtil.write2httpResponse(response, "角色和菜单关联", SysRoleDeptExcel.class, excelList);
	}

}
