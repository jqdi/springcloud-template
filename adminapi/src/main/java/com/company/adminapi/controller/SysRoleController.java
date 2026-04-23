package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysRoleExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysRoleFeign;
import com.company.system.api.request.SysRoleGrantMenuReq;
import com.company.system.api.request.SysRoleReq;
import com.company.system.api.response.SysRoleResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

	@Autowired
	private SysRoleFeign sysRoleFeign;

	@RequirePermissions("system:sysRole:query")
	@GetMapping("/page")
	public PageResp<SysRoleResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, String roleName, String roleKey, Integer roleSort, String dataScope, String status, String roleRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		return sysRoleFeign.page(current, size, roleName, roleKey, roleSort, dataScope, status, roleRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
	}

	@RequirePermissions("system:sysRole:query")
	@GetMapping("/list")
	public List<SysRoleResp> list(String roleName, String roleKey, Integer roleSort, String dataScope, String status, String roleRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		return sysRoleFeign.list(roleName, roleKey, roleSort, dataScope, status, roleRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
	}

	@RequirePermissions("system:sysRole:query")
	@GetMapping("/query")
	public SysRoleResp query(@NotNull Integer id) {
		return sysRoleFeign.query(id);
	}

    @OperationLog(title = "角色信息保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysRole:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysRoleReq sysRoleReq) {
		return sysRoleFeign.save(sysRoleReq);
	}

    @OperationLog(title = "角色信息更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysRole:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysRoleReq sysRoleReq) {
		return sysRoleFeign.update(sysRoleReq);
	}

    @OperationLog(title = "角色信息删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysRole:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysRoleFeign.remove(req);
	}

	@OperationLog(title = "角色信息导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysRole:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, String roleName, String roleKey, Integer roleSort, String dataScope, String status, String roleRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		List<SysRoleResp> listResp = sysRoleFeign.list(roleName, roleKey, roleSort, dataScope, status, roleRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<SysRoleExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysRoleExcel.class);
		ExcelUtil.write2httpResponse(response, "角色信息", SysRoleExcel.class, excelList);
	}

	@OperationLog(title = "角色授权菜单", businessType = BusinessType.GRANT)
	@RequirePermissions("system:sysRole:grantMenu")
	@PostMapping("/grantMenu")
	public Boolean grantMenu(@RequestBody SysRoleGrantMenuReq req) {
		return sysRoleFeign.grantMenu(req);
	}

}
