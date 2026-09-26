package com.company.tool.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.tool.constant.Constants;
import com.company.tool.entity.AppVersion;
import com.company.tool.mapper.AppVersionMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppVersionService extends ServiceImpl<AppVersionMapper, AppVersion> {

    public AppVersion selectLastByAppCode(String appCode) {
        return baseMapper.selectLastByAppCode(appCode);
    }

    @Cacheable(value = Constants.CacheName.APP_VERSION, key = "#appCode")
    public AppVersion selectLastByAppCodeCache(String appCode) {
        return this.selectLastByAppCode(appCode);
    }

    @CacheEvict(value = Constants.CacheName.APP_VERSION, key = "#appCode")
    public void delLastByAppCodeCache(String appCode) {
    }
}