package com.company.system.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.system.entity.SysConfig;
import com.company.system.mapper.SysConfigMapper;

@Service
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> implements IService<SysConfig> {

	public String getValueByCode(String code) {
		return baseMapper.getValueByCode(code);
	}

	public boolean updateValueByCode(String value, String code) {
		Integer affect = baseMapper.updateValueByCode(value, code);
		return affect > 0;
	}
}