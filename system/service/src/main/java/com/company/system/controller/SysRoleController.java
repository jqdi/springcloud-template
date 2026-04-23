package com.company.system.controller;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.google.common.collect.Sets;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysRoleFeign;
import com.company.system.api.request.SysRoleGrantMenuReq;
import com.company.system.api.request.SysRoleReq;
import com.company.system.api.response.SysRoleResp;
import com.company.system.entity.SysRole;
import com.company.system.service.SysRoleMenuService;
import com.company.system.service.SysRoleService;

@RestController
@RequestMapping("/sysRole")
public class SysRoleController implements SysRoleFeign {

	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;

	private QueryWrapper<SysRole> toQueryWrapper(String roleName, String roleKey, Integer roleSort, String dataScope, String status, String roleRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(roleName)) {
			queryWrapper.like("role_name", roleName);
		}
		if (StringUtils.isNotBlank(roleKey)) {
			queryWrapper.like("role_key", roleKey);
		}
		if (roleSort != null) {
			queryWrapper.eq("role_sort", roleSort);
		}
        if (StringUtils.isNotBlank(dataScope)) {
            queryWrapper.eq("data_scope", dataScope);
        }
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
		if (StringUtils.isNotBlank(roleRemark)) {
			queryWrapper.like("role_remark", roleRemark);
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
	public PageResp<SysRoleResp> page(Long current, Long size, String roleName, String roleKey, Integer roleSort, String dataScope, String status, String roleRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysRole> queryWrapper = toQueryWrapper(roleName, roleKey, roleSort, dataScope, status, roleRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		long count = sysRoleService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysRole> list = sysRoleService.list(PageDTO.of(current, size), queryWrapper);

		List<SysRoleResp> respList = PropertyUtils.copyArrayProperties(list, SysRoleResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysRoleResp> list(String roleName, String roleKey, Integer roleSort, String dataScope, String status, String roleRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysRole> queryWrapper = toQueryWrapper(roleName, roleKey, roleSort, dataScope, status, roleRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		queryWrapper.orderByDesc("id");
		List<SysRole> list = sysRoleService.list(queryWrapper);

		List<SysRoleResp> respList = PropertyUtils.copyArrayProperties(list, SysRoleResp.class);
		return respList;
	}

	@Override
	public SysRoleResp query(Integer id) {
		SysRole sysRole = sysRoleService.getById(id);
		SysRoleResp sysRoleResp = PropertyUtils.copyProperties(sysRole, SysRoleResp.class);

		Set<Integer> menuIds = sysRoleMenuService.listMenuIdByRoleIds(Sets.newHashSet(id));
		sysRoleResp.setMenuIds(menuIds);

		return sysRoleResp;
	}

	@Override
	public Boolean save(SysRoleReq sysRoleReq) {
		SysRole sysRole = PropertyUtils.copyProperties(sysRoleReq, SysRole.class);
		boolean success = sysRoleService.save(sysRole);

		return success;
	}

	@Override
	public Boolean update(SysRoleReq sysRoleReq) {
		SysRole sysRole = PropertyUtils.copyProperties(sysRoleReq, SysRole.class);
		boolean success = sysRoleService.updateById(sysRole);

		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysRoleService.removeBatchByIds(req.getIdList());
		return success;
	}

	@Override
	public Boolean grantMenu(@RequestBody SysRoleGrantMenuReq req) {
		sysRoleMenuService.grantMenu(req.getRoleId(), req.getMenuIds());
		return true;
	}
}
