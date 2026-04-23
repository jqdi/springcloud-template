package com.company.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.FlatMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.javatool.canal.client.handler.MessageHandler;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/canal")
@Slf4j
@ConditionalOnProperty(value = "canal.mode", havingValue = "messagedriven")
public class CanalController {
    @Autowired
    private MessageHandler messageHandler;

    /**
     * 测试 MessagedrivenClientAutoConfiguration自动装配的MessageHandler
     */
    @GetMapping(value = "/handleMessage")
    public Map<String, String> handleMessage() {
        String data = "{\"data\":[{\"id\":\"1\",\"uid\":\"\",\"nickname\":\"1111111111111\",\"avatar\":null,\"remark\":\"3333333333333\",\"create_time\":\"2025-11-29 21:31:22\",\"update_time\":\"2025-11-29 23:25:04\"}],\"database\":\"template\",\"es\":1764429904000,\"id\":5,\"isDdl\":false,\"mysqlType\":{\"id\":\"int(11)\",\"uid\":\"varchar(32)\",\"nickname\":\"varchar(32)\",\"avatar\":\"varchar(32)\",\"remark\":\"varchar(255)\",\"create_time\":\"datetime\",\"update_time\":\"datetime\"},\"old\":[{\"nickname\":\"111111111111\",\"remark\":\"333333333333\",\"update_time\":\"2025-11-29 23:24:53\"}],\"pkNames\":[\"id\"],\"sql\":\"\",\"sqlType\":{\"id\":4,\"uid\":12,\"nickname\":12,\"avatar\":12,\"remark\":12,\"create_time\":93,\"update_time\":93},\"table\":\"user_info\",\"ts\":1764429904992,\"type\":\"UPDATE\"}";
        FlatMessage message = JSON.parseObject(data, FlatMessage.class);
        System.out.println(message);
        messageHandler.handleMessage(message);
        return Collections.singletonMap("value", "success");
    }
}
