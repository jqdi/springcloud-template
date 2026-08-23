package com.company.developer.policy.impl;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;

import com.company.developer.policy.ServicePriorityPolicy;

public class DefaultServicePriorityPolicy implements ServicePriorityPolicy {
    public DefaultServicePriorityPolicy() {
    }

    public boolean support(ServiceInstance serviceInstance, List<String> developerList) {
        return true;
    }

    public int serverOrder(ServiceInstance serviceInstance) {
        return 10000;
    }

    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
