package com.company.framework.autoconfigure;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.company.framework.constant.CommonConstants;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一加载拦截器，其他地方请勿使用InterceptorRegistry添加拦截器，可能会重复添加
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

	@Autowired(required = false)
	private List<HandlerInterceptor> interceptorList;// 注册为bean的拦截器，直接new的装配不到

	@Autowired(required = false)
	private List<WebMvcConfigurer> webMvcConfigurerList;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (interceptorList == null) {
			log.info("no interceptors");
			return;
		}

		if (webMvcConfigurerList != null) {
			String classNames = webMvcConfigurerList.stream()
					.filter(v -> v.getClass().getName().startsWith(CommonConstants.BASE_PACKAGE))// 只看项目下面的
					.map(v -> v.getClass().getSimpleName()).collect(Collectors.joining(","));
			if (StringUtils.isNotBlank(classNames)) {// 存在其他的WebMvcConfigurer则提醒
				log.warn("存在其他的WebMvcConfigurer,请注意勿重复加载拦截器:{}", classNames);
			}
		}

		List<String> addClassNameList = Lists.newArrayList();
		for (HandlerInterceptor interceptor : interceptorList) {
			Class<? extends HandlerInterceptor> clazz = interceptor.getClass();
			if (clazz.getName().startsWith(CommonConstants.BASE_PACKAGE)) {// 只加载项目下面的bean
				registry.addInterceptor(interceptor).addPathPatterns("/**"); // 指定要拦截的路径模式
				addClassNameList.add(clazz.getSimpleName());
			}
		}
		String classNames = addClassNameList.stream().collect(Collectors.joining("->"));
		log.info("load interceptors,{}", classNames);
	}

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> item : converters) {
            log.info("{} {}", item.getClass().getSimpleName(), item.getSupportedMediaTypes());
        }
    }
}
