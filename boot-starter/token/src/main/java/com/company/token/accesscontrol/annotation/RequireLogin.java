package com.company.token.accesscontrol.annotation;

import java.lang.annotation.*;

/**
 * 登录后可访问的API
 *
 * <pre>
 * 一般需要获取当前登录用户ID的API需要添加该注解
 * </pre>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequireLogin {
}
