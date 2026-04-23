package com.company.framework.messagedriven.rabbitmq;

import com.company.framework.util.HostUtil;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Conditional(RabbitMQAutoConfiguration.RabbitMQCondition.class)
public class RabbitMQAutoConfiguration extends org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration {

	// 设置消息转换器
	@Bean
	public MessageConverter messageConverter() {
		return new ContentTypeDelegatingMessageConverter(new SimpleMessageConverter());
	}

	// 复制于源码RabbitAnnotationDrivenConfiguration
	@Bean(name = "rabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);

		// 设置手动acks
		factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		// 设置Consumer tag，方便通过命名查找消费者，特别是开发环境，测试数据可能会被他人消费
		factory.setConsumerTagStrategy(
				a -> HostUtil.identity() + "-" + HostUtil.ip() + "-" + RandomStringUtils.randomAlphanumeric(22));
		return factory;
	}

	// 设置RabbitTemplate Callback方法
	@Bean
	public Object setRabbitTemplateCallback(RabbitTemplate rabbitTemplate) {
		rabbitTemplate.setConfirmCallback(new ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				// publiser-confirm模式可以确保生产者到交换器exchange消息有没有发送成功
				log.info("correlationData:{},ack:{},cause:{}", correlationData, ack, cause);
			}
		});
		rabbitTemplate.setReturnCallback(new ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
					String routingKey) {
				// 当消息通过交换器无法匹配到队列会返回给生产者，就会打印这个日志
				if (exchange != null && exchange.startsWith(BroadcastConstants.PREFIX)) {
					// (并非是BUG)如果配置了发送回调ReturnCallback，插件延迟队列则会回调该方法，因为发送方确实没有投递到队列上，只是在交换器上暂存，等过期时间到了才会发往队列
					return;
				}
				log.info("message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",
						message, replyCode, replyText, exchange, routingKey);
			}
		});
		return new Object();
	}

	public static class RabbitMQCondition extends AllNestedConditions {

		RabbitMQCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnProperty(prefix = "template.enable", name = "message-driven", havingValue = "rabbitmq")
		static class EnableProperty {
		}

		@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "addresses")
		static class HostProperty {
		}

	}
}
