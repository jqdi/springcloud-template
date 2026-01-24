package com.company.adminapi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.company.token.accesscontrol.annotation.RequireLogin;

/**
 * 有指定权限可访问的API
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RequireLogin
public @interface RequirePermissions {
	/**
	 * 需要校验的权限码
	 */
	String value();
}