package com.company.developer.policy;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cloud.client.ServiceInstance;

import com.company.developer.policy.impl.DefaultServicePriorityPolicy;

public class ServicePriorityPolicyManager {
    private final List<ServicePriorityPolicy> servicePriorityPolicies;
    private ServicePriorityPolicy defaultServicePriorityPolicy;

    public ServicePriorityPolicyManager(List<ServicePriorityPolicy> servicePriorityPolicies) {
        this.servicePriorityPolicies = servicePriorityPolicies.stream().sorted(Comparator.comparingInt(ServicePriorityPolicy::getOrder)).collect(Collectors.toList());
        this.defaultServicePriorityPolicy = new DefaultServicePriorityPolicy();
    }

    public void setDefaultServicePriorityPolicy(ServicePriorityPolicy defaultServicePriorityPolicy) {
        this.defaultServicePriorityPolicy = Optional.ofNullable(defaultServicePriorityPolicy).orElse(new DefaultServicePriorityPolicy());
    }

    public int serverOrder(ServiceInstance serviceInstance, List<String> developerList) {
        Iterator<ServicePriorityPolicy> var2 = this.servicePriorityPolicies.iterator();

        ServicePriorityPolicy priorityPolicy;
        do {
            if (!var2.hasNext()) {
                return this.defaultServicePriorityPolicy.serverOrder(serviceInstance);
            }

            priorityPolicy = var2.next();
        } while (!priorityPolicy.support(serviceInstance, developerList));

        return priorityPolicy.serverOrder(serviceInstance);
    }
}
