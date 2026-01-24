package com.company.framework.trace.filter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.filter.request.HeaderMapRequestWrapper;
import com.company.framework.trace.TraceManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对客户端请求添加追踪ID
 */
@Component
@Order(CommonConstants.FilterOrdered.TRACE)
public class TraceFilter extends OncePerRequestFilter {

	@Autowired
	private TraceManager traceManager;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String traceId = request.getHeader(HeaderConstants.TRACE_ID);
		traceManager.put(traceId);
		if (StringUtils.isEmpty(traceId)) {
			HeaderMapRequestWrapper headerRequest = new HeaderMapRequestWrapper(request);
			headerRequest.addHeader(HeaderConstants.TRACE_ID, traceManager.get());
			request = headerRequest;
		}
		try {
			chain.doFilter(request, response);
		} finally {
			traceManager.remove();
		}
	}
}
