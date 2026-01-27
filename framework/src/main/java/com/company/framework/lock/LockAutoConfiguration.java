package com.company.framework.lock;

import com.company.framework.lock.impl.JvmLockClient;
import com.company.framework.lock.impl.RedissonLockClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LockAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "template.enable", name = "lock", havingValue = "jvm")
	public LockClient jvmLockClient() {
		LockClient lockClient = new JvmLockClient();
		return lockClient;
	}

	@Bean
	@ConditionalOnProperty(prefix = "template.enable", name = "lock", havingValue = "redisson")
	public LockClient redissonLockClient(RedisProperties redisProperties) {
		LockClient lockClient = new RedissonLockClient(redisProperties);
		return lockClient;
	}

    @Bean
    @ConditionalOnBean(LockClient.class)
    public AnnotationLockAspect annotationLockAspect(LockClient lockClient) {
        AnnotationLockAspect annotationLockAspect = new AnnotationLockAspect(lockClient);
        return annotationLockAspect;
    }
}
