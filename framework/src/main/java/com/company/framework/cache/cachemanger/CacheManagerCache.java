package com.company.framework.cache.cachemanger;

import com.company.framework.cache.ICache;
import com.company.framework.cache.exception.ValueRetrievalException;
import com.google.common.util.concurrent.Striped;
import io.lettuce.core.RedisException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.PoolException;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CacheManager 缓存
 */
public class CacheManagerCache implements ICache {
	private static final String NULL_VALUE = "null";// 缓存空值

    private Striped<Lock> stripedLock = Striped.lock(16);// 分段锁，减少锁竞争

    private Cache cache;

    public CacheManagerCache(CacheManager cacheManager, String cacheName) {
        cache = cacheManager.getCache(cacheName);
    }

    @Override
    public void set(String key, String value) {
        cache.put(key, value);
    }

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		// 不支持灵活配置过期时间，所以忽略参数timeout、unit
		cache.put(key, value);
	}

	@Override
	public String get(String key) {
		return cache.get(key, String.class);
	}

	@Override
	public String get(String key, Callable<String> valueLoader) {
		String value = null;
		try {
            value =  cache.get(key, String.class);
			if (value != null) {
				if (NULL_VALUE.equals(value)) {
					return null;
				}
				return value;
			}

			// redis没有问题的情况下，加载数据需要做同步操作，防止大量请求执行valueLoader获取数据
            Lock lock4cache = stripedLock.get(key);
            try {
				lock4cache.lock();
                value =  cache.get(key, String.class);
				if (value != null) {
					return value;
				}

				if (valueLoader != null) {
					value = valueLoader.call();
					String setValue = value;
					if (setValue == null) {
						setValue = NULL_VALUE;
					}
                    cache.put(key, setValue);
				}
			} finally {// 一定要在finally解锁
				lock4cache.unlock();
			}
		} catch (Exception e) {
			if (e instanceof PoolException//
					|| e instanceof RedisException//
					|| e.getCause() instanceof RedisException//
			) {
				throw new ValueRetrievalException(e);
			} else {
				throw new RuntimeException(e);
			}
		}
		return value;
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
        // CacheManager缓存不支持灵活配置过期时间，所以忽略参数timeout、unit
        return increment(key, delta);
	}
}
