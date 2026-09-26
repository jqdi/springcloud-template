package com.company.tool.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.company.tool.constant.Constants;
import com.company.tool.entity.AppInfo;
import com.company.tool.mapper.AppInfoMapper;

@Component
public class AppInfoCache {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AppInfoMapper appInfoMapper;

    public AppInfo getById(Integer id) {
        Cache cache = cacheManager.getCache(Constants.CacheName.APP_INFO);
        if (cache == null) {
            return appInfoMapper.selectById(id);
        }
        return cache.get(id, () -> appInfoMapper.selectById(id));
    }

    public void del(Integer id) {
        Cache cache = cacheManager.getCache(Constants.CacheName.APP_INFO);
        if (cache == null) {
            return;
        }
        cache.evict(id);
    }
}