package com.company.framework.filter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.util.IpUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;
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
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 设备拦截器，记录设备信息（使用场景：推送等业务场景）
 * </pre>
 */
@Component
@Order(CommonConstants.FilterOrdered.DEVICE)
public class DeviceInfoFilter extends OncePerRequestFilter {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private AsyncTaskExecutor executor;

    // 创建断路器，用于发送MQ失败次数达到阈值时自动关闭发送
    private final EventCountCircuitBreaker circuitBreaker =
        new EventCountCircuitBreaker(10, 10, TimeUnit.SECONDS, 5, 10, TimeUnit.SECONDS);

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

        if (!circuitBreaker.checkState()) {
            // 断路器已打开
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

		String platform = request.getHeader(HeaderConstants.HEADER_PLATFORM);
		if (StringUtils.isBlank(platform)) {
			platform = request.getParameter(HeaderConstants.HEADER_PLATFORM);
		}

		String operator = request.getHeader(HeaderConstants.HEADER_OPERATOR);
		if (StringUtils.isBlank(operator)) {
			operator = request.getParameter(HeaderConstants.HEADER_OPERATOR);
		}

		String channel = request.getHeader(HeaderConstants.HEADER_CHANNEL);
		if (StringUtils.isBlank(channel)) {
			channel = request.getParameter(HeaderConstants.HEADER_CHANNEL);
		}

		String version = request.getHeader(HeaderConstants.HEADER_VERSION);
		if (StringUtils.isBlank(version)) {
			version = request.getParameter(HeaderConstants.HEADER_VERSION);
		}

		String requestip = request.getHeader(HeaderConstants.HEADER_REQUESTIP);
		if (StringUtils.isBlank(requestip)) {
			requestip = IpUtil.getRequestIp(request);
		}

		String userAgent = request.getHeader("User-Agent");

		// 记录设备信息
		LocalDateTime now = LocalDateTime.now();
		String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		// 发布设备信息事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("deviceid", deviceid);
		params.put("platform", platform);
		params.put("operator", operator);
		params.put("channel", channel);
		params.put("version", version);
		params.put("requestip", requestip);
		params.put("userAgent", userAgent);
		params.put("time", time);
        // 异步防止阻塞，保证性能
        executor.submit(() -> {
            try {
                messageSender.sendBroadcastMessage(params, BroadcastConstants.DEVICE_INFO.EXCHANGE);
            } catch (Exception ignore) {
                // 异常不影响主线程
                circuitBreaker.incrementAndCheckState();
            }
        });

		chain.doFilter(request, response);
	}
}
