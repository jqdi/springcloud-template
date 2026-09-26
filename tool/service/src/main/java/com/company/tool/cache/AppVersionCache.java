package com.company.tool.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.company.tool.constant.Constants;
import com.company.tool.entity.AppVersion;
import com.company.tool.service.AppVersionService;

@Component
public class AppVersionCache {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AppVersionService appVersionService;

    public AppVersion selectLastByAppCode(String appCode) {
        Cache cache = cacheManager.getCache(Constants.CacheName.APP_VERSION);
        if (cache == null) {
            return appVersionService.selectLastByAppCode(appCode);
        }
        return cache.get(appCode, () -> appVersionService.selectLastByAppCode(appCode));
    }

    public void del(String appCode) {
        Cache cache = cacheManager.getCache(Constants.CacheName.APP_VERSION);
        if (cache == null) {
            return;
        }
        cache.evict(appCode);
    }
}