package com.company.framework.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;

import com.company.framework.cache.exception.ValueRetrievalException;

import lombok.extern.slf4j.Slf4j;

/**
 * 组合缓存（先查primary，primary不可用的情况下使用本地缓存）
 */
@Slf4j
public class CombinationCache implements ICache {

	int openingThreshold = 10;// 单位时间内请求超过openingThreshold就打开断路器
	long openingInterval = 10;
	TimeUnit openingUnit = TimeUnit.SECONDS;

	int closingThreshold = 5;// 单位时间内请求小于closingThreshold就关闭断路器
	long closingInterval = 10;
	TimeUnit closingUnit = TimeUnit.SECONDS;

	// 断路器
	EventCountCircuitBreaker breaker = new EventCountCircuitBreaker(openingThreshold, openingInterval, openingUnit,
			closingThreshold, closingInterval, closingUnit);
	{
		breaker.addChangeListener(event -> {
			log.info("event:{}", event);
		});
	}

	private ICache primaryCache;
	private ICache fallbackCache;

	public CombinationCache(ICache primaryCache, ICache fallbackCache) {
		this.primaryCache = primaryCache;
		this.fallbackCache = fallbackCache;
	}

	@Override
	public void set(String key, String value) {
		breakerNoReturn(() -> primaryCache.set(key, value), () -> fallbackCache.set(key, value));
	}

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		breakerNoReturn(() -> primaryCache.set(key, value, timeout, unit),
				() -> fallbackCache.set(key, value, timeout, unit));
	}

	@Override
	public String get(String key) {
		return breakerReturn(() -> primaryCache.get(key), () -> fallbackCache.get(key));
	}

	@Override
	public boolean del(String key) {
		return breakerReturn(() -> primaryCache.del(key), () -> fallbackCache.del(key));
	}

	@Override
	public long increment(String key, long delta) {
		return breakerReturn(() -> primaryCache.increment(key, delta), () -> fallbackCache.increment(key, delta));
	}

	@Override
	public long increment(String key, long delta, long timeout, TimeUnit unit) {
		return breakerReturn(() -> primaryCache.increment(key, delta, timeout, unit),
				() -> fallbackCache.increment(key, delta, timeout, unit));
	}

	private <T> T breakerReturn(Supplier<T> primarySupplier, Supplier<T> fallbackSupplier) {
		if (!breaker.checkState()) {
			return fallbackSupplier.get();
		}
		try {
			return primarySupplier.get();
		} catch (ValueRetrievalException e) {
			breaker.incrementAndCheckState();
			// 如果是primary导致的异常，就用fallback缓存
			return fallbackSupplier.get();
		}
	}

	private void breakerNoReturn(Runnable primaryRunnable, Runnable fallbackRunnable) {
		if (!breaker.checkState()) {
			fallbackRunnable.run();
			return;
		}
		try {
			primaryRunnable.run();
		} catch (ValueRetrievalException e) {
			breaker.incrementAndCheckState();
			// 如果是primary导致的异常，就用fallback缓存
			fallbackRunnable.run();
		}
	}
}
