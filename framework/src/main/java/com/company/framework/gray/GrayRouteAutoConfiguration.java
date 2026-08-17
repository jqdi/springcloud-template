package com.company.framework.gray;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

/**
 * 灰度路由自动配置（框架层，镜像 gateway 包）。
 *
 * <p>通过 {@code gray.enabled=true} 开启，注册 {@link GrayLoadBalancerConfiguration} 作为默认 LB 配置。
 * 通过 {@code gray.mode} 切换路由策略：developer（开发调试）| release（灰度发布）。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "gray.enabled", havingValue = "true")
@EnableConfigurationProperties(GrayProperties.class)
@LoadBalancerClients(defaultConfiguration = {GrayLoadBalancerConfiguration.class})
public class GrayRouteAutoConfiguration {
}
