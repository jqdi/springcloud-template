package com.company.config.nacos;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigChangeItem;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 官方的RefreshEventListener只打印了配置修改的key，没有打印修改前后的value，这里继承AbstractConfigChangeListener用于打印修改前后的值
 */
@Slf4j
public class LogValueConfigChangeListener extends AbstractConfigChangeListener implements InitializingBean {

    private final NacosConfigManager nacosConfigManager;

    public LogValueConfigChangeListener(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
    }

    @Override
    public void receiveConfigChange(ConfigChangeEvent changeEvent) {
        Collection<ConfigChangeItem> changeItems = changeEvent.getChangeItems();
        for (ConfigChangeItem changeItem : changeItems) {
            log.info("changed:{} {} {} -> {}", changeItem.getType(), changeItem.getKey(), changeItem.getOldValue(),
                changeItem.getNewValue());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigService configService = nacosConfigManager.getConfigService();
        NacosConfigProperties nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();

        String dataId = nacosConfigProperties.getName();
        String group = nacosConfigProperties.getGroup();
        configService.addListener(dataId, group, this);
    }

}