package com.company.framework.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.company.framework.util.JsonUtil;

@Configuration(proxyBeanMethods = false)
public class HttpMessageConverterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ObjectMapper.class)
    ObjectMapper objectMapper() {
        return JsonUtil.mapper();
    }

    @Bean
    @ConditionalOnMissingBean(value = MappingJackson2HttpMessageConverter.class)
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
