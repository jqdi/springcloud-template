package com.company.system.controller;

import java.util.List;

import com.company.system.api.request.RemoveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysRoleDeptFeign;
import com.company.system.api.request.SysRoleDeptReq;
import com.company.system.api.response.SysRoleDeptResp;
import com.company.system.entity.SysRoleDept;
import com.company.system.service.SysRoleDeptService;

@RestController
@RequestMapping("/sysRoleDept")
public class SysRoleDeptController implements SysRoleDeptFeign {

	@Autowired
	private SysRoleDeptService sysRoleDeptService;

	private QueryWrapper<SysRoleDept> toQueryWrapper(Integer roleId, Integer deptId) {
		QueryWrapper<SysRoleDept> queryWrapper = new QueryWrapper<>();
		if (roleId != null) {
			queryWrapper.eq("role_id", roleId);
		}
		if (deptId != null) {
			queryWrapper.eq("dept_id", deptId);
		}
		return queryWrapper;
	}

	@Override
	public PageResp<SysRoleDeptResp> page(Long current, Long size, Integer roleId, Integer deptId) {
		QueryWrapper<SysRoleDept> queryWrapper = toQueryWrapper(roleId, deptId);

		long count = sysRoleDeptService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysRoleDept> list = sysRoleDeptService.list(PageDTO.of(current, size), queryWrapper);

		List<SysRoleDeptResp> respList = PropertyUtils.copyArrayProperties(list, SysRoleDeptResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysRoleDeptResp> list(Integer roleId, Integer deptId) {
		QueryWrapper<SysRoleDept> queryWrapper = toQueryWrapper(roleId, deptId);

		queryWrapper.orderByDesc("id");
		List<SysRoleDept> list = sysRoleDeptService.list(queryWrapper);

		List<SysRoleDeptResp> respList = PropertyUtils.copyArrayProperties(list, SysRoleDeptResp.class);
		return respList;
	}

	@Override
	public SysRoleDeptResp query(Integer id) {
		SysRoleDept sysRoleDept = sysRoleDeptService.getById(id);
		SysRoleDeptResp sysRoleDeptResp = PropertyUtils.copyProperties(sysRoleDept, SysRoleDeptResp.class);
		return sysRoleDeptResp;
	}

	@Override
	public Boolean save(SysRoleDeptReq sysRoleDeptReq) {
		SysRoleDept sysRoleDept = PropertyUtils.copyProperties(sysRoleDeptReq, SysRoleDept.class);
		boolean success = sysRoleDeptService.save(sysRoleDept);
		return success;
	}

	@Override
	public Boolean update(SysRoleDeptReq sysRoleDeptReq) {
		SysRoleDept sysRoleDept = PropertyUtils.copyProperties(sysRoleDeptReq, SysRoleDept.class);
		boolean success = sysRoleDeptService.updateById(sysRoleDept);
		return success;
	}

	@Override
	public Boolean remove(RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysRoleDeptService.removeBatchByIds(req.getIdList());
		return success;
	}
}
