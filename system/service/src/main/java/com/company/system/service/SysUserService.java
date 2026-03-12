package com.company.system.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.system.entity.SysUser;
import com.company.system.mapper.SysUserMapper;


@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> implements IService<SysUser> {

	public SysUser getByAccount(String account) {
		return baseMapper.selectByAccount(account);
	}

	public List<SysUser> selectByIds(Collection<Integer> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		ids = ids.stream().collect(Collectors.toSet());// 去重
		return baseMapper.selectByIds(ids);
	}

    @Cacheable(value = "system:sysUser", key = "#id")
    public SysUser selectByIdCache(Integer id) {
        return baseMapper.selectById(id);
    }

    @CacheEvict(value = "system:sysUser", key = "#id")
    public void delByIdCache(Integer id) {
    }
}