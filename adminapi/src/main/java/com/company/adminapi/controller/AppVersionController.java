package com.company.adminapi.controller;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;

import com.company.common.response.PageResp;
import com.company.adminapi.feign.client.AppVersionFeign;
import com.company.tool.api.request.AppVersionReq;
import com.company.tool.api.response.AppVersionResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/appVersion")
public class AppVersionController {

	@Autowired
	private AppVersionFeign appVersionFeign;

	@RequirePermissions("tool:appVersion:query")
	@GetMapping("/page")
	public PageResp<AppVersionResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, String appCode, String version) {
		return appVersionFeign.page(current, size, appCode, version);
	}

	@RequirePermissions("tool:appVersion:query")
	@GetMapping("/query")
	public AppVersionResp query(@NotNull Integer id) {
		return appVersionFeign.query(id);
	}

	@OperationLog(title = "APP版本保存", businessType = BusinessType.INSERT)
	@RequirePermissions("tool:appVersion:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody AppVersionReq appVersionReq) {
		return appVersionFeign.save(appVersionReq);
	}

	@OperationLog(title = "APP版本更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("tool:appVersion:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody AppVersionReq appVersionReq) {
		return appVersionFeign.update(appVersionReq);
	}

	@OperationLog(title = "APP版本删除", businessType = BusinessType.DELETE)
	@RequirePermissions("tool:appVersion:remove")
	@PostMapping("/remove")
	public Boolean remove(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
		return appVersionFeign.remove(idList);
	}

	@RequirePermissions("tool:appVersion:query")
	@GetMapping("/list")
	public List<AppVersionResp> list(String appCode, String version) {
		return appVersionFeign.list(appCode, version);
	}
}
