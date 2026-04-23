package com.company.adminapi.interceptor;

import com.company.adminapi.annotation.RequirePermissions;
import com.company.framework.constant.CommonConstants.InterceptorOrdered;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.system.api.feign.SysUserRoleFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;

@Order(InterceptorOrdered.PERMISSION)
@Slf4j
@Component
@ConditionalOnProperty(prefix = "template.enable", name = "permission", havingValue = "true", matchIfMissing = true)
public class PermissionInterceptor implements AsyncHandlerInterceptor {

	@Lazy // 与feign扫描有冲突，构成循环依赖，所以加@Lazy
	@Autowired
	private SysUserRoleFeign sysUserRoleFeign;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;

		Method method = handlerMethod.getMethod();

		RequirePermissions methodAnnotation = AnnotationUtils.findAnnotation(method, RequirePermissions.class);
		RequirePermissions classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequirePermissions.class);

		if (methodAnnotation == null && classAnnotation == null) {
			// 方法和类上都没有打注解RequirePermissions
			return true;
		}

		// 注：为了防止直接在header设置用户ID，绕过认证，要取最后1个值
		Enumeration<String> headerCurrentUserIdEnum = request.getHeaders(HeaderConstants.HEADER_CURRENT_USER_ID);
		String lastCurrentUserId = null;
		while (headerCurrentUserIdEnum.hasMoreElements()) {
			lastCurrentUserId = headerCurrentUserIdEnum.nextElement();
		}

		String userId = StringUtils.defaultIfBlank(lastCurrentUserId, "0");

		// 判断是否有访问权限？
		RequirePermissions requirePermissions = method.getAnnotation(RequirePermissions.class);

		Boolean hasPermission = sysUserRoleFeign.hasPermission(Integer.valueOf(userId), requirePermissions.value())
				;
		if (hasPermission) {
			return true;
		}

		log.warn("无访问权限:{}.{}", method.getDeclaringClass().getName(), method.getName());
		ExceptionUtil.throwException("未授权");
		return false;
	}
}
