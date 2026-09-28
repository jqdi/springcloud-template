package com.company.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.company.config.nacos.EnvironmentChangeListener;
import com.company.config.nacos.LogValueConfigChangeListener;
import com.company.config.nacos.SpringValueAutoRefreshProcessor;

// @Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)// 仅nacos启动时装配
public class NacosAutoConfiguration {

    @Role(ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnClass({ContextRefresher.class, RefreshScope.class, EnvironmentChangeEvent.class})
    public SpringValueAutoRefreshProcessor springValueAutoRefreshProcessor() {
        return new SpringValueAutoRefreshProcessor();
    }

    @Bean
    public EnvironmentChangeListener environmentChangeListener(SpringValueAutoRefreshProcessor springValueAutoRefreshProcessor) {
        return new EnvironmentChangeListener(springValueAutoRefreshProcessor);
    }

    @Bean
    public LogValueConfigChangeListener logValueConfigChangeListener(NacosConfigManager nacosConfigManager) {
        ConfigService configService = nacosConfigManager.getConfigService();
        NacosConfigProperties nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();

        LogValueConfigChangeListener logValueConfigChangeListener = new LogValueConfigChangeListener();
        String dataId = nacosConfigProperties.getName();
        String group = nacosConfigProperties.getGroup();
        try {
            configService.addListener(dataId, group, logValueConfigChangeListener);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        return logValueConfigChangeListener;
    }
}