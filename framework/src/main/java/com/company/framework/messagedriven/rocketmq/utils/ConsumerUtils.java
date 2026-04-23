package com.company.framework.messagedriven.rocketmq.utils;

import com.company.framework.util.JsonUtil;
import com.company.framework.context.SpringContextUtil;
import com.company.framework.messagedriven.BaseStrategy;
import com.company.framework.messagedriven.constants.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class ConsumerUtils {
	private ConsumerUtils() {
	}

	/**
	 * 使用Strategy处理逻辑
	 *
	 * @param jsonStrMsg
	 * @param properties
	 */
	public static <E> void handleByStrategy(String jsonStrMsg, Map<String, String> properties) {
		handleByStrategy(jsonStrMsg, properties, true);
	}

	/**
	 * 使用Strategy处理逻辑
	 *
	 * @param jsonStrMsg
	 * @param properties
	 * @param unAckIfException
	 *            如果抛出java.lang.Exception异常则不ack
	 */
	public static <E> void handleByStrategy(String jsonStrMsg, Map<String, String> properties,
											boolean unAckIfException) {
		@SuppressWarnings("unchecked")
		Consumer<Object> consumer = entity -> {
			String strategyName = MapUtils.getString(properties, HeaderConstants.HEADER_STRATEGY_NAME);
			BaseStrategy<E> strategy = SpringContextUtil.getBean(strategyName, BaseStrategy.class);
			strategy.doStrategy((E) entity);
		};
		handle(jsonStrMsg, properties, consumer, unAckIfException);
	}

	/**
	 * 使用自定义Consumer函数处理逻辑
	 *
	 * @param jsonStrMsg
	 * @param properties
	 * @param consumer
	 */
	public static <E> void handleByConsumer(String jsonStrMsg, Map<String, String> properties, Consumer<E> consumer) {
		handleByConsumer(jsonStrMsg, properties, consumer, false);
	}

	/**
	 * 使用自定义Consumer函数处理逻辑
	 *
	 * @param jsonStrMsg
	 * @param properties
	 * @param consumer
	 * @param unAckIfException
	 *            如果抛出java.lang.Exception异常则不ack
	 */
	public static <E> void handleByConsumer(String jsonStrMsg, Map<String, String> properties, Consumer<E> consumer,
											boolean unAckIfException) {
		@SuppressWarnings("unchecked")
		Consumer<Object> consumer2 = entity -> {
			consumer.accept((E) entity);
		};
		handle(jsonStrMsg, properties, consumer2, unAckIfException);
	}

	private static void handle(String jsonStrMsg, Map<String, String> properties, Consumer<Object> consumer,
							   boolean unAckIfException) {
		log.info("jsonStrMsg:{},properties:{}", jsonStrMsg, properties);
		if (jsonStrMsg == null) {
			return;
		}
		long start = System.currentTimeMillis();
		try {
			String paramsClassName = MapUtils.getString(properties, HeaderConstants.HEADER_PARAMS_CLASS);
			Class<?> paramsClass;
			try {
				paramsClass = Class.forName(paramsClassName);
			} catch (ClassNotFoundException e) {
				log.warn("class {} not found,use {} instead", paramsClassName, Map.class.getName());
				paramsClass = Map.class;// 找不到类，就用Map
			}
			Object entity = JsonUtil.toEntity(jsonStrMsg, paramsClass);
			consumer.accept(entity);
		} catch (RuntimeException e) {
			// 业务异常一般是校验不通过，可以当做成功处理
			log.warn("RuntimeException", e);
		} catch (Exception e) {
			log.error("accept error", e);
			if (unAckIfException) {
				return;
			}
		} finally {
			log.info("耗时:{}ms", System.currentTimeMillis() - start);
		}
	}
}
