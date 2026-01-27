package com.company.im.websocket.concept;

import org.springframework.context.annotation.Configuration;

import com.github.linyuzai.connection.loadbalance.websocket.EnableWebSocketLoadBalanceConcept;

@Configuration(proxyBeanMethods = false)
@EnableWebSocketLoadBalanceConcept
public class ConceptConfig {
}
