package com.company.im.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tio.core.Tio;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.starter.TioWebSocketServerBootstrap;


import com.company.framework.util.IpUtil;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/tio")
public class TioController {

	@Value("${server.port:0}")
	private String port;

	@Autowired
	private TioWebSocketServerBootstrap bootstrap;

	@RequestMapping("/toUser")
	public Map<String, String> tiosend(String toUserId, String msg) {
		String message = IpUtil.getHostIp() + ":" + port + " " + msg;
		if (StringUtils.isBlank(toUserId)) {
			Tio.sendToAll(bootstrap.getServerTioConfig(), WsResponse.fromText(message, "utf-8"));
		} else {
			Tio.sendToUser(bootstrap.getServerTioConfig(), toUserId, WsResponse.fromText(message, "utf-8"));
		}
        return Collections.singletonMap("value", message);
	}

	@RequestMapping("/group")
	public Map<String, String> tiogroup(String group, String msg) {
		String message = IpUtil.getHostIp() + ":" + port + " " + msg;
		if (StringUtils.isBlank(group)) {
			Tio.sendToAll(bootstrap.getServerTioConfig(), WsResponse.fromText(message, "utf-8"));
		} else {
			Tio.sendToGroup(bootstrap.getServerTioConfig(), group, WsResponse.fromText(message, "utf-8"));
		}
        return Collections.singletonMap("value", message);
	}
}
