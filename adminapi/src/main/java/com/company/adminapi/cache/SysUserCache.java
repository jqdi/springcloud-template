package com.company.adminapi.cache;

import com.company.adminapi.constants.Constants;
import com.company.adminapi.feign.SysUserFeign;
import com.company.system.api.response.SysUserResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class SysUserCache {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private SysUserFeign sysUserFeign;

    public SysUserResp getById(Integer id) {
        Cache cache = cacheManager.getCache(Constants.CacheName.SYS_USER);
        if (cache == null) {
            return sysUserFeign.getById(id);
        }
        return cache.get(id, () -> sysUserFeign.getById(id));
    }

    @Cacheable(value = Constants.CacheName.SYS_USER, key = "#id")
    public SysUserResp getById2(Integer id) {
        return sysUserFeign.getById(id);
    }
}