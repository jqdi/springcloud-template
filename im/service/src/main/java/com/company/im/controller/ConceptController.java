package com.company.im.controller;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.framework.util.IpUtil;
import com.github.linyuzai.connection.loadbalance.core.extension.GroupMessage;
import com.github.linyuzai.connection.loadbalance.core.extension.PathMessage;
import com.github.linyuzai.connection.loadbalance.core.extension.UserMessage;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;

@RestController
@RequestMapping("/concept")
public class ConceptController {

    @Value("${server.port:0}")
    private String port;

    @Autowired(required = false)
    private WebSocketLoadBalanceConcept concept;

    @RequestMapping("/toUser")
    public Map<String, String> conceptsend(String toUserId, String msg) {
        String message = IpUtil.getHostIp() + ":" + port + " " + msg;
        if (StringUtils.isBlank(toUserId)) {
            concept.send(message);
        } else {
            concept.send(new UserMessage(message, toUserId));
        }
        return Collections.singletonMap("value", "message");
    }

    @RequestMapping("/path")
    public Map<String, String> conceptpath(String path, String msg) {
        String message = IpUtil.getHostIp() + ":" + port + " " + msg;
        if (StringUtils.isBlank(path)) {
            concept.send(message);
        } else {
            concept.send(new PathMessage(message, path));
        }
        return Collections.singletonMap("value", "message");
    }

    @RequestMapping("/group")
    public Map<String, String> conceptgroup(String group, String msg) {
        String message = IpUtil.getHostIp() + ":" + port + " " + msg;
        if (StringUtils.isBlank(group)) {
            concept.send(message);
        } else {
            concept.send(new GroupMessage(message, group));
        }
        return Collections.singletonMap("value", "message");
    }
}
