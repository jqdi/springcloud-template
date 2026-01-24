package com.company.gateway.filter;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.company.gateway.constant.CommonConstants;
import com.company.gateway.constant.HeaderConstants;
import com.company.gateway.trace.TraceManager;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 对Skywalking tid无法打印进行修复
 * skywalking tid 在gateway中无法传递，原因未知，但是使用WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId);可以获取到tid，故暂时使用编码方式解决
 */
@Slf4j
@Component
public class SkywalkingTraceFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return CommonConstants.FilterOrdered.TRACE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String tid = WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId);
        MDC.put("tid", tid);
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            MDC.remove("tid");
        }));
    }
}
