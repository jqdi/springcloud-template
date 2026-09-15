package com.company.system;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.company.framework.constant.CommonConstants;
import com.company.framework.context.SpringContextUtil;

@EnableFeignClients(basePackages = { CommonConstants.BASE_PACKAGE + ".**.feign.**" }) // @FeignClient所在的包
@SpringBootApplication(scanBasePackages = CommonConstants.BASE_PACKAGE, exclude = { RabbitAutoConfiguration.class, RocketMQAutoConfiguration.class }) // bean扫描路径，需要注意com.company.**.api.feign.fallback也需要扫描，所以配置大点
public class SystemApplication {
	public static void main(String[] args) {
		// SpringApplication.run(SystemApplication.class, args);

		SpringApplication springApplication = new SpringApplication(SystemApplication.class);
		// 初始化ApplicationContext，保证在所有bean实例化前面
		springApplication.addInitializers(SpringContextUtil.newInstance());
		springApplication.run(args);
	}
}