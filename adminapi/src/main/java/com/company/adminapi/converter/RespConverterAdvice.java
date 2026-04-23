package com.company.adminapi.converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.company.adminapi.converter.annotation.RespConverter;
import com.company.adminapi.converter.annotation.RespConverters;
import com.company.adminapi.converter.ds.ConverterDataSource;
import com.company.framework.constant.CommonConstants;
import com.company.framework.util.JsonUtil;
import com.company.system.api.response.PageResp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 对响应的Resp实体进行字段值转换
 */
@Slf4j
@Order(2)
@RestControllerAdvice(basePackages = { CommonConstants.BASE_PACKAGE }) // 注意哦，这里要加上需要扫描的包
public class RespConverterAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
		// 如果使用了RespConverter，说明响应值需要转换
		return returnType.hasMethodAnnotation(RespConverter.class)
				|| returnType.hasMethodAnnotation(RespConverters.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType,
			Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
		if (body == null) {
			return body;
		}

		RespConverter[] values = null;
		RespConverters respConverters = returnType.getMethodAnnotation(RespConverters.class);
		if (respConverters != null) {
			values = respConverters.value();
		} else {
			RespConverter respConverter = returnType.getMethodAnnotation(RespConverter.class);
			values = new RespConverter[] { respConverter };
		}

		if (body instanceof PageResp) {// 分页对象
			@SuppressWarnings("unchecked")
			PageResp<Object> pageResp = (PageResp<Object>) body;
			List<Object> list = pageResp.getList();
			if (CollectionUtils.isEmpty(list)) {
				return body;
			}
			List<Object> newList = exeConvert(list, values);
			pageResp.setList(newList);
		} else if (body instanceof List) {// 列表对象
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) body;
			if (CollectionUtils.isEmpty(list)) {
				return body;
			}
			List<Object> newList = exeConvert(list, values);
			body = newList;
		} else {// 实体对象
			Object entity = body;
			List<Object> list = Lists.newArrayList(entity);
			List<Object> newList = exeConvert(list, values);
			Object newObject = newList.get(0);
			body = newObject;
		}
		return body;
	}

	/**
	 * 执行转换
	 *
	 * @param list
	 * @param values
	 * @return
	 */
	private List<Object> exeConvert(List<Object> list, RespConverter[] values) {
		Object entityResp0 = list.get(0);
		Map<RespConverter, FieldConverterStorage> fieldConverterStorageMap = Maps.newHashMap();
		for (RespConverter respConverter : values) {
			String fieldName = respConverter.field();
			Field field = null;
			try {
				field = entityResp0.getClass().getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				log.error("get field error", e);
				continue;
			}
			field.setAccessible(true);
			Field finalField = field;
			Set<Object> valueSet = list.stream().map(v -> ReflectionUtils.getField(finalField, v))
					.collect(Collectors.toSet());
			field.setAccessible(false);
			ConverterDataSource converterDataSource = null;
			try {
				converterDataSource = respConverter.dataSource().getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				log.error("newInstance error", e);
				continue;
			}
			Map<Object, String> fieldConverterMap = converterDataSource.getFieldConverterValue(valueSet);
			fieldConverterStorageMap.put(respConverter, new FieldConverterStorage(field, fieldConverterMap));
		}

		List<Object> list2 = list.stream().map(v -> {
			JsonNode jsonNode = JsonUtil.toJsonNode(v);
			ObjectNode objectNode = (ObjectNode) jsonNode;

			Set<Entry<RespConverter, FieldConverterStorage>> entrySet = fieldConverterStorageMap.entrySet();
			for (Entry<RespConverter, FieldConverterStorage> entry : entrySet) {
				RespConverter respConverter = entry.getKey();
				FieldConverterStorage fieldConverterStorage = entry.getValue();
				Field field = fieldConverterStorage.getField();
				Map<Object, String> fieldConverterMap = fieldConverterStorage.getFieldConverterMap();

				String fieldName = respConverter.field();
				field.setAccessible(true);
				Object fieldValue = ReflectionUtils.getField(field, v);
				field.setAccessible(false);
				if (fieldValue == null) {
					continue;
				}
				String fieldConverterValue = fieldConverterMap.get(fieldValue);
				if (fieldConverterValue == null) {
					continue;
				}
				String newField = respConverter.newField();
				if (StringUtils.isBlank(newField)) {
					newField = fieldName;
				}
				objectNode.set(newField, objectNode.textNode(fieldConverterValue));
			}
			return objectNode;
		}).map(v -> (Object) v).collect(Collectors.toList());
		return list2;
	}

	@Data
	@AllArgsConstructor
	private static class FieldConverterStorage {
		Field field;
		Map<Object, String> fieldConverterMap;
	}
}
