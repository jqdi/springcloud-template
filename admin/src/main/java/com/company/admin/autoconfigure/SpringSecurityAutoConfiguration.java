package com.company.admin.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.company.admin.springsecurity.JsonRedirectStrategy;
import com.company.admin.springsecurity.LogSavedRequestAwareAuthenticationSuccessHandler;

@Configuration(proxyBeanMethods = false)
public class SpringSecurityAutoConfiguration {

	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		LogSavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new LogSavedRequestAwareAuthenticationSuccessHandler();
		authenticationSuccessHandler.setRedirectStrategy(new JsonRedirectStrategy());
		return authenticationSuccessHandler;
	}
}
