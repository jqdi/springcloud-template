package com.company.framework.filter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * <pre>
 * source拦截器，记录用户来源（使用场景：引流统计、邀请奖励、地推业绩计算等业务场景）
 * </pre>
 */
@Component
@Order(CommonConstants.FilterOrdered.SOURCE)
public class SourceFilter extends OncePerRequestFilter {

	@Autowired
	private MessageSender messageSender;
    @Autowired
    private AsyncTaskExecutor executor;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String source = request.getHeader(HeaderConstants.HEADER_SOURCE);
		if (StringUtils.isBlank(source)) {
			source = request.getParameter(HeaderConstants.HEADER_SOURCE);
		}

		if (StringUtils.isBlank(source)) {
			// 请求头和参数都找不到source
			chain.doFilter(request, response);
			return;
		}

		String deviceid = request.getHeader(HeaderConstants.HEADER_DEVICEID);
		if (StringUtils.isBlank(deviceid)) {
			deviceid = request.getParameter(HeaderConstants.HEADER_DEVICEID);
		}

		if (StringUtils.isBlank(deviceid)) {
			// 请求头和参数都找不到deviceid
			chain.doFilter(request, response);
			return;
		}

		// 记录deviceid是来自哪个source
		LocalDateTime now = LocalDateTime.now();
		String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		// 发布用户来源事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("source", source);
		params.put("deviceid", deviceid);
		params.put("time", time);
        // 异步防止阻塞，保证性能
        executor.submit(() -> messageSender.sendBroadcastMessage(params, BroadcastConstants.USER_SOURCE.EXCHANGE));

		chain.doFilter(request, response);
	}
}
