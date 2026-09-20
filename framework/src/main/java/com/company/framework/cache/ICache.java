package com.company.framework.cache;

import java.util.concurrent.TimeUnit;

import com.company.framework.util.JsonUtil;

public interface ICache {

	void set(String key, String value);

	void set(String key, String value, long timeout, TimeUnit unit);

	default void set(String key, Object value) {
		set(key, JsonUtil.toJsonString(value));
	}

	default void set(String key, Object value, long timeout, TimeUnit unit) {
		set(key, JsonUtil.toJsonString(value), timeout, unit);
	}

	String get(String key);

	boolean del(String key);

	default <T> T get(String key, Class<T> clazz) {
		String value = get(key);
		return JsonUtil.toEntity(value, clazz);
	}

	long increment(String key, long delta);

	long increment(String key, long delta, long timeout, TimeUnit unit);

}
