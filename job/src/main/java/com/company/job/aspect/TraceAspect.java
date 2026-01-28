package com.company.job.aspect;

import com.company.framework.trace.TraceManager;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志追踪ID切面
 *
 * @author JQ棣
 */
@Slf4j
@Aspect
@Component("jobTraceAspect")
@Order(10)
public class TraceAspect {
	@Autowired
	private TraceManager traceManager;

	// 在jobHander执行之前塞入日志ID，用于追踪整个执行链路
	@Before("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
	public void setTraceId() {
		traceManager.put();
		log.info("初始化日志ID");
	}

	// 执行完毕之后清理日志ID
	@After("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
	public void clearTraceId() {
		log.info("清除日志ID");
		traceManager.remove();
	}

}
