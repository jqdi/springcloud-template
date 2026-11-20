package com.company.job.aspect;

import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.context.HttpContextUtil;
import com.company.framework.util.HostUtil;
import com.google.common.collect.Maps;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.company.framework.trace.TraceManager;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * HeaderContext切面
 *
 * @author JQ棣
 */
@Slf4j
@Aspect
@Component("headerContextAspect")
@Order(10)
public class HeaderContextAspect {
    public static void main(String[] args) {
        OsInfo osInfo = SystemUtil.getOsInfo();
        System.out.println(JSON.toJSONString(osInfo));
    }

	// 在jobHander执行之前塞入HeaderContext
    @Before("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void setHeaderContext(XxlJob xxlJob) {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put(HeaderConstants.HEADER_PLATFORM, "job");
        headerMap.put(HeaderConstants.HEADER_OPERATOR, SystemUtil.getOsInfo().getName());
        headerMap.put(HeaderConstants.HEADER_VERSION, "1.0.0");
        headerMap.put(HeaderConstants.HEADER_DEVICEID, xxlJob.value());
        headerMap.put(HeaderConstants.HEADER_APPCODE, "job");
        headerMap.put(HeaderConstants.HEADER_CHANNEL, "job");
        headerMap.put(HeaderConstants.HEADER_REQUESTIP, HostUtil.ip());
        headerMap.put(HeaderConstants.HEADER_SOURCE, "job");
        HeaderContextUtil.setHeaderMap(headerMap);
        log.info("初始化HeaderContext");
    }

	// 执行完毕之后清理HeaderContext
	@After("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
	public void clearHeaderContext() {
		log.info("清除HeaderContext");
        HeaderContextUtil.remove();
	}

}
