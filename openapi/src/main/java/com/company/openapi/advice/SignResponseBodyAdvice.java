package com.company.openapi.advice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.company.framework.constant.CommonConstants;
import com.company.openapi.annotation.NoSign;
import com.company.openapi.config.SignConfiguration;
import com.company.openapi.util.SignUtil;

import cn.hutool.core.bean.BeanUtil;

/**
 * 对响应的内容进行加签
 */
@Order(90)
@RestControllerAdvice(basePackages = { CommonConstants.BASE_PACKAGE }) // 注意哦，这里要加上需要扫描的包
@ConditionalOnProperty(prefix = "sign", name = "check", havingValue = "true", matchIfMissing = true)
public class SignResponseBodyAdvice implements ResponseBodyAdvice<Object> {
	@Autowired
	private SignConfiguration signConfiguration;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
		// 如果使用了NoSign，说明响应值不需要验签
		boolean hasMethodAnnotation = returnType.hasMethodAnnotation(NoSign.class);
		if (hasMethodAnnotation) {
			return false;
		}
		boolean isAnnotationPresent = returnType.getDeclaringClass().isAnnotationPresent(NoSign.class);
		if (isAnnotationPresent) {
			return false;
		}
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType,
			Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
		if (data == null) {
			return data;
		}

		HttpHeaders headers = request.getHeaders();
		String appid = headers.getFirst("appid");
		if (StringUtils.isBlank(appid)) {
			return data;
		}
		String appsecret = signConfiguration.getAppsecret(appid);
		if (StringUtils.isBlank(appsecret)) {
			return data;
		}
		String timestamp = headers.getFirst("timestamp");
		if (StringUtils.isBlank(timestamp)) {
			return data;
		}
		long timestampLong = Long.parseLong(timestamp);
		String noncestr = headers.getFirst("noncestr");
		if (StringUtils.isBlank(noncestr)) {
			return data;
		}

        if (!isBeanObj(data)) {
            // 简单类型
            return data;
        }
        // 对象类型
        Map<String, Object> data2Map = BeanUtil.beanToMap(data);
        String sign4md5 = SignUtil.generate(appid, timestampLong, noncestr, data2Map, "", appsecret);
        data2Map.put("sign", sign4md5);
        return data2Map;
	}

	/**
	 * 判断是否是bean格式对象
	 *
	 * @param obj
	 * @return
	 */
	private static boolean isBeanObj(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof String || obj instanceof Number || obj instanceof Date || obj instanceof LocalDate
				|| obj instanceof LocalDateTime || obj instanceof Enum) {
			return false;
		}
		return true;
	}
}
