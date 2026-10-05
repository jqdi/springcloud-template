package com.company.config.apollo;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * 1.@Value的属性刷新：AutoUpdateConfigChangeListener默认支持刷新
 * 2.@ConfigurationProperties的属性刷新：基于EnvironmentChangeEvent实现刷新
 * 3.当bean上如果使用了@ConditionalOnProperty如何实现刷新(暂不考虑)
 * 
 * 参考网页：https://www.zhangshengrong.com/p/zAaOQyQrXd/
 * 
 * </pre>
 * @author JQ棣
 *
 */
@Slf4j
public class PropertiesRefresher implements ApplicationContextAware {

	private ApplicationContext applicationContext;

//	@ApolloConfigChangeListener(value = { "application", "otherconfig" }) // 如果要监听多个命名空间，需要都配置上，一般与配置apollo.bootstrap.namespaces对应上
	@ApolloConfigChangeListener // 使用默认的namespace:application
	public void onChange(ConfigChangeEvent changeEvent) {
		Set<String> changedKeys = changeEvent.changedKeys();
		for (String changedKey : changedKeys) {
			ConfigChange configChange = changeEvent.getChange(changedKey);
			log.info("changed:{} {} {} -> {}", configChange.getChangeType(), changedKey, configChange.getOldValue(),
					configChange.getNewValue());
		}
		this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}