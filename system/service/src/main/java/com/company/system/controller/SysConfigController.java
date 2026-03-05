package com.company.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.company.system.api.request.RemoveReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysConfigFeign;
import com.company.system.api.request.SysConfigReq;
import com.company.system.api.response.SysConfigResp;
import com.company.system.entity.SysConfig;
import com.company.system.service.SysConfigService;

@RestController
@RequestMapping("/sysConfig")
public class SysConfigController implements SysConfigFeign {

	@Autowired
	private SysConfigService sysConfigService;

	private QueryWrapper<SysConfig> toQueryWrapper(String name, String code, String value, String configRemark) {
		QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(name)) {
			queryWrapper.like("name", name);
		}
		if (StringUtils.isNotBlank(code)) {
			queryWrapper.like("code", code);
		}
		if (StringUtils.isNotBlank(value)) {
			queryWrapper.like("value", value);
		}
		if (StringUtils.isNotBlank(configRemark)) {
			queryWrapper.like("config_remark", configRemark);
		}
		return queryWrapper;
	}

	@Override
	public PageResp<SysConfigResp> page(Long current, Long size, String name, String code, String value, String configRemark) {
		QueryWrapper<SysConfig> queryWrapper = toQueryWrapper(name, code, value, configRemark);

//		long count = sysConfigService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		Page<SysConfig> page = PageDTO.of(current, size);
		List<SysConfig> list = sysConfigService.list(page, queryWrapper);

		List<SysConfigResp> respList = PropertyUtils.copyArrayProperties(list, SysConfigResp.class);
		return PageResp.of(page.getTotal(), respList);
	}

	@Override
	public List<SysConfigResp> list(String name, String code, String value, String configRemark) {
		QueryWrapper<SysConfig> queryWrapper = toQueryWrapper(name, code, value, configRemark);

		queryWrapper.orderByDesc("id");
		List<SysConfig> list = sysConfigService.list(queryWrapper);

		List<SysConfigResp> respList = PropertyUtils.copyArrayProperties(list, SysConfigResp.class);
		return respList;
	}

	@Override
	public SysConfigResp query(Integer id) {
		SysConfig sysConfig = sysConfigService.getById(id);
		SysConfigResp sysConfigResp = PropertyUtils.copyProperties(sysConfig, SysConfigResp.class);
		return sysConfigResp;
	}

	@Override
	public Boolean save(SysConfigReq sysConfigReq) {
		SysConfig sysConfig = PropertyUtils.copyProperties(sysConfigReq, SysConfig.class);
		boolean success = sysConfigService.save(sysConfig);
		return success;
	}

	@Override
	public Boolean update(SysConfigReq sysConfigReq) {
		SysConfig sysConfig = PropertyUtils.copyProperties(sysConfigReq, SysConfig.class);
		boolean success = sysConfigService.updateById(sysConfig);
		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysConfigService.removeBatchByIds(req.getIdList());
		return success;
	}

	@Override
	public Map<String, String> getValueByCode(String code) {
		String value = sysConfigService.getValueByCode(code);
        return Collections.singletonMap("value", value);
	}

	@Override
	public Boolean updateValueByCode(String value, String code) {
		boolean success = sysConfigService.updateValueByCode(value, code);
		return success;
	}
}
