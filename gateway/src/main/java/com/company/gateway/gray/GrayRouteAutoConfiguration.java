package com.company.gateway.gray;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

/**
 * 灰度路由自动配置。
 *
 * <p>通过 {@code gray.enabled=true} 开启，注册 {@link GrayLoadBalancerConfiguration} 作为默认 LB 配置。
 * 与 developer 模块互斥：同一环境不可同时启用 {@code developer.enabled} 和 {@code gray.enabled}。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "gray.enabled", havingValue = "true")
@EnableConfigurationProperties(GrayProperties.class)
@LoadBalancerClients(defaultConfiguration = {GrayLoadBalancerConfiguration.class})
public class GrayRouteAutoConfiguration {
}
