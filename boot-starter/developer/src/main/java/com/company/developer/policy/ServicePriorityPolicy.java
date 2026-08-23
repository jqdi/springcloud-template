package com.company.developer.policy;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.Ordered;

public interface ServicePriorityPolicy extends Ordered {
    boolean support(ServiceInstance serviceInstance, List<String> developerList);

    int serverOrder(ServiceInstance serviceInstance);

    default int getOrder() {
        return 0;
    }
}
