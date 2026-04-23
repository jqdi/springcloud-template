package com.company.gateway.filter;

import com.company.gateway.constant.CommonConstants;
import com.company.gateway.util.JsonUtil;
import com.company.gateway.util.WebUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * SQL注入过滤
 */
@Slf4j
@Component
public class SqlInjectFilter implements GlobalFilter, Ordered {

	@Value("${template.sqlInjectFilter.keywords:}")
	private String[] sqlInjectKeywords;

	@Value("${template.sqlInjectFilter.enable:true}")
	private boolean enable;

	@Override
	public int getOrder() {
		return CommonConstants.FilterOrdered.SQLINJECT;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (!enable) {
			return chain.filter(exchange);
		}

		if (sqlInjectKeywords == null || sqlInjectKeywords.length == 0) {
			return chain.filter(exchange);
		}

		try {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			HttpHeaders headers = request.getHeaders();

			String contentType = Optional.ofNullable(headers).map(HttpHeaders::getContentType).map(MediaType::toString)
					.orElse(null);
			if (contentType != null && contentType.contains("application/json")) {
				return DataBufferUtils.join(request.getBody()).map(dataBuffer -> {
					byte[] bytes = new byte[dataBuffer.readableByteCount()];
					dataBuffer.read(bytes);
					DataBufferUtils.release(dataBuffer);
					return bytes;
				}).defaultIfEmpty(new byte[0]).flatMap(bytes -> {
					DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
					ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
						@Override
						public Flux<DataBuffer> getBody() {
							if (bytes.length > 0) {
								return Flux.just(dataBufferFactory.wrap(bytes));
							}
							return Flux.empty();
						}
					};

					String bodyStr = new String(bytes);
					if (StringUtils.isNotBlank(bodyStr)) {
						JsonNode bodyNode = JsonUtil.toJsonNode(bodyStr);
						if (bodyNode != null) {
							Iterator<Entry<String, JsonNode>> fields = bodyNode.fields();
							while (fields.hasNext()) {
								Entry<String, JsonNode> entry = fields.next();
								String value = Optional.ofNullable(entry.getValue()).map(JsonNode::asText).orElse(null);
								if (StringUtils.isBlank(value)) {
									continue;
								}
								for (String keyword : sqlInjectKeywords) {
									if (StringUtils.containsIgnoreCase(value, keyword)) {
										String key = entry.getKey();
										log.warn("参数‘{}’存在sql注入风险:{}", key, value);
										return writeError(response, MessageFormat.format("参数‘{0}’不合法", key));
									}
								}
							}
						}
					}
					return chain.filter(exchange.mutate().request(decorator).build());
				});
			}

			Map<String, String> reqParamMap = WebUtil.getReqParam(request);
			Set<Entry<String, String>> entrySet = reqParamMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String value = entry.getValue();
				if (StringUtils.isBlank(value)) {
					continue;
				}
				for (String keyword : sqlInjectKeywords) {
					if (StringUtils.containsIgnoreCase(value, keyword)) {
						String key = entry.getKey();
						log.warn("参数‘{}’存在sql注入风险:{}", key, value);
						return writeError(response, MessageFormat.format("参数‘{0}’不合法", key));
					}
				}
			}

			Mono<Void> mono = chain.filter(exchange.mutate().request(request).build());
			return mono;
		} catch (Exception e) {
			// 避免filter逻辑中的任何异常，直接转发请求
			return chain.filter(exchange);
		}
	}

	private Mono<Void> writeError(ServerHttpResponse response, String message) {
		Map<String, Object> errorMap = Maps.newHashMap();
		errorMap.put("code", "1");
		errorMap.put("msg", message);
		String failJson = JsonUtil.toJsonString(errorMap);
		byte[] failJsonBytes = failJson.getBytes(StandardCharsets.UTF_8);

		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		return response.writeAndFlushWith(Mono.just(ByteBufMono.just(response.bufferFactory().wrap(failJsonBytes))));
	}
}
