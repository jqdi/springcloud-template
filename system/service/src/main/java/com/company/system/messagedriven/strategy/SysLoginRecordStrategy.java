package com.company.system.messagedriven.strategy;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.constant.HeaderConstants;
import com.company.framework.messagedriven.BaseStrategy;
import com.company.system.entity.SysLogininfo;
import com.company.system.service.SysLogininfoService;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;

/**
 * 记录登录日志
 */
@Component(StrategyConstants.SYS_LOGINRECORD_STRATEGY)
public class SysLoginRecordStrategy implements BaseStrategy<Map<String, Object>> {

	@Autowired
	private SysLogininfoService sysLogininfoService;

//	@Autowired
//	private LocationFeign locationFeign;

	@Override
	public void doStrategy(Map<String, Object> params) {
		String device = MapUtils.getString(params, "device");
		Integer sysUserId = MapUtils.getInteger(params, "sysUserId");
		String account = MapUtils.getString(params, "account");
		LocalDateTime loginTime = LocalDateTimeUtil.parse(MapUtils.getString(params, "loginTime"),
				DatePattern.NORM_DATETIME_FORMATTER);

		@SuppressWarnings("unchecked")
		Map<String, String> httpContextHeader = (Map<String, String>) MapUtils.getMap(params, "httpContextHeader");
		String platform = httpContextHeader.get(HeaderConstants.HEADER_PLATFORM);
		String operator = httpContextHeader.get(HeaderConstants.HEADER_OPERATOR);
		String version = httpContextHeader.get(HeaderConstants.HEADER_VERSION);
		String deviceid = httpContextHeader.get(HeaderConstants.HEADER_DEVICEID);
		String channel = httpContextHeader.get(HeaderConstants.HEADER_CHANNEL);
		String requestip = httpContextHeader.get(HeaderConstants.HEADER_REQUESTIP);
		String source = httpContextHeader.get(HeaderConstants.HEADER_SOURCE);
		String language = httpContextHeader.get(HeaderConstants.ACCEPT_LANGUAGE);

		SysLogininfo sysLogininfo = new SysLogininfo();
		sysLogininfo.setSysUserId(sysUserId);
		sysLogininfo.setLoginTime(loginTime);
		sysLogininfo.setAccount(account);
		sysLogininfo.setDevice(device);

		sysLogininfo.setPlatform(platform);
		sysLogininfo.setOperator(operator);
		sysLogininfo.setVersion(version);
		sysLogininfo.setDeviceid(deviceid);
		sysLogininfo.setChannel(channel);
		sysLogininfo.setIp(requestip);

//		LocationResp result = locationFeign.getByIp(requestip);
//		sysLogininfo.setAddress(result.getAddress());

		sysLogininfo.setAddress("query by requestip");

		sysLogininfo.setSource(source);
		sysLogininfo.setLang(language);
		sysLogininfoService.save(sysLogininfo);
	}
}
