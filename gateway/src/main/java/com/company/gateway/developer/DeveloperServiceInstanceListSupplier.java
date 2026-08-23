package com.company.gateway.developer;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;

import com.company.gateway.developer.policy.ServicePriorityPolicyManager;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 开发者服务列表，添加了根据请求头过滤服务列表的逻辑
 */
@Slf4j
public class DeveloperServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {
    private final ServicePriorityPolicyManager servicePriorityPolicyManager;
    private final String developerHeaders;

    public DeveloperServiceInstanceListSupplier(ServiceInstanceListSupplier delegate,
        ServicePriorityPolicyManager servicePriorityPolicyManager, String developerHeaders) {
        super(delegate);
        this.servicePriorityPolicyManager = servicePriorityPolicyManager;
        this.developerHeaders = developerHeaders;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return getDelegate().get();
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request request) {
        List<String> developerList;
        if (request instanceof DefaultRequest) {
            RequestDataContext context = (RequestDataContext)request.getContext();
            HttpHeaders httpHeaders =
                Optional.ofNullable(context).map(RequestDataContext::getClientRequest).map(RequestData::getHeaders).orElse(null);
            if (httpHeaders != null && StringUtils.isNotBlank(developerHeaders)) {
                developerList = Arrays.stream(developerHeaders.split(",")).filter(StringUtils::isNotBlank)
                    .map(httpHeaders::getFirst).filter(Objects::nonNull).collect(Collectors.toList());
            } else {
                developerList = null;
            }
        } else {
            developerList = null;
        }
        if (CollectionUtils.isEmpty(developerList)) {
            return getDelegate().get(request);
        }
        return getDelegate().get(request).map(v -> filteredByDeveloper(v, developerList));
    }

    private List<ServiceInstance> filteredByDeveloper(List<ServiceInstance> allServerList, List<String> developerList) {
        Map<Integer, List<ServiceInstance>> serverInfoMap = allServerList.stream()
            .collect(Collectors.groupingBy(v -> servicePriorityPolicyManager.serverOrder(v, developerList)));
        Optional<Integer> minOrder = serverInfoMap.keySet().stream().min(Integer::compareTo);
        return minOrder.map(serverInfoMap::get).orElse(allServerList);
    }
}
