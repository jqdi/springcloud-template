package com.company.framework.globalresponse.gracefulresponse.feign;

import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.company.framework.globalresponse.gracefulresponse.feign.converter.GracefulResponseHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.EnableGracefulResponse;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;

import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;

/**
 * 参考FeignClientsConfiguration
 */
@Configuration(proxyBeanMethods = false)
@EnableGracefulResponse
public class GracefulResponseFeignClientsConfiguration {

    /**
     * 替换默认的feignDecoder，使得返回值可以适配GracefulResponse
     */
    @Bean
    @ConditionalOnMissingBean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
        ObjectProvider<HttpMessageConverterCustomizer> customizers, ObjectMapper objectMapper,
        GracefulResponseProperties gracefulResponseProperties, ResponseFactory responseBeanFactory) {
        return new OptionalDecoder(new ResponseEntityDecoder(new GracefulResponseDecoder(messageConverters, customizers,
            objectMapper, gracefulResponseProperties, responseBeanFactory)));
    }

    @Bean
    public Object addGracefulResponseHttpMessageConverter(RestTemplate restTemplate, ObjectMapper objectMapper,
        GracefulResponseProperties gracefulResponseProperties, ResponseFactory responseBeanFactory) {
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        int i = 0;
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                break;
            }
            i++;
        }
        // 将GracefulResponseHttpMessageConverter添加到MappingJackson2HttpMessageConverter之前
        messageConverters.add(i,
            new GracefulResponseHttpMessageConverter(objectMapper, gracefulResponseProperties, responseBeanFactory));
        return new Object();
    }
}
