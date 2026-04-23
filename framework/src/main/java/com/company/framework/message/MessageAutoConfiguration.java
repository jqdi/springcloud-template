package com.company.framework.message;

import com.company.framework.message.impl.I18nMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MessageAutoConfiguration {

//	@Bean
//	public IMessage message() {
//		IMessage message = new SimpleMessage(); // 普通消息
//		IMessage message = new FormatMessage(); // 格式化消息
//		return message;
//	}

    @Bean
    public IMessage message(MessageSource messageSource) { // 国际化消息
        IMessage message = new I18nMessage(messageSource);
        return message;
    }
}
