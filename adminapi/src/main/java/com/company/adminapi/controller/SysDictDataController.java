package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysDictDataExcel;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.SelectResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysDictDataFeign;
import com.company.system.api.request.SysDictDataReq;
import com.company.system.api.response.SysDictDataResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/sysDictData")
public class SysDictDataController {

	@Autowired
	private SysDictDataFeign sysDictDataFeign;

	@RequirePermissions("system:sysDictData:query")
	@GetMapping("/page")
	public PageResp<SysDictDataResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, String dictType, String dictCode, String dictValue, Integer dictSort, String isDefault, String status, String dictRemark) {
		return sysDictDataFeign.page(current, size, dictType, dictCode, dictValue, dictSort, isDefault, status, dictRemark);
	}

	@RequirePermissions("system:sysDictData:query")
	@GetMapping("/query")
	public SysDictDataResp query(@NotNull Integer id) {
		return sysDictDataFeign.query(id);
	}

    @OperationLog(title = "字典数据保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysDictData:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysDictDataReq sysDictDataReq) {
		return sysDictDataFeign.save(sysDictDataReq);
	}

    @OperationLog(title = "字典数据更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysDictData:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysDictDataReq sysDictDataReq) {
		return sysDictDataFeign.update(sysDictDataReq);
	}

    @OperationLog(title = "字典数据删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysDictData:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysDictDataFeign.remove(req);
	}

	@OperationLog(title = "字典数据导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysDictData:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, String dictType, String dictCode, String dictValue, Integer dictSort, String isDefault, String status, String dictRemark) {
		List<SysDictDataResp> listResp = sysDictDataFeign.list(dictType, dictCode, dictValue, dictSort, isDefault, status, dictRemark);
		List<SysDictDataExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysDictDataExcel.class);
		ExcelUtil.write2httpResponse(response, "字典数据", SysDictDataExcel.class, excelList);
	}

	@GetMapping("/queryByType")
	public List<SelectResp<String>> queryByType(String type) {
		List<SysDictDataResp> dataList = sysDictDataFeign.getByType(type);
		List<SelectResp<String>> respList = dataList.stream().map(v ->
				new SelectResp<String>().setCode(v.getDictCode()).setName(v.getDictValue())).collect(Collectors.toList());
		return respList;
	}

}
