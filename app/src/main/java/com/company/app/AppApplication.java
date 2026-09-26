package com.company.app;

import com.company.framework.constant.CommonConstants;
import com.company.framework.context.SpringContextUtil;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients // @FeignClient扫描，默认扫描‘当前启动类所在包 + 子包’的`@FeignClient`接口
@SpringBootApplication(scanBasePackages = CommonConstants.BASE_PACKAGE, exclude = { RabbitAutoConfiguration.class, RocketMQAutoConfiguration.class }) // bean扫描路径
public class AppApplication {
	public static void main(String[] args) {
		// SpringApplication.run(AppApplication.class, args);

		SpringApplication springApplication = new SpringApplication(AppApplication.class);
		// 初始化ApplicationContext，保证在所有bean实例化前面
		springApplication.addInitializers(SpringContextUtil.newInstance());
		springApplication.run(args);
	}
}