package com.company.developer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

@ConditionalOnProperty(name = "developer.enabled", havingValue = "true")
@LoadBalancerClients(defaultConfiguration = {DeveloperServiceInstanceListConfiguration.class})
public class DeveloperRouteAutoConfiguration {
}
