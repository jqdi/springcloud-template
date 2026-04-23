package com.company.im.websocket.concept;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.trace.TraceManager;
import com.company.framework.util.JsonUtil;
import com.company.im.websocket.dto.WsMsg;
import com.github.linyuzai.connection.loadbalance.core.concept.Connection;
import com.github.linyuzai.connection.loadbalance.core.concept.ConnectionLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.core.extension.UserMessage;
import com.github.linyuzai.connection.loadbalance.core.extension.UserSelector;
import com.github.linyuzai.connection.loadbalance.core.message.Message;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketMessageHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomWebSocketMessageHandler implements WebSocketMessageHandler {
	@Autowired
	private TraceManager traceManager;

	@Override
	public void onMessage(Message message, Connection connection, ConnectionLoadBalanceConcept concept) {
		traceManager.put();
		log.info("message:{}", message);
		Map<Object, Object> metadata = connection.getMetadata();
		log.info("connection metadata:{}", metadata);

		String userId = MapUtils.getString(metadata, UserSelector.KEY);

		String payload = message.getPayload();
		// Object id = connection.getId();

		WsMsg wsMsg = JsonUtil.toEntity(payload, WsMsg.class);
		String toUserId = wsMsg.getToUserId();
		if (StringUtils.isBlank(toUserId)) {
			String msg = String.format("接收到【%s】的信息:%s", userId, wsMsg.getMessage());
			concept.send(msg);
		} else {
			String msg = String.format("接收到【%s】的信息:%s", userId, wsMsg.getMessage());
			concept.send(new UserMessage(msg, toUserId));
		}
		traceManager.remove();
	}

}
