package com.company.framework.cachemanger;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Redis CacheManager 配置类
 */
@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = false)
public class RedisCacheManagerConfig {

    /**
     * 把默认的： JdkSerializationRedisSerializer 替换掉
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(CacheProperties cacheProperties, ObjectMapper redisObjectMapper) {
        return builder -> builder.cacheDefaults(createConfiguration(cacheProperties, redisObjectMapper));
    }

    /**
     * 替换掉serializeValuesWith的赋值
     * <p>
     * 参考 org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration#createConfiguration
     * </p>
     */
    private RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties, ObjectMapper redisObjectMapper) {
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config
            // .serializeValuesWith(SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
             .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
