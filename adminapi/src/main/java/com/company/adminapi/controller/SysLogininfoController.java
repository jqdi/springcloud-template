package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.controller.converter.SysUserIdNicknameConverter;
import com.company.adminapi.converter.annotation.RespConverter;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysLogininfoExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysLogininfoFeign;
import com.company.system.api.request.SysLogininfoReq;
import com.company.system.api.response.SysLogininfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysLogininfo")
public class SysLogininfoController {

	@Autowired
	private SysLogininfoFeign sysLogininfoFeign;

	@RespConverter(field = "sysUserId", dataSource = SysUserIdNicknameConverter.class)
	@RequirePermissions("system:sysLogininfo:query")
	@GetMapping("/page")
	public PageResp<SysLogininfoResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer sysUserId, String loginTimeStart, String loginTimeEnd, String account, String device, String platform, String operator, String version, String deviceid, String channel, String ip, String address, String source, String lang, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		return sysLogininfoFeign.page(current, size, sysUserId, loginTimeStart, loginTimeEnd, account, device, platform, operator, version, deviceid, channel, ip, address, source, lang, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
	}

	@RequirePermissions("system:sysLogininfo:query")
	@GetMapping("/query")
	public SysLogininfoResp query(@NotNull Integer id) {
		return sysLogininfoFeign.query(id);
	}

    @OperationLog(title = "用户登录日志保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysLogininfo:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysLogininfoReq sysLogininfoReq) {
		return sysLogininfoFeign.save(sysLogininfoReq);
	}

    @OperationLog(title = "用户登录日志更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysLogininfo:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysLogininfoReq sysLogininfoReq) {
		return sysLogininfoFeign.update(sysLogininfoReq);
	}

    @OperationLog(title = "用户登录日志删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysLogininfo:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysLogininfoFeign.remove(req);
	}

	@OperationLog(title = "用户登录日志导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysLogininfo:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer sysUserId, String loginTimeStart, String loginTimeEnd, String account, String device, String platform, String operator, String version, String deviceid, String channel, String ip, String address, String source, String lang, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		List<SysLogininfoResp> listResp = sysLogininfoFeign.list(sysUserId, loginTimeStart, loginTimeEnd, account, device, platform, operator, version, deviceid, channel, ip, address, source, lang, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<SysLogininfoExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysLogininfoExcel.class);
		ExcelUtil.write2httpResponse(response, "用户登录日志", SysLogininfoExcel.class, excelList);
	}

}
