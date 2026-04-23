package com.company.user.messagedriven.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.framework.messagedriven.BaseStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 记录登录日志
 */
@Slf4j
@Component(StrategyConstants.LOGINRECORD_STRATEGY)
public class LoginRecordStrategy implements BaseStrategy<Map<String, Object>> {

	@Override
	public void doStrategy(Map<String, Object> params) {
		log.info("loginRecord params:{}", params);
	}
}
