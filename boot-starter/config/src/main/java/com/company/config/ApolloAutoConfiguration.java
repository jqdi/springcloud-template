package com.company.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.company.config.apollo.PropertiesRefresher;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;

// @Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
@ConditionalOnProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED)
public class ApolloAutoConfiguration {

    @Bean
    public PropertiesRefresher propertiesRefresher() {
        return new PropertiesRefresher();
    }

}