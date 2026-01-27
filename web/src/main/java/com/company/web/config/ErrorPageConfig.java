package com.company.web.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration(proxyBeanMethods = false)
public class ErrorPageConfig implements ErrorPageRegistrar {

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		// 错误页
		ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/error/403");
		ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
		ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
		registry.addErrorPages(error403Page, error404Page, error500Page);
	}
}
