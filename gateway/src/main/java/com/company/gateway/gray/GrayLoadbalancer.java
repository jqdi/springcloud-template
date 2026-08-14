package com.company.gateway.gray;

import com.company.gateway.gray.strategy.GrayStrategy;
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
import java.util.Optional;

/**
 * 灰度负载均衡器，在 RoundRobinLoadBalancer 基础上添加基于版本号的泳道隔离路由。
 *
 * <p>从请求头提取灰度版本号，构建 {@link GrayContext}，委托 {@link GrayStrategy} 选实例。
 * 配合 {@link GrayDyeFilter} 流量染色实现端到端灰度路由。
 */
@Slf4j
public class GrayLoadbalancer extends RoundRobinLoadBalancer {

    private final Logger logger = LoggerFactory.getLogger(GrayLoadbalancer.class);
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final GrayStrategy grayStrategy;
    private final GrayProperties grayProperties;
    private final String grayHeaders;
    private final String serviceId;

    public GrayLoadbalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                            String serviceId,
                            GrayStrategy grayStrategy,
                            GrayProperties grayProperties,
                            String grayHeaders) {
        super(serviceInstanceListSupplierProvider, serviceId);
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.grayStrategy = grayStrategy;
        this.grayProperties = grayProperties;
        this.grayHeaders = grayHeaders;
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

    /**
     * 从请求中提取灰度版本号，构建灰度上下文。
     */
    private GrayContext buildContext(Request request) {
        String version = null;
        HttpHeaders httpHeaders = null;

        if (request instanceof DefaultRequest) {
            RequestDataContext context = (RequestDataContext) request.getContext();
            httpHeaders = Optional.ofNullable(context)
                    .map(RequestDataContext::getClientRequest)
                    .map(RequestData::getHeaders)
                    .orElse(null);
            if (httpHeaders != null && StringUtils.isNotBlank(grayHeaders)) {
                // 取第一个非空的配置请求头值作为版本号
                version = Arrays.stream(grayHeaders.split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(httpHeaders::getFirst)
                        .filter(StringUtils::isNotBlank)
                        .findFirst()
                        .orElse(null);
            }
        }

        return new GrayContext(version, httpHeaders);
    }
}
