package com.company.web.controller;

import cn.hutool.core.date.DateUtil;

import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.web.messagedriven.strategy.StrategyConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mq")
@Slf4j
public class MqController {
	@Autowired
	private MessageSender messageSender;
	@Autowired
	private MessagedrivenProperties messagedrivenProperties;

	@GetMapping(value = "/sendNormalMessage")
	public String sendNormalMessage(String message) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("open", message);
		params.put("open2", message);
		params.put("time", DateUtil.now());
        messageSender.sendNormalMessage(StrategyConstants.MAP_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(), messagedrivenProperties.getQueue().getCommon().getKey());

//		UserMQDto param = new UserMQDto();
//		param.setP1("p1");
//		param.setP2("p2");
//		param.setP3("p3");
//		param.put("time", DateUtil.now());
//		messageSender.sendNormalMessage(StrategyConstants.USER_STRATEGY, param, messagedrivenProperties.getExchange().getDirect(), messagedrivenProperties.getQueue().getCommon().getKey());

		return "success";
	}

	@GetMapping(value = "/sendBroadcastMessage")
	public String sendBroadcastMessage(String message) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("open", message);
		params.put("open2", message);
		params.put("time", DateUtil.now());
		messageSender.sendBroadcastMessage(params, BroadcastConstants.ORDER_CREATE.EXCHANGE);

		return "success";
	}

	@GetMapping(value = "/sendDelayMessage")
	public String sendDelayMessage(String message, Integer delaySeconds) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("open", message);
		params.put("time", DateUtil.now());
		params.put("delaySeconds", delaySeconds);

		messageSender.sendDelayMessage(StrategyConstants.XDELAYMESSAGE_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				messagedrivenProperties.getQueue().getDeadLetter().getKey(), delaySeconds);
		return "success";
	}

	@GetMapping(value = "/sendXDelayMessage")
	public String sendXDelayMessage(String message, Integer delaySeconds) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("open", message);
		params.put("time", DateUtil.now());
		params.put("delaySeconds", delaySeconds);

		messageSender.sendDelayMessage(StrategyConstants.XDELAYMESSAGE_STRATEGY, params, messagedrivenProperties.getExchange().getXdelayed(),
				messagedrivenProperties.getQueue().getXdelayed().getKey(), delaySeconds);
		return "success";
	}
}
