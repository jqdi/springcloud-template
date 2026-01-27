package com.company.framework.messagedriven.rabbitmq.config;

import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.framework.messagedriven.rabbitmq.RabbitMQAutoConfiguration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 延时队列(用2个队列实现)
 *
 * <pre>
 * 无法解决高延时消息阻塞低延时消息问题
 * </pre>
 *
 * @return
 */
@Configuration(proxyBeanMethods = false)
@Conditional(RabbitMQAutoConfiguration.RabbitMQCondition.class)
public class DelayConfig {

	@Bean
	public DirectExchange directExchange(MessagedrivenProperties messagedrivenProperties) {
		return new DirectExchange(messagedrivenProperties.getExchange().getDirect());
	}

	/**
	 * 死信队列
	 *
	 * @return
	 */
	@Bean
	public Queue delayQueue(MessagedrivenProperties messagedrivenProperties) {
		return QueueBuilder.durable(messagedrivenProperties.getQueue().getDeadLetter().getName())
				// 最大延迟毫秒数（这里指定1天）
				.withArgument("x-message-ttl", 86400000)
				// 配置到期后转发的交换
				.withArgument("x-dead-letter-exchange", messagedrivenProperties.getExchange().getDirect())
				// 配置到期后转发的路由键
				.withArgument("x-dead-letter-routing-key", messagedrivenProperties.getQueue().getCommon().getKey())// ttl到期后转发到普通公共队列
				.build();
	}

	/**
	 * 死信队列和交换机绑定
	 *
	 * @return
	 */
	@Bean
	public Binding delayBinding(Queue delayQueue, DirectExchange directExchange, MessagedrivenProperties messagedrivenProperties) {
		return BindingBuilder.bind(delayQueue).to(directExchange).with(messagedrivenProperties.getQueue().getDeadLetter().getKey());
	}
}
