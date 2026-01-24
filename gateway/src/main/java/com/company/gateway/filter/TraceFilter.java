package com.company.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.company.gateway.constant.CommonConstants;
import com.company.gateway.constant.HeaderConstants;
import com.company.gateway.trace.TraceManager;

import reactor.core.publisher.Mono;

/**
 * 对客户端请求添加追踪ID
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

	@Autowired
	private TraceManager traceManager;

	@Override
	public int getOrder() {
		return CommonConstants.FilterOrdered.TRACE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		traceManager.put();
		request = request.mutate().header(HeaderConstants.TRACE_ID, traceManager.get()).build();

		return chain.filter(exchange.mutate().request(request).build()).then(Mono.fromRunnable(() -> {
			traceManager.remove();
		}));
	}
}
