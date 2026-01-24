package com.company.token.accesscontrol.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.token.accesscontrol.handler.UnauthorizedHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.company.token.TokenService;
import com.company.token.accesscontrol.annotation.RequireLogin;

import lombok.extern.slf4j.Slf4j;

//@Order(InterceptorOrdered.ACCESS_CONTROL)
@Order(1)
@Slf4j
public class AccessControlInterceptor implements AsyncHandlerInterceptor {

    private TokenService tokenService;

    private UnauthorizedHandler unauthorizedHandler;

    public AccessControlInterceptor(TokenService tokenService, UnauthorizedHandler unauthorizedHandler) {
        this.tokenService = tokenService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;

		Method method = handlerMethod.getMethod();

		RequireLogin methodAnnotation = AnnotationUtils.findAnnotation(method, RequireLogin.class);
		RequireLogin classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequireLogin.class);
		if (methodAnnotation == null && classAnnotation == null) {
			// 方法和类上都没有打注解RequireLogin
			return true;
		}

        // 判断是否已登录
        String token = request.getHeader(tokenService.getTokenName());

        String userId = null;
        if (StringUtils.isNotBlank(token)) {
            userId = tokenService.checkAndGet(token);
        }
        if (StringUtils.isNotBlank(userId)) {
            return true;
        }

		// 判断是否有访问权限？
		log.warn("访问未授权:{}.{}", method.getDeclaringClass().getName(), method.getName());
        unauthorizedHandler.handle(HttpServletResponse.SC_UNAUTHORIZED, "未授权，请登录");
        return false;
    }

}
