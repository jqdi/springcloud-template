package com.company.gateway.filter;

import java.nio.charset.StandardCharsets;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.company.gateway.constant.CommonConstants;
import com.company.gateway.constant.HeaderConstants;
import com.company.gateway.trace.TraceManager;
import com.company.gateway.util.IpUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应参数日志打印
 */
@Slf4j
@Component
public class ResponseFilter implements GlobalFilter, Ordered {

	@Value("${filter.response.enable:true}")
	private boolean enable;
	@Autowired
	private TraceManager traceManager;

	@Override
	public int getOrder() {
		return CommonConstants.FilterOrdered.RESPONSE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (!enable) {
			return chain.filter(exchange);
		}
		ServerHttpRequest request = exchange.getRequest();
		String requestIp = IpUtil.getRequestIp(request);
		HttpMethod method = request.getMethod();
		String path = request.getURI().getPath();

		ServerHttpResponse originalResponse = exchange.getResponse();
		DataBufferFactory bufferFactory = originalResponse.bufferFactory();

		String uniqueKey = request.getHeaders().getFirst(HeaderConstants.TRACE_ID);

		long start = System.currentTimeMillis();
		ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				if (body instanceof Flux) {
					Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
					return super.writeWith(fluxBody.map(dataBuffer -> {
						byte[] content = new byte[dataBuffer.readableByteCount()];
						dataBuffer.read(content);
						DataBufferUtils.release(dataBuffer);
						String responseBody = new String(content, StandardCharsets.UTF_8);
						traceManager.put(uniqueKey);
						log.info("{} {} {} {}ms,response:{}", method, requestIp, path, System.currentTimeMillis() - start, responseBody);
						traceManager.remove();
						return bufferFactory.wrap(content);
					}));
				}
				return super.writeWith(body);
			}
		};

		return chain.filter(exchange.mutate().response(decoratedResponse).build());
	}
}
