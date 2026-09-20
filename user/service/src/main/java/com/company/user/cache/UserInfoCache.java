package com.company.user.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.company.user.constant.Constants;
import com.company.user.entity.UserInfo;
import com.company.user.service.UserInfoService;

@Component
public class UserInfoCache {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserInfoService userInfoService;

    public UserInfo getById(Integer id) {
        Cache cache = cacheManager.getCache(Constants.CacheName.USER_INFO);
        if (cache == null) {
            return userInfoService.getById(id);
        }
        return cache.get(id, () -> userInfoService.getById(id));
    }

    public void del(Integer id) {
        Cache cache = cacheManager.getCache(Constants.CacheName.USER_INFO);
        if (cache == null) {
            return;
        }
        cache.evict(id);
    }
}
