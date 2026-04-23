package com.company.system.controller;

import java.util.List;

import com.company.system.api.request.RemoveReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysRoleMenuFeign;
import com.company.system.api.request.SysRoleMenuReq;
import com.company.system.api.response.SysRoleMenuResp;
import com.company.system.entity.SysRoleMenu;
import com.company.system.service.SysRoleMenuService;

@RestController
@RequestMapping("/sysRoleMenu")
public class SysRoleMenuController implements SysRoleMenuFeign {

	@Autowired
	private SysRoleMenuService sysRoleMenuService;

	private QueryWrapper<SysRoleMenu> toQueryWrapper(Integer roleId, Integer menuId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>();
		if (roleId != null) {
			queryWrapper.eq("role_id", roleId);
		}
		if (menuId != null) {
			queryWrapper.eq("menu_id", menuId);
		}
        if (StringUtils.isNotBlank(createTimeStart)) {
            queryWrapper.ge("create_time", createTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd + " 23:59:59");
        }
        if (StringUtils.isNotBlank(updateTimeStart)) {
            queryWrapper.ge("update_time", updateTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(updateTimeEnd)) {
            queryWrapper.le("update_time", updateTimeEnd + " 23:59:59");
        }
		return queryWrapper;
	}

	@Override
	public PageResp<SysRoleMenuResp> page(Long current, Long size, Integer roleId, Integer menuId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysRoleMenu> queryWrapper = toQueryWrapper(roleId, menuId, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		long count = sysRoleMenuService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysRoleMenu> list = sysRoleMenuService.list(PageDTO.of(current, size), queryWrapper);

		List<SysRoleMenuResp> respList = PropertyUtils.copyArrayProperties(list, SysRoleMenuResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysRoleMenuResp> list(Integer roleId, Integer menuId, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysRoleMenu> queryWrapper = toQueryWrapper(roleId, menuId, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		queryWrapper.orderByDesc("id");
		List<SysRoleMenu> list = sysRoleMenuService.list(queryWrapper);

		List<SysRoleMenuResp> respList = PropertyUtils.copyArrayProperties(list, SysRoleMenuResp.class);
		return respList;
	}

	@Override
	public SysRoleMenuResp query(Integer id) {
		SysRoleMenu sysRoleMenu = sysRoleMenuService.getById(id);
		SysRoleMenuResp sysRoleMenuResp = PropertyUtils.copyProperties(sysRoleMenu, SysRoleMenuResp.class);
		return sysRoleMenuResp;
	}

	@Override
	public Boolean save(SysRoleMenuReq sysRoleMenuReq) {
		SysRoleMenu sysRoleMenu = PropertyUtils.copyProperties(sysRoleMenuReq, SysRoleMenu.class);
		boolean success = sysRoleMenuService.save(sysRoleMenu);
		return success;
	}

	@Override
	public Boolean update(SysRoleMenuReq sysRoleMenuReq) {
		SysRoleMenu sysRoleMenu = PropertyUtils.copyProperties(sysRoleMenuReq, SysRoleMenu.class);
		boolean success = sysRoleMenuService.updateById(sysRoleMenu);
		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysRoleMenuService.removeBatchByIds(req.getIdList());
		return success;
	}
}
