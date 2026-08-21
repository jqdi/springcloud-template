package com.company.framework.developer;

import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancerClientConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

//@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "developer.enabled", havingValue = "true")
//@LoadBalancerClients(defaultConfiguration = {DeveloperLoadBalancerConfiguration.class})
@LoadBalancerClients(defaultConfiguration = {DeveloperServiceInstanceListConfiguration.class})
//@LoadBalancerClients(defaultConfiguration = {NacosLoadBalancerClientConfiguration.class})
public class DeveloperRouteAutoConfiguration {
}
