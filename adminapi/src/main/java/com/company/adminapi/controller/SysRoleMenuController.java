package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysRoleMenuExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysRoleMenuFeign;
import com.company.system.api.request.SysRoleMenuReq;
import com.company.system.api.response.SysRoleMenuResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysRoleMenu")
public class SysRoleMenuController {

	@Autowired
	private SysRoleMenuFeign sysRoleMenuFeign;

	@RequirePermissions("system:sysRoleMenu:query")
	@GetMapping("/page")
	public PageResp<SysRoleMenuResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer roleId, Integer menuId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		return sysRoleMenuFeign.page(current, size, roleId, menuId, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
	}

	@RequirePermissions("system:sysRoleMenu:query")
	@GetMapping("/query")
	public SysRoleMenuResp query(@NotNull Integer id) {
		return sysRoleMenuFeign.query(id);
	}

    @OperationLog(title = "角色和菜单关联保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysRoleMenu:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysRoleMenuReq sysRoleMenuReq) {
		return sysRoleMenuFeign.save(sysRoleMenuReq);
	}

    @OperationLog(title = "角色和菜单关联更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysRoleMenu:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysRoleMenuReq sysRoleMenuReq) {
		return sysRoleMenuFeign.update(sysRoleMenuReq);
	}

    @OperationLog(title = "角色和菜单关联删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysRoleMenu:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysRoleMenuFeign.remove(req);
	}

	@OperationLog(title = "角色和菜单关联导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysRoleMenu:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer roleId, Integer menuId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		List<SysRoleMenuResp> listResp = sysRoleMenuFeign.list(roleId, menuId, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<SysRoleMenuExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysRoleMenuExcel.class);
		ExcelUtil.write2httpResponse(response, "角色和菜单关联", SysRoleMenuExcel.class, excelList);
	}

}
