package com.company.admin.advice;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.company.framework.constant.CommonConstants;
import com.company.framework.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

/**
 * 根据code为响应值添加status字段,code=0:true,code=其他:false
 */
@Order(100)
@RestControllerAdvice(basePackages = { CommonConstants.BASE_PACKAGE }) // 注意哦，这里要加上需要扫描的包
public class StatusResponseBodyAdvice implements ResponseBodyAdvice<Object> {
	@Autowired
	private GracefulResponseProperties gracefulResponseProperties;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType,
			Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {

		if (data instanceof String) {
			return data;
		}

		if (data instanceof Response) {
			Response grResponse = (Response) data;
			ResponseStatus status = grResponse.getStatus();
			String code = status.getCode();

			JsonNode resultNode = JsonUtil.toJsonNode(grResponse);
			ObjectNode putResultNode = (ObjectNode) resultNode;
			putResultNode.put("status", Objects.equals(code, gracefulResponseProperties.getDefaultSuccessCode()));
			return putResultNode;
		}
		return data;
	}
}
