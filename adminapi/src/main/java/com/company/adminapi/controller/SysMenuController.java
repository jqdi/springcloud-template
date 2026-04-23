package com.company.adminapi.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysMenuExcel;

import com.company.token.accesscontrol.annotation.RequireLogin;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysMenuFeign;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysMenuReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.RouterResp;
import com.company.system.api.response.SysMenuResp;

@Validated
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

	@Autowired
	private SysMenuFeign sysMenuFeign;

	@RequirePermissions("system:sysMenu:query")
	@GetMapping("/page")
	public PageResp<SysMenuResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, Integer parentId,
											  String menuName, String menuType, String status, Integer visible, String perms,
											  String createTimeStart, String createTimeEnd) {
		return sysMenuFeign.page(current, size, parentId, menuName, menuType, status, visible, perms, createTimeStart, createTimeEnd);
	}

	@RequirePermissions("system:sysMenu:query")
	@GetMapping("/tree")
	public List<SysMenuResp> tree(Integer parentId, String menuName, Integer orderNum, String menuType,
										  String status, Integer visible, String perms,
										  String createTimeStart, String createTimeEnd) {
		List<SysMenuResp> sysMenuResps = sysMenuFeign.list(parentId, menuName, orderNum, menuType, status, visible, perms,
				createTimeStart, createTimeEnd);
		Map<Integer, SysMenuResp> menuMap = sysMenuResps.stream().collect(Collectors.toMap(SysMenuResp::getId, Function.identity()));
		Set<Integer> childrenId = new HashSet<>();
		sysMenuResps.forEach(menu -> {
			if (menu.getParentId() == 0) {
				return;
			}
			SysMenuResp parent = menuMap.get(menu.getParentId());
			if (parent != null) {
				if (parent.getChildrenList() == null) {
					parent.setChildrenList(new ArrayList<>());
				}
				parent.getChildrenList().add(menu);
				childrenId.add(menu.getId());
			}
		});
		List<SysMenuResp> rootList = sysMenuResps.stream().filter(m -> !childrenId.contains(m.getId())).collect(Collectors.toList());
		return rootList;
	}

	@RequirePermissions("system:sysMenu:query")
	@GetMapping("/query")
	public SysMenuResp query(@NotNull Integer id) {
		return sysMenuFeign.query(id);
	}

	@OperationLog(title = "菜单权限保存", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysMenu:save")
	@PostMapping("/save")
	public Boolean save(@RequestBody SysMenuReq sysMenuReq) {
		return sysMenuFeign.save(sysMenuReq);
	}

	@OperationLog(title = "菜单权限更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysMenu:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysMenuReq sysMenuReq) {
		return sysMenuFeign.update(sysMenuReq);
	}

	@OperationLog(title = "菜单权限删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysMenu:remove")
	@PostMapping("/remove")
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		return sysMenuFeign.remove(req);
	}

	@OperationLog(title = "菜单权限导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysMenu:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, Integer parentId, String menuName, Integer orderNum, String menuType,
					   String status, Integer visible, String perms, String createTimeStart, String createTimeEnd) {
		List<SysMenuResp> listResp = sysMenuFeign.list(parentId, menuName, orderNum, menuType, status, visible, perms,
				createTimeStart, createTimeEnd);
		List<SysMenuExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysMenuExcel.class);
		ExcelUtil.write2httpResponse(response, "菜单权限", SysMenuExcel.class, excelList);
	}

	@RequireLogin
	@GetMapping(value = "/getRouters")
	public List<RouterResp> getRouters() {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		return sysMenuFeign.getRouters(userId);
	}

}
