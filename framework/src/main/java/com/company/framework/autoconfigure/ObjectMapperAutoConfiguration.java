package com.company.framework.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.framework.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration(proxyBeanMethods = false)
public class ObjectMapperAutoConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return JsonUtil.mapper();
    }

}
