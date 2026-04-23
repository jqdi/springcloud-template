package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.controller.converter.SysUserIdNicknameConverter;
import com.company.adminapi.converter.annotation.RespConverter;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysOperLogExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysOperLogFeign;
import com.company.system.api.request.SysOperLogReq;
import com.company.system.api.response.SysOperLogResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/sysOperLog")
public class SysOperLogController {

	@Autowired
	private SysOperLogFeign sysOperLogFeign;

	@RespConverter(field = "sysUserId", dataSource = SysUserIdNicknameConverter.class)
	@RequirePermissions("system:sysOperLog:query")
	@GetMapping("/page")
	public PageResp<SysOperLogResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer sysUserId, String title, Integer businessType, String method, String requestMethod, String operUrl, String operIp, String operLocation, String operParam, String jsonResult, Integer status, String errorMsg, Integer costTime, String operTimeStart, String operTimeEnd, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		return sysOperLogFeign.page(current, size, sysUserId, title, businessType, method, requestMethod, operUrl, operIp, operLocation, operParam, jsonResult, status, errorMsg, costTime, operTimeStart, operTimeEnd, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
	}

	@RequirePermissions("system:sysOperLog:query")
	@GetMapping("/query")
	public SysOperLogResp query(@NotNull Integer id) {
		return sysOperLogFeign.query(id);
	}

    @OperationLog(title = "操作日志记录保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysOperLog:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysOperLogReq sysOperLogReq) {
		return sysOperLogFeign.save(sysOperLogReq);
	}

    @OperationLog(title = "操作日志记录更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysOperLog:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysOperLogReq sysOperLogReq) {
		return sysOperLogFeign.update(sysOperLogReq);
	}

    @OperationLog(title = "操作日志记录删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysOperLog:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysOperLogFeign.remove(req);
	}

	@OperationLog(title = "操作日志记录导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysOperLog:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer sysUserId, String title, Integer businessType, String method, String requestMethod, String operUrl, String operIp, String operLocation, String operParam, String jsonResult, Integer status, String errorMsg, Integer costTime, String operTimeStart, String operTimeEnd, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		List<SysOperLogResp> listResp = sysOperLogFeign.list(sysUserId, title, businessType, method, requestMethod, operUrl, operIp, operLocation, operParam, jsonResult, status, errorMsg, costTime, operTimeStart, operTimeEnd, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<SysOperLogExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysOperLogExcel.class);
		ExcelUtil.write2httpResponse(response, "操作日志记录", SysOperLogExcel.class, excelList);
	}

}
