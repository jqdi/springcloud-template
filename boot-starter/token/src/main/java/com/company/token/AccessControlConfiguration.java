package com.company.token;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.company.token.accesscontrol.interceptor.AccessControlInterceptor;
import com.company.token.accesscontrol.handler.DefaultUnauthorizedHandler;
import com.company.token.accesscontrol.handler.UnauthorizedHandler;

//@Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
@ConditionalOnProperty(prefix = "template.enable", name = "access-control", havingValue = "true", matchIfMissing = true)
public class AccessControlConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UnauthorizedHandler unauthorizedHandler() {
        return new DefaultUnauthorizedHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessControlInterceptor accessControlInterceptor(TokenService tokenService, UnauthorizedHandler unauthorizedHandler) {
        return new AccessControlInterceptor(tokenService, unauthorizedHandler);
    }
}
