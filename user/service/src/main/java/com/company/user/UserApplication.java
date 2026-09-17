package com.company.user;

import com.company.framework.constant.CommonConstants;
import com.company.framework.context.SpringContextUtil;
import org.frameworkset.elasticsearch.boot.BBossESAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = { CommonConstants.BASE_PACKAGE + ".**.feign.**" }) // @FeignClient所在的包
@SpringBootApplication(scanBasePackages = CommonConstants.BASE_PACKAGE, exclude = { BBossESAutoConfiguration.class, RabbitAutoConfiguration.class }) // bean扫描路径
public class UserApplication {
	public static void main(String[] args) {
		// SpringApplication.run(UserApplication.class, args);

		SpringApplication springApplication = new SpringApplication(UserApplication.class);
		// 初始化ApplicationContext，保证在所有bean实例化前面
		springApplication.addInitializers(SpringContextUtil.newInstance());
		springApplication.run(args);
	}
}