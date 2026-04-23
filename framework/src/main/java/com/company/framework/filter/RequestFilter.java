package com.company.framework.filter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.RegexUtil;
import com.company.framework.context.HttpContextUtil;
import com.company.framework.filter.request.BodyReaderHttpServletRequestWrapper;
import com.company.framework.filter.request.BodyReaderHttpServletResponseWrapper;
import com.company.framework.util.IpUtil;
import com.company.framework.util.WebUtil;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 请求/响应参数日志打印
 */
@Slf4j
@Component
@Order(CommonConstants.FilterOrdered.REQUEST)
public class RequestFilter extends OncePerRequestFilter {

	@Value("${template.requestFilter.ignoreLogPatterns:}")
	private String ignoreLogPatterns;
	@Value("${template.requestFilter.arrMaxLength:1000}")
	private int arrMaxLength;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		if (StringUtils.isBlank(ignoreLogPatterns)) {
			return false;
		}

		String requestURI = request.getRequestURI();
		String[] ignoreLogPatternss = StringUtils.split(ignoreLogPatterns, ",");
		for (String ignoreLogPattern : ignoreLogPatternss) {
			boolean match = RegexUtil.match(ignoreLogPattern, requestURI);
			if (match) {
				// 匹配任意一个表达式就不打印日志
				return true;
			}
		}
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
//		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);// 不能使用，读取不到body

		long start = System.currentTimeMillis();
		String contentType = request.getContentType();
		String bodyStr = "{}";
		if (contentType != null && contentType.contains("application/json")) {
			BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
			bodyStr = JsonUtil.toJsonStringReplaceProperties(JsonUtil.toJsonNode(requestWrapper.getBodyStr()), arrMaxLength);// 用json去掉有换行和空格
			request = requestWrapper;
		}

		String paramsStr = JsonUtil.toJsonStringReplaceProperties(WebUtil.getReqParam(request), arrMaxLength);
		String requestIp = IpUtil.getRequestIp(request);
		String headerStr = JsonUtil.toJsonStringReplaceProperties(HttpContextUtil.httpContextHeaderThisRequest(request), arrMaxLength);
		String method = request.getMethod();
		String requestURI = request.getRequestURI();

		log.info("{} {} {} header:{},param:{},body:{}", method, requestIp, requestURI, headerStr, paramsStr, bodyStr);

//		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);// 不能使用，影响文件下载等流操作
		BodyReaderHttpServletResponseWrapper responseWrapper = new BodyReaderHttpServletResponseWrapper(response);

		chain.doFilter(request, responseWrapper);

		String responseBodyStr = "非文本输出";
		if (isLoggableContentType(responseWrapper.getContentType())) {
			responseBodyStr = responseWrapper.getCachedBodyString();
		}

		log.info("{} {} {} response:{},{}ms", method, requestIp, requestURI, responseBodyStr, System.currentTimeMillis() - start);

		// 内容写出到客户端
//		responseWrapper.copyBodyToResponse();
	}

	private static final Set<String> LOGGABLE_RESPONSE_TYPES = Sets.newHashSet(
			"application/json",
			"application/xml",
			"text/plain",
			"text/html",
			"text/xml",
			"text/css",
			"text/javascript",
			"application/vnd.spring-boot.actuator"
	);

	private boolean isLoggableContentType(String contentType) {
		if (contentType == null) {
			return false;
		}
		contentType = contentType.split(";")[0].trim().toLowerCase();
		return LOGGABLE_RESPONSE_TYPES.contains(contentType) || contentType.startsWith("text/");
	}
}
