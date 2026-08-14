package com.company.framework.gray;

import com.company.framework.gray.strategy.GrayStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 灰度负载均衡器（框架层，镜像 gateway 包），支持 developer 和 release 两种路由模式。
 */
@Slf4j
public class GrayLoadbalancer extends RoundRobinLoadBalancer {

    private final Logger logger = LoggerFactory.getLogger(GrayLoadbalancer.class);
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final GrayStrategy grayStrategy;
    private final GrayProperties grayProperties;
    private final String serviceId;

    public GrayLoadbalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                            String serviceId,
                            GrayStrategy grayStrategy,
                            GrayProperties grayProperties) {
        super(serviceInstanceListSupplierProvider, serviceId);
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.grayStrategy = grayStrategy;
        this.grayProperties = grayProperties;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        GrayContext context = buildContext(request);

        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(allServerList -> {
            Response<ServiceInstance> response = grayStrategy.choose(allServerList, context, grayProperties);
            if (supplier instanceof SelectedInstanceCallback && response.hasServer()) {
                ((SelectedInstanceCallback) supplier).selectedServiceInstance(response.getServer());
            }
            return response;
        }).doOnError(e -> {
            this.logger.error("Gray route choose ServiceInstance error :: " + e.getMessage(), e);
        }).onErrorReturn(new EmptyResponse());
    }

    private GrayContext buildContext(Request request) {
        String version = null;
        List<String> developerList = null;
        HttpHeaders httpHeaders;

        if (request instanceof DefaultRequest) {
            RequestDataContext context = (RequestDataContext) request.getContext();
            httpHeaders = Optional.ofNullable(context)
                    .map(RequestDataContext::getClientRequest)
                    .map(RequestData::getHeaders)
                    .orElse(null);

            if (httpHeaders != null) {
                if (grayProperties.isDeveloperMode()) {
                    String headers = grayProperties.getDeveloperHeaders();
                    if (StringUtils.isNotBlank(headers)) {
                        developerList = Arrays.stream(headers.split(","))
                                .filter(StringUtils::isNotBlank)
                                .map(h -> httpHeaders.getFirst(h.trim()))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList());
                    }
                } else if (grayProperties.isReleaseMode()) {
                    String headers = grayProperties.getHeaders();
                    if (StringUtils.isNotBlank(headers)) {
                        version = Arrays.stream(headers.split(","))
                                .filter(StringUtils::isNotBlank)
                                .map(h -> httpHeaders.getFirst(h.trim()))
                                .filter(StringUtils::isNotBlank)
                                .findFirst()
                                .orElse(null);
                    }
                }
            }
        } else {
            httpHeaders = null;
        }

        return new GrayContext(version, developerList, httpHeaders);
    }
}
