package com.company.framework.autoconfigure;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration(proxyBeanMethods = false)
@Conditional(RedisAutoConfiguration.RedisCondition.class)
public class RedisAutoConfiguration {

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	public static class RedisCondition extends AnyNestedCondition {
		RedisCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnProperty(prefix = "template.enable", name = "cache", havingValue = "redis")
		static class RedisCacheProperty {
		}

		@ConditionalOnProperty(prefix = "template.enable", name = "cache", havingValue = "combination")
		static class CombinationCacheProperty {
		}

		@ConditionalOnProperty(prefix = "template.enable", name = "message-driven", havingValue = "redis")
		static class MessageDrivenProperty {
		}
	}
}
