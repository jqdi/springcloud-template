package com.company.framework.cache.redis;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.springframework.data.redis.connection.PoolException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.company.framework.cache.ICache;
import com.company.framework.cache.exception.ValueRetrievalException;
import com.google.common.util.concurrent.Striped;

import io.lettuce.core.RedisException;

/**
 * redis 缓存
 */
public class RedisCache implements ICache {
	private static final String NULL_VALUE = "null";// 缓存空值

    private Striped<Lock> stripedLock = Striped.lock(16);// 分段锁，减少锁竞争

	private StringRedisTemplate stringRedisTemplate;

	public RedisCache(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public void set(String key, String value) {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		opsForValue.set(key, value);
	}

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		opsForValue.set(key, value, timeout, unit);
	}
	
	@Override
	public String get(String key) {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		return opsForValue.get(key);
	}

	@Override
	public String get(String key, Callable<String> valueLoader) {
		String value = null;
		try {
			ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
			value = opsForValue.get(key);
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
				value = opsForValue.get(key);
				if (value != null) {
					return value;
				}

				if (valueLoader != null) {
					value = valueLoader.call();
					String setValue = value;
					if (setValue == null) {
						setValue = NULL_VALUE;
					}
					opsForValue.set(key, setValue);
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
		return stringRedisTemplate.delete(key);
	}

	@Override
	public long increment(String key, long delta) {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		return opsForValue.increment(key, delta);
	}

	@Override
	public long increment(String key, long delta, long timeout, TimeUnit unit) {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		Long result = opsForValue.increment(key, delta);
		stringRedisTemplate.expire(key, timeout, unit);
		return result;
	}
}