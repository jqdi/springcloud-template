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
import com.company.system.api.feign.SysDictTypeFeign;
import com.company.system.api.request.SysDictTypeReq;
import com.company.system.api.response.SysDictTypeResp;
import com.company.system.entity.SysDictType;
import com.company.system.service.SysDictTypeService;

@RestController
@RequestMapping("/sysDictType")
public class SysDictTypeController implements SysDictTypeFeign {

	@Autowired
	private SysDictTypeService sysDictTypeService;

	private QueryWrapper<SysDictType> toQueryWrapper(String dictName, String dictType, String status, String dictRemark) {
		QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(dictName)) {
			queryWrapper.like("dict_name", dictName);
		}
		if (StringUtils.isNotBlank(dictType)) {
			queryWrapper.like("dict_type", dictType);
		}
		if (status != null) {
			queryWrapper.eq("status", status);
		}
		if (StringUtils.isNotBlank(dictRemark)) {
			queryWrapper.like("dict_remark", dictRemark);
		}
		return queryWrapper;
	}

	@Override
	public PageResp<SysDictTypeResp> page(Long current, Long size, String dictName, String dictType, String status, String dictRemark) {
		QueryWrapper<SysDictType> queryWrapper = toQueryWrapper(dictName, dictType, status, dictRemark);

		long count = sysDictTypeService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysDictType> list = sysDictTypeService.list(PageDTO.of(current, size), queryWrapper);

		List<SysDictTypeResp> respList = PropertyUtils.copyArrayProperties(list, SysDictTypeResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysDictTypeResp> list(String dictName, String dictType, String status, String dictRemark) {
		QueryWrapper<SysDictType> queryWrapper = toQueryWrapper(dictName, dictType, status, dictRemark);

		queryWrapper.orderByDesc("id");
		List<SysDictType> list = sysDictTypeService.list(queryWrapper);

		List<SysDictTypeResp> respList = PropertyUtils.copyArrayProperties(list, SysDictTypeResp.class);
		return respList;
	}

	@Override
	public SysDictTypeResp query(Integer id) {
		SysDictType sysDictType = sysDictTypeService.getById(id);
		SysDictTypeResp sysDictTypeResp = PropertyUtils.copyProperties(sysDictType, SysDictTypeResp.class);
		return sysDictTypeResp;
	}

	@Override
	public Boolean save(SysDictTypeReq sysDictTypeReq) {
		SysDictType sysDictType = PropertyUtils.copyProperties(sysDictTypeReq, SysDictType.class);
		boolean success = sysDictTypeService.save(sysDictType);
		return success;
	}

	@Override
	public Boolean update(SysDictTypeReq sysDictTypeReq) {
		SysDictType sysDictType = PropertyUtils.copyProperties(sysDictTypeReq, SysDictType.class);
		boolean success = sysDictTypeService.updateById(sysDictType);
		return success;
	}

	@Override
	public Boolean remove(RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysDictTypeService.removeBatchByIds(req.getIdList());
		return success;
	}
}
