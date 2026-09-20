package com.company.framework.cache.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.company.framework.cache.ICache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 本地内存 缓存
 */
@Slf4j
public class GuavaCache implements ICache {
    private final Cache<String, String> guavaCache = CacheBuilder.newBuilder()//
        .maximumSize(10000)//
        .expireAfterWrite(10, TimeUnit.SECONDS)//
        .removalListener(listener -> {
            log.info("key:{},value:{},cause:{}", listener.getKey(), listener.getValue(), listener.getCause());
        }).build();

    private final Cache<String, AtomicLong> incrmentCache = CacheBuilder.newBuilder()//
        .maximumSize(10000)//
        .expireAfterWrite(10 * 60, TimeUnit.SECONDS)//
        .removalListener(listener -> {
            log.info("key:{},value:{},cause:{}", listener.getKey(), listener.getValue(), listener.getCause());
        }).build();

    public GuavaCache() {}

    @Override
    public void set(String key, String value) {
        if (value == null) {
            return;
        }
        guavaCache.put(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        if (value == null) {
            return;
        }
        // guava缓存不支持灵活配置过期时间，所以忽略参数timeout、unit
        guavaCache.put(key, value);
    }

    @Override
    public String get(String key) {
        return guavaCache.getIfPresent(key);
    }

    @Override
    public boolean del(String key) {
        guavaCache.invalidate(key);
        return true;
    }

    @Override
    public long increment(String key, long delta) {
        try {
            AtomicLong atomicLong = incrmentCache.get(key, AtomicLong::new);
            return atomicLong.addAndGet(delta);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long increment(String key, long delta, long timeout, TimeUnit unit) {
        // guava缓存不支持灵活配置过期时间，所以忽略参数timeout、unit
        return increment(key, delta);
    }
}