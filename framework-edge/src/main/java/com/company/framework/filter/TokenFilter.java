package com.company.framework.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.framework.context.SpringContextUtil;
import com.company.token.TokenParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.filter.request.HeaderMapRequestWrapper;
import com.company.token.TokenService;

/**
 * token解析，把token转换为userId，并放回请求头中
 */
@Component
@Order(CommonConstants.FilterOrdered.TOKEN)
public class TokenFilter extends OncePerRequestFilter {

	@Autowired
	private TokenService tokenService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = request.getHeader(tokenService.getTokenName());

		String userId = StringUtils.EMPTY;// 注：为了防止直接在header设置用户ID，绕过认证，null情况下要设置用户ID为空串
		String device = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(token)) {
			TokenParams tokenParams = tokenService.checkAndGet(token);
			userId = Optional.ofNullable(tokenParams).map(TokenParams::getUserId).orElse(null);
			if (userId == null) {
				userId = StringUtils.EMPTY;// 注：为了防止直接在header设置用户ID，绕过认证，null情况下要设置用户ID为空串
			}
			device = Optional.ofNullable(tokenParams).map(TokenParams::getDevice).orElse(SpringContextUtil.getProperty("spring.application.name"));
		}

        HeaderMapRequestWrapper headerRequest = new HeaderMapRequestWrapper(request);
        headerRequest.addHeader(HeaderConstants.HEADER_CURRENT_USER_ID, userId);
        headerRequest.addHeader(HeaderConstants.HEADER_CURRENT_DEVICE, device);
        chain.doFilter(headerRequest, response);
	}
}
