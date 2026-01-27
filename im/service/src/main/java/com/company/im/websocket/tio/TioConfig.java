package com.company.im.websocket.tio;

import org.springframework.context.annotation.Configuration;
import org.tio.websocket.starter.EnableTioWebSocketServer;

@Configuration(proxyBeanMethods = false)
@EnableTioWebSocketServer
public class TioConfig {
}
