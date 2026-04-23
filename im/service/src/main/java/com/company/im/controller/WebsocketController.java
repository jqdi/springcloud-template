package com.company.im.controller;


import com.company.im.api.feign.WebsocketFeign;
import com.company.im.api.request.AllReq;
import com.company.im.api.request.GroupReq;
import com.company.im.api.request.UserReq;
import com.github.linyuzai.connection.loadbalance.core.extension.GroupMessage;
import com.github.linyuzai.connection.loadbalance.core.extension.UserMessage;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/websocket")
public class WebsocketController implements WebsocketFeign {
    @Autowired
    private WebSocketLoadBalanceConcept concept;

    @Override
    public Void sendToAll(@RequestBody AllReq allReq) {
        String message = allReq.getMessage();
        concept.send(message);
        return null;
    }

    @Override
    public Void sendToUser(@RequestBody UserReq userReq) {
        String message = userReq.getMessage();
        String userId = userReq.getUserId();
        concept.send(new UserMessage(message, userId));
        return null;
    }

    @Override
    public Void sendToGroup(@RequestBody GroupReq groupReq) {
        String message = groupReq.getMessage();
        String group = groupReq.getGroup();
        concept.send(new GroupMessage(message, group));// 框架未实现
        return null;
    }
}
