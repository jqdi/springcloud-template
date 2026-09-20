package com.company.framework.cache.cachemanger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.company.framework.cache.ICache;
import com.google.common.util.concurrent.Striped;

/**
 * CacheManager 缓存
 */
public class CacheManagerCache implements ICache {
    private Striped<Lock> stripedLock = Striped.lock(16);// 分段锁，减少锁竞争

    private Cache cache;

    public CacheManagerCache(CacheManager cacheManager, String cacheName) {
        cache = cacheManager.getCache(cacheName);
    }

    @Override
    public void set(String key, String value) {
        if (value == null) {
            return;
        }
        cache.put(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        if (value == null) {
            return;
        }
        // 不支持灵活配置过期时间，所以忽略参数timeout、unit
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        return cache.get(key, String.class);
    }

    @Override
    public boolean del(String key) {
        return cache.evictIfPresent(key);
    }

    @Override
    public long increment(String key, long delta) {
        // CacheManager缓存不支持自增，所以通过锁的方式实现
        Lock lock4cache = stripedLock.get(key);
        try {
            lock4cache.lock();
            String value = get(key);
            if (value == null) {
                value = "0";
            }
            long result = Long.parseLong(value) + delta;
            set(key, String.valueOf(result));
            return result;
        } finally {// 一定要在finally解锁
            lock4cache.unlock();
        }
    }

    @Override
    public long increment(String key, long delta, long timeout, TimeUnit unit) {
        // CacheManager缓存不支持灵活配置过期时间，所以忽略参数timeout、unit
        return increment(key, delta);
    }
}
