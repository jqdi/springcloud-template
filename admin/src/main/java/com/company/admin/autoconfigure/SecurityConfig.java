package com.company.admin.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;
	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private AccessDeniedHandler accessDeniedHandler;

	@Autowired
	private AccessDecisionManager accessDecisionManager;
	@Autowired
	private FilterInvocationSecurityMetadataSource securityMetadataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		/** 内存用户存储 */
		/*
		auth.inMemoryAuthentication()
		.passwordEncoder(new BCryptPasswordEncoder())
		.withUser("system").password(new BCryptPasswordEncoder().encode("666666")).roles("vip2", "content", "system")
		.and()
		.withUser("root").password(new BCryptPasswordEncoder().encode("666666")).roles("vip1", "vip2", "vip3")
		.and()
		.withUser("guest").password(new BCryptPasswordEncoder().encode("666666")).roles("vip1");
		*/
		
		/** 从数据库获取用户信息 */
		/*
		auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(new BCryptPasswordEncoder())
				.usersByUsernameQuery("select username, password, status from Users where username = ?")
				.authoritiesByUsernameQuery("select username, authority from Authority where username = ?");
		*/
		
		/** 自定义用户存储 */
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	// 授权
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin()
		;
		
		// 防止网站攻击： get,post
		http.csrf().disable()// 关闭csrf功能，登出失败可能存在的原因
		;
		
		// 首页所有人可以访问，功能页只有对应有权限的人才能访问
		// 这里的配置需要成对出现，并且配置的顺序也很重要。声明在前面的规则拥有更高的优先级
		// 默认情况下，如果提供的角色不是以“ROLE_”开头，则会添加该角色。可以通过修改defaultRolePrefixon来自定义DefaultWebSecurityExpressionHandler
		http.authorizeRequests()
				.antMatchers("/admin", "/admin/login", "/admin/security/login",
						"/admin/content/article/article/preview").permitAll()// 不需要通过登录验证就可以被访问的资源路径（可以结合注解来开放API）
				// 注：开放的url不要配置到sc_security_resource
				
				/* 这里是静态配置API和访问权限，动态配置使用FilterInvocationSecurityMetadataSource */
				// .antMatchers("/vip2/index").hasRole("vip2")//有vip2角色的用户才可以访问/vip2/index
				// .antMatchers("/admin/system/dict").hasAuthority("system_dict_index_url")//有system_dict_index_url权限的用户才可以访问/admin/system/dict
				/* 这里是静态配置API和访问权限，动态配置使用FilterInvocationSecurityMetadataSource */
				.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
					@Override
					public <O extends FilterSecurityInterceptor> O postProcess(O o) {
						o.setAccessDecisionManager(accessDecisionManager);
						o.setSecurityMetadataSource(securityMetadataSource);
						return o;
					}
				})
				
				.anyRequest().authenticated() // 其他所有请求都是登录后可访问
		;
		
		/** 登录页 */
		// 没有权限会默认到登录页,需要开启登录的页面
		http.formLogin()
		.loginPage("/admin/login")//用户未登录时，访问任何资源都转跳到该路径，即登录页面
		.loginProcessingUrl("/admin/security/login")// 登录表单提交数据的地址，也就是处理认证请求的路径，与loginPage里面的对应上，默认：/login
//		.usernameParameter("username")/// 登录表单form中用户名输入框input的name名，不修改的话默认：username
//		.passwordParameter("password")// form中密码输入框input的name名，不修改的话默认：password
		.defaultSuccessUrl("/")// 登录认证成功后默认转跳的路径
//		.successForwardUrl("/index/page")
		.successHandler(authenticationSuccessHandler)// 默认SavedRequestAwareAuthenticationSuccessHandler
//		.failureForwardUrl(forwardUrl)
//		.failureUrl("/admin/login?error=1")
		.failureHandler(authenticationFailureHandler)
		;
		
		/** 开启注销功能 */
		http.logout()
		.logoutUrl("/admin/logout") // 默认/logout
		.logoutSuccessHandler(logoutSuccessHandler)
//		.logoutSuccessUrl("/admin/login")// 默认/login?logout
		;
		
		/** session管理器 */
		http.sessionManagement().maximumSessions(999)// 配置session最大的数量
		;
		
		/** 用户没有权限的页面处理 */
		http.exceptionHandling()
		.accessDeniedHandler(accessDeniedHandler)
		;
		
		/** 记住我 */
//		http.rememberMe().rememberMeParameter("remember");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 将项目中静态资源路径开放出来
		web.ignoring().antMatchers("favicon.ico", "/assets/**", "/css/**", "/fonts/**", "/img/**", "/js/**");
	}

}
