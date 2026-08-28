package com.company.framework.message;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.framework.message.impl.I18nMessage;
import com.company.framework.message.impl.MysqlMessage;

@Configuration(proxyBeanMethods = false)
public class MessageAutoConfiguration {

//	@Bean
//	public IMessage message() {
//		IMessage message = new SimpleMessage(); // 普通消息
//		IMessage message = new FormatMessage(); // 格式化消息
//		return message;
//	}

	/**
	 * 国际化消息：基于 MySQL 表存储（common_i18n），未命中时回退到 MessageSource（messages*.properties）。
	 * <p>如需切回纯 properties 实现，将下方实现替换为 {@code new I18nMessage(messageSource)}。
	 * <p>未引入 {@link MessageResolver} 的 DB 适配实现时，自动降级为空解析器，
	 * {@link MysqlMessage} 将完全回退到 MessageSource，保证最小依赖下可正常运行。
	 */
	@Bean
	public IMessage message(MessageSource messageSource, ObjectProvider<MessageResolver> resolverProvider) {
        IMessage message = new I18nMessage(messageSource);
        MessageResolver resolver = resolverProvider.getIfAvailable();
		if (resolver == null) {
			resolver = (code, localeTag) -> null;
		}
		return new MysqlMessage(message, resolver);
	}
}
