package com.company.developer.policy.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;

import com.company.developer.policy.ServicePriorityPolicy;

public class OnLineServicePriorityPolicy implements ServicePriorityPolicy {
    private static final String TAG_ONLINE = "ONLINE";

    public OnLineServicePriorityPolicy() {
    }

    public boolean support(ServiceInstance serviceInstance, List<String> developerList) {
        String developRouteTags = serviceInstance.getMetadata().get("developer_route_tag");
        if (StringUtils.isBlank(developRouteTags)) {
            return false;
        }
        boolean online = Arrays.stream(developRouteTags.split(",")).anyMatch(str -> StringUtils.equalsIgnoreCase(str, TAG_ONLINE));
        return online;
    }

    public int serverOrder(ServiceInstance serviceInstance) {
        return 2000;
    }

    public int getOrder() {
        return 200;
    }
}
