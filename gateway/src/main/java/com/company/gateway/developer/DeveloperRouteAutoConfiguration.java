package com.company.gateway.developer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "developer.enabled", havingValue = "true")
@LoadBalancerClients(defaultConfiguration = {DeveloperLoadBalancerConfiguration.class})
public class DeveloperRouteAutoConfiguration {
}
