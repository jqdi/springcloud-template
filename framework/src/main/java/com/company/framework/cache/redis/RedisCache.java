package com.company.framework.cache.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.company.framework.cache.ICache;

/**
 * redis 缓存
 */
public class RedisCache implements ICache {
	private final StringRedisTemplate stringRedisTemplate;

	public RedisCache(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public void set(String key, String value) {
        if (value == null) {
            return;
        }
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		opsForValue.set(key, value);
	}

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
        if (value == null) {
            return;
        }
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		opsForValue.set(key, value, timeout, unit);
	}
	
	@Override
	public String get(String key) {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		return opsForValue.get(key);
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