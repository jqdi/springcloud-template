package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysUserRoleExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysUserRoleFeign;
import com.company.system.api.request.SysUserRoleReq;
import com.company.system.api.response.SysUserRoleResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/sysUserRole")
public class SysUserRoleController {

	@Autowired
	private SysUserRoleFeign sysUserRoleFeign;

	@RequirePermissions("system:sysUserRole:query")
	@GetMapping("/page")
	public PageResp<SysUserRoleResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer userId, Integer roleId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		return sysUserRoleFeign.page(current, size, userId, roleId, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
	}

	@RequirePermissions("system:sysUserRole:query")
	@GetMapping("/query")
	public SysUserRoleResp query(@NotNull Integer id) {
		return sysUserRoleFeign.query(id);
	}

	@RequirePermissions("system:sysUserRole:query")
	@GetMapping("/listRoleIdByUserId")
	public Set<Integer> listRoleIdByUserId(Integer userId) {
		return sysUserRoleFeign.listRoleIdByUserId(userId);
	}

    @OperationLog(title = "用户和角色关联保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysUserRole:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysUserRoleReq sysUserRoleReq) {
		return sysUserRoleFeign.save(sysUserRoleReq);
	}

    @OperationLog(title = "用户和角色关联更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysUserRole:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysUserRoleReq sysUserRoleReq) {
		return sysUserRoleFeign.update(sysUserRoleReq);
	}

    @OperationLog(title = "用户和角色关联删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysUserRole:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysUserRoleFeign.remove(req);
	}

	@OperationLog(title = "用户和角色关联导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysUserRole:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer userId, Integer roleId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		List<SysUserRoleResp> listResp = sysUserRoleFeign.list(userId, roleId, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<SysUserRoleExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysUserRoleExcel.class);
		ExcelUtil.write2httpResponse(response, "用户和角色关联", SysUserRoleExcel.class, excelList);
	}

}
