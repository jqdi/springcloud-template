package com.company.gateway.config.nacos;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

/**
 * 实现springcloud应用@Value配置的自动刷新(跟nacos无关，但是apollo无需使用该类，所以只给nacos使用)
 * <p>
 * 参考网页：https://blog.csdn.net/echizao1839/article/details/122319170
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ContextRefresher.class, RefreshScope.class, EnvironmentChangeEvent.class})
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)// 仅nacos启动时装配
public class SpringValueAutoRefreshConfiguration {

    @Role(ROLE_INFRASTRUCTURE)
    @Bean
    public BeanPostProcessor springValueAutoRefreshProcessor() {
        return new SpringValueAutoRefreshProcessor();
    }

}
