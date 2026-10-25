package com.company.framework.filter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.context.UserAgentContext;
import com.company.framework.context.UserAgentUtil;
import com.company.framework.filter.request.HeaderMapRequestWrapper;
import com.company.framework.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Http上下文公共请求信息设置到header
 * （只有接收外部请求的服务才会用到，内部请求的服务通过FeignHeaderInterceptor已经直接放到header）
 */
@Component
@Order(CommonConstants.FilterOrdered.HTTP_CONTEXT)
public class HttpContextFilter extends OncePerRequestFilter {

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HeaderMapRequestWrapper headerRequest = new HeaderMapRequestWrapper(request);

		String userAgentString = request.getHeader("User-Agent");
		UserAgentContext userAgentContext = UserAgentUtil.parse(userAgentString);

		// 有些请求的公共信息没有放在header，而是跟在url后面
		String platform = request.getHeader(HeaderConstants.HEADER_PLATFORM);
		if (StringUtils.isBlank(platform)) {
			platform = request.getParameter(HeaderConstants.HEADER_PLATFORM);
			if (StringUtils.isBlank(platform)) {
				platform = userAgentContext.getPlatform();
			}
			if (StringUtils.isNotBlank(platform)) {
				headerRequest.addHeader(HeaderConstants.HEADER_PLATFORM, platform);
			}
		}

		String operator = request.getHeader(HeaderConstants.HEADER_OPERATOR);
		if (StringUtils.isBlank(operator)) {
			operator = request.getParameter(HeaderConstants.HEADER_OPERATOR);
			if (StringUtils.isBlank(operator)) {
				operator = userAgentContext.getOperator();
			}
			if (StringUtils.isNotBlank(operator)) {
				headerRequest.addHeader(HeaderConstants.HEADER_OPERATOR, operator);
			}
		}

		String version = request.getHeader(HeaderConstants.HEADER_VERSION);
		if (StringUtils.isBlank(version)) {
			version = request.getParameter(HeaderConstants.HEADER_VERSION);
			if (StringUtils.isNotBlank(version)) {
				headerRequest.addHeader(HeaderConstants.HEADER_VERSION, version);
			}
		}

		String deviceid = request.getHeader(HeaderConstants.HEADER_DEVICEID);
		if (StringUtils.isBlank(deviceid)) {
			deviceid = request.getParameter(HeaderConstants.HEADER_DEVICEID);
			if (StringUtils.isNotBlank(deviceid)) {
				headerRequest.addHeader(HeaderConstants.HEADER_DEVICEID, deviceid);
			}
		}

		String appCode = request.getHeader(HeaderConstants.HEADER_APPCODE);
		if (StringUtils.isBlank(appCode)) {
			appCode = request.getParameter(HeaderConstants.HEADER_APPCODE);
			if (StringUtils.isNotBlank(appCode)) {
				headerRequest.addHeader(HeaderConstants.HEADER_APPCODE, appCode);
			}
		}

		String channel = request.getHeader(HeaderConstants.HEADER_CHANNEL);
		if (StringUtils.isBlank(channel)) {
			channel = request.getParameter(HeaderConstants.HEADER_CHANNEL);
			if (StringUtils.isBlank(channel)) {
				channel = userAgentContext.getChannel();
			}
			if (StringUtils.isNotBlank(channel)) {
				headerRequest.addHeader(HeaderConstants.HEADER_CHANNEL, channel);
			}
		}

		String requestip = request.getHeader(HeaderConstants.HEADER_REQUESTIP);
		if (StringUtils.isBlank(requestip)) {
			requestip = IpUtil.getRequestIp(request);
			if (StringUtils.isNotBlank(requestip)) {
				headerRequest.addHeader(HeaderConstants.HEADER_REQUESTIP, requestip);
			}
		}

		String source = request.getHeader(HeaderConstants.HEADER_SOURCE);
		if (StringUtils.isBlank(source)) {
			source = request.getParameter(HeaderConstants.HEADER_SOURCE);
			if (StringUtils.isNotBlank(source)) {
				headerRequest.addHeader(HeaderConstants.HEADER_SOURCE, source);
			}
		}

		chain.doFilter(headerRequest, response);
	}
}
