package com.company.framework.cache.guava;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.company.framework.cache.ICache;
import com.company.framework.cache.exception.ValueRetrievalException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.google.common.util.concurrent.Striped;
import lombok.extern.slf4j.Slf4j;

/**
 * 本地内存 缓存
 */
@Slf4j
public class GuavaCache implements ICache {
    private Striped<Lock> stripedLock = Striped.lock(16);// 分段锁，减少锁竞争

	private Cache<String, String> guavaCache = CacheBuilder.newBuilder()//
			.maximumSize(10000)//
			.expireAfterWrite(10, TimeUnit.SECONDS)//
			.removalListener(listener -> {
				log.info("key:{},value:{},cause:{}", listener.getKey(), listener.getValue(), listener.getCause());
			}).build();

	public GuavaCache() {
	}

	@Override
	public void set(String key, String value) {
		guavaCache.put(key, value);
	}

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		// guava缓存不支持灵活配置过期时间，所以忽略参数timeout、unit
		guavaCache.put(key, value);
	}
	
	@Override
	public String get(String key) {
		return guavaCache.getIfPresent(key);
	}
	
	@Override
	public String get(String key, Callable<String> valueLoader) {
		String value = null;
		try {
			value = guavaCache.get(key, valueLoader);
		} catch (ExecutionException e) {
			throw new ValueRetrievalException(e);
		}
		return value;
	}

	@Override
	public boolean del(String key) {
		guavaCache.invalidate(key);
		return true;
	}

	@Override
	public long increment(String key, long delta) {
		// guava缓存不支持自增，所以通过锁的方式实现
        Lock lock4cache = stripedLock.get(key);
		try {
            lock4cache.lock();
			
			String value = get(key, () -> "0");
			long result = Long.parseLong(value) + delta;
			set(key, String.valueOf(result));
			return result;
		} finally {// 一定要在finally解锁
			lock4cache.unlock();
		}
	}

	@Override
	public long increment(String key, long delta, long timeout, TimeUnit unit) {
		// guava缓存不支持灵活配置过期时间，所以忽略参数timeout、unit
        return increment(key, delta);
	}
}