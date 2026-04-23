package com.company.system.controller;

import java.util.List;

import com.company.system.api.request.RemoveReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysDeptFeign;
import com.company.system.api.request.SysDeptReq;
import com.company.system.api.response.SysDeptResp;
import com.company.system.entity.SysDept;
import com.company.system.service.SysDeptService;

@RestController
@RequestMapping("/sysDept")
public class SysDeptController implements SysDeptFeign {

	@Autowired
	private SysDeptService sysDeptService;

	private QueryWrapper<SysDept> toQueryWrapper(Integer parentId, String parentIds, String name, Integer orderNum, String status) {
		QueryWrapper<SysDept> queryWrapper = new QueryWrapper<>();
		if (parentId != null) {
			queryWrapper.eq("parent_id", parentId);
		}
		if (StringUtils.isNotBlank(parentIds)) {
			queryWrapper.like("parent_ids", parentIds);
		}
		if (StringUtils.isNotBlank(name)) {
			queryWrapper.like("name", name);
		}
		if (orderNum != null) {
			queryWrapper.eq("order_num", orderNum);
		}
		if (status != null) {
			queryWrapper.eq("status", status);
		}
		return queryWrapper;
	}

	@Override
	public PageResp<SysDeptResp> page(Long current, Long size, Integer parentId, String parentIds, String name, Integer orderNum, String status) {
		QueryWrapper<SysDept> queryWrapper = toQueryWrapper(parentId, parentIds, name, orderNum, status);

		long count = sysDeptService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysDept> list = sysDeptService.list(PageDTO.of(current, size), queryWrapper);

		List<SysDeptResp> respList = PropertyUtils.copyArrayProperties(list, SysDeptResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysDeptResp> list(Integer parentId, String parentIds, String name, Integer orderNum, String status) {
		QueryWrapper<SysDept> queryWrapper = toQueryWrapper(parentId, parentIds, name, orderNum, status);

		queryWrapper.orderByDesc("id");
		List<SysDept> list = sysDeptService.list(queryWrapper);

		List<SysDeptResp> respList = PropertyUtils.copyArrayProperties(list, SysDeptResp.class);
		return respList;
	}

	@Override
	public SysDeptResp query(Integer id) {
		SysDept sysDept = sysDeptService.getById(id);
		SysDeptResp sysDeptResp = PropertyUtils.copyProperties(sysDept, SysDeptResp.class);
		return sysDeptResp;
	}

	@Override
	public Boolean save(SysDeptReq sysDeptReq) {
		SysDept sysDept = PropertyUtils.copyProperties(sysDeptReq, SysDept.class);
		boolean success = sysDeptService.save(sysDept);
		return success;
	}

	@Override
	public Boolean update(SysDeptReq sysDeptReq) {
		SysDept sysDept = PropertyUtils.copyProperties(sysDeptReq, SysDept.class);
		boolean success = sysDeptService.updateById(sysDept);
		return success;
	}

	@Override
	public Boolean remove(RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysDeptService.removeBatchByIds(req.getIdList());
		return success;
	}
}
