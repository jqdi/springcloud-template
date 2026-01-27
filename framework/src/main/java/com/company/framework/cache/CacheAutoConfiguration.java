package com.company.framework.cache;

import com.company.framework.cache.cachemanger.CacheManagerCache;
import com.company.framework.cache.guava.GuavaCache;
import com.company.framework.cache.redis.RedisCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration(proxyBeanMethods = false)
public class CacheAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "template.enable", name = "cache", havingValue = "redis")
	public ICache redisCache(StringRedisTemplate stringRedisTemplate) {
		ICache redisCache = new RedisCache(stringRedisTemplate);
		return redisCache;
	}

	@Bean
	@ConditionalOnProperty(prefix = "template.enable", name = "cache", havingValue = "guava")
	public ICache guavaCache() {
		ICache guavaCache = new GuavaCache();
		return guavaCache;
	}

	@Bean
	@ConditionalOnProperty(prefix = "template.enable", name = "cache", havingValue = "combination")
	public ICache combinationCache(StringRedisTemplate stringRedisTemplate) {
		ICache primaryCache = new RedisCache(stringRedisTemplate);
		ICache fallbackCache = new GuavaCache();
		ICache combinationCache = new CombinationCache(primaryCache, fallbackCache);
		return combinationCache;
	}

	@Bean
	@ConditionalOnProperty(prefix = "template.enable", name = "cache", havingValue = "cachemanager")
	public ICache cachemanagerCache(CacheManager cacheManager, @Value("${spring.application.name}") String cacheName) {
		ICache cacheManagerCache = new CacheManagerCache(cacheManager, cacheName);
		return cacheManagerCache;
	}

}
