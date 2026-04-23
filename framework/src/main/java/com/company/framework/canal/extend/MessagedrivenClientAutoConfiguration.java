package com.company.framework.canal.extend;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.javatool.canal.client.factory.MapColumnModelFactory;
import top.javatool.canal.client.handler.EntryHandler;
import top.javatool.canal.client.handler.MessageHandler;
import top.javatool.canal.client.handler.RowDataHandler;
import top.javatool.canal.client.handler.impl.MapRowDataHandlerImpl;
import top.javatool.canal.client.handler.impl.SyncFlatMessageHandlerImpl;
import top.javatool.canal.client.spring.boot.autoconfigure.ThreadPoolAutoConfiguration;
import top.javatool.canal.client.spring.boot.properties.CanalProperties;

import java.util.List;
import java.util.Map;

/**
 * 消息驱动 canal 客户端
 * <p>
 * 參考top.javatool.canal.client.spring.boot.autoconfigure.KafkaClientAutoConfiguration实现
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = CanalProperties.CANAL_MODE, havingValue = "messagedriven")
@Import(ThreadPoolAutoConfiguration.class)
public class MessagedrivenClientAutoConfiguration {

    @Bean
    public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
        return new MapRowDataHandlerImpl(new MapColumnModelFactory());
    }

    @Bean
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers) {
        return new SyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler);
    }
}
