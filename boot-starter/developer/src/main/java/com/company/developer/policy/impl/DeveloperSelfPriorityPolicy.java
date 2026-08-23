package com.company.developer.policy.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;

import com.company.developer.policy.ServicePriorityPolicy;

public class DeveloperSelfPriorityPolicy implements ServicePriorityPolicy {
    public DeveloperSelfPriorityPolicy() {
    }

    public boolean support(ServiceInstance serviceInstance, List<String> developerList) {
        String developer = serviceInstance.getMetadata().get("developer");
        if (StringUtils.isBlank(developer)) {
            return false;
        }
        if (developerList == null || developerList.isEmpty()) {
            return false;
        }
        return developerList.contains(developer);
    }

    public int serverOrder(ServiceInstance serviceInstance) {
        return 1000;
    }

    public int getOrder() {
        return 100;
    }
}
