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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 灰度负载均衡器，支持 developer 和 release 两种路由模式。
 *
 * <p>developer 模式：从请求头提取开发者标识列表，路由到开发者本地实例。
 * release 模式：从请求头提取灰度版本号，按泳道隔离路由。
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

    /**
     * 根据路由模式从请求中提取标识，构建灰度上下文。
     */
    private GrayContext buildContext(Request request) {
        String version = null;
        List<String> developerList = null;
        HttpHeaders httpHeaders = null;

        if (request instanceof DefaultRequest) {
            RequestDataContext context = (RequestDataContext) request.getContext();
            httpHeaders = Optional.ofNullable(context)
                    .map(RequestDataContext::getClientRequest)
                    .map(RequestData::getHeaders)
                    .orElse(null);

            if (httpHeaders != null) {
                if (grayProperties.isDeveloperMode()) {
                    // developer 模式：从 developerHeaders 配置的头取值列表
                    String headers = grayProperties.getDeveloperHeaders();
                    if (StringUtils.isNotBlank(headers)) {
                        developerList = Arrays.stream(headers.split(","))
                                .filter(StringUtils::isNotBlank)
                                .map(h -> httpHeaders.getFirst(h.trim()))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList());
                    }
                } else if (grayProperties.isReleaseMode()) {
                    // release 模式：从 headers 配置的头取版本号
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
        }

        return new GrayContext(version, developerList, httpHeaders);
    }
}
