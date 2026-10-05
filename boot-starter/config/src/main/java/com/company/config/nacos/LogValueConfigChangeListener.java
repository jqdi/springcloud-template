package com.company.config.nacos;

import java.util.Collection;

import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigChangeItem;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 官方的RefreshEventListener只打印了配置修改的key，没有打印修改前后的value，这里继承AbstractConfigChangeListener用于打印修改前后的值
 */
@Slf4j
public class LogValueConfigChangeListener extends AbstractConfigChangeListener {

    public LogValueConfigChangeListener() {}

    @Override
    public void receiveConfigChange(ConfigChangeEvent changeEvent) {
        Collection<ConfigChangeItem> changeItems = changeEvent.getChangeItems();
        for (ConfigChangeItem changeItem : changeItems) {
            log.info("changed:{} {} {} -> {}", changeItem.getType(), changeItem.getKey(), changeItem.getOldValue(),
                changeItem.getNewValue());
        }
    }
}