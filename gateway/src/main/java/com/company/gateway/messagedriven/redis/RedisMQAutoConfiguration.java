package com.company.gateway.messagedriven.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Conditional(RedisMQAutoConfiguration.RedisMQCondition.class)
public class RedisMQAutoConfiguration {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    public static class RedisMQCondition extends AllNestedConditions {

        RedisMQCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "template.enable", name = "message-driven", havingValue = "redis")
        static class EnableProperty {
        }

        @ConditionalOnProperty(prefix = "spring.redis", name = "host")
        static class HostProperty {
        }
    }
}
