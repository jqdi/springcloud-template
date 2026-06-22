package com.company.framework.feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

@Configuration(proxyBeanMethods = false)
class FeignLoggerConfiguration {
    /**
     * Feign 日志级别：
     * NONE：不记录
     * BASIC：仅请求方法、URL、响应码、执行时间
     * HEADERS：请求+响应头
     * FULL：请求+响应头+请求体+响应体（最详细）
     */
    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Logger logger(@Value("${template.log.arrMaxLength:1000}") int arrMaxLength) {
        return new FeignLogger(arrMaxLength);
    }

}
