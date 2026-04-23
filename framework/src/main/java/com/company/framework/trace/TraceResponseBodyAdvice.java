package com.company.framework.trace;

import com.company.framework.constant.CommonConstants;
import com.company.framework.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * 响应数据增加traceId
 */
@Order(Ordered.LOWEST_PRECEDENCE) // 要加在最外层，所以优先级最低
@ControllerAdvice
@Slf4j
@RestControllerAdvice(basePackages = {CommonConstants.BASE_PACKAGE}) // 注意哦，这里要加上需要扫描的包
public class TraceResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final TraceManager traceManager;

    @Autowired
    public TraceResponseBodyAdvice(TraceManager traceManager) {
        this.traceManager = traceManager;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
    	if (body == null) {
			return null;
		}
        if (body instanceof String) {
            return body;
        }
        String bodyStr = JsonUtil.toJsonString(body);
        if (bodyStr.startsWith("[")) {// 数组无法设置traceId
            return body;
        }
        JsonNode resultNode = JsonUtil.toJsonNode(bodyStr);
        ObjectNode putResultNode = (ObjectNode) resultNode;
        putResultNode.put("traceId", traceManager.get());// 日志追踪ID
        return putResultNode;
    }
}
