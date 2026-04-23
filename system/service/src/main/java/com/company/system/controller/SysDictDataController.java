package com.company.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.company.system.api.request.RemoveReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysDictDataFeign;
import com.company.system.api.request.SysDictDataReq;
import com.company.system.api.response.SysDictDataResp;
import com.company.system.entity.SysDictData;
import com.company.system.service.SysDictDataService;

@RestController
@RequestMapping("/sysDictData")
public class SysDictDataController implements SysDictDataFeign {

	@Autowired
	private SysDictDataService sysDictDataService;

	private QueryWrapper<SysDictData> toQueryWrapper(String dictType, String dictCode, String dictValue, Integer dictSort, String isDefault, String status, String dictRemark) {
		QueryWrapper<SysDictData> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(dictType)) {
			queryWrapper.like("dict_type", dictType);
		}
		if (StringUtils.isNotBlank(dictCode)) {
			queryWrapper.like("dict_code", dictCode);
		}
		if (StringUtils.isNotBlank(dictValue)) {
			queryWrapper.like("dict_value", dictValue);
		}
		if (dictSort != null) {
			queryWrapper.eq("dict_sort", dictSort);
		}
		if (isDefault != null) {
			queryWrapper.eq("is_default", isDefault);
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
	public PageResp<SysDictDataResp> page(Long current, Long size, String dictType, String dictCode, String dictValue, Integer dictSort, String isDefault, String status, String dictRemark) {
		QueryWrapper<SysDictData> queryWrapper = toQueryWrapper(dictType, dictCode, dictValue, dictSort, isDefault, status, dictRemark);

		long count = sysDictDataService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysDictData> list = sysDictDataService.list(PageDTO.of(current, size), queryWrapper);

		List<SysDictDataResp> respList = PropertyUtils.copyArrayProperties(list, SysDictDataResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysDictDataResp> list(String dictType, String dictCode, String dictValue, Integer dictSort, String isDefault, String status, String dictRemark) {
		QueryWrapper<SysDictData> queryWrapper = toQueryWrapper(dictType, dictCode, dictValue, dictSort, isDefault, status, dictRemark);

		queryWrapper.orderByDesc("id");
		List<SysDictData> list = sysDictDataService.list(queryWrapper);

		List<SysDictDataResp> respList = PropertyUtils.copyArrayProperties(list, SysDictDataResp.class);
		return respList;
	}

	@Override
	public SysDictDataResp query(Integer id) {
		SysDictData sysDictData = sysDictDataService.getById(id);
		SysDictDataResp sysDictDataResp = PropertyUtils.copyProperties(sysDictData, SysDictDataResp.class);
		return sysDictDataResp;
	}

	@Override
	public Boolean save(SysDictDataReq sysDictDataReq) {
		SysDictData sysDictData = PropertyUtils.copyProperties(sysDictDataReq, SysDictData.class);
		boolean success = sysDictDataService.save(sysDictData);
		return success;
	}

	@Override
	public Boolean update(SysDictDataReq sysDictDataReq) {
		SysDictData sysDictData = PropertyUtils.copyProperties(sysDictDataReq, SysDictData.class);
		boolean success = sysDictDataService.updateById(sysDictData);
		return success;
	}

	@Override
	public Boolean remove(RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysDictDataService.removeBatchByIds(req.getIdList());
		return success;
	}

	@Override
	public List<SysDictDataResp> getByType(@RequestParam("type") String type) {
		LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SysDictData::getDictType, type);
		wrapper.orderByAsc(SysDictData::getDictSort);
		List<SysDictData> dictDataList = sysDictDataService.list(wrapper);
		return PropertyUtils.copyArrayProperties(dictDataList, SysDictDataResp.class);
	}

	@Override
	public Map<String, String> getValueByTypeCode(String type, String code) {
		String value = sysDictDataService.getValueByTypeCode(type, code);
        return Collections.singletonMap("value", value);
	}

}
