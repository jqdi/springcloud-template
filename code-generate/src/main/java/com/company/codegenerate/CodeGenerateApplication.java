package com.company.codegenerate;

import com.company.framework.constant.CommonConstants;
import com.company.framework.context.SpringContextUtil;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.frameworkset.elasticsearch.boot.BBossESAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients // @FeignClient扫描，默认扫描‘当前启动类所在包 + 子包’的`@FeignClient`接口
@SpringBootApplication(scanBasePackages = CommonConstants.BASE_PACKAGE, exclude = { BBossESAutoConfiguration.class, RabbitAutoConfiguration.class, RocketMQAutoConfiguration.class }) // bean扫描路径，需要注意com.company.**.api.feign.fallback也需要扫描，所以配置大点
public class CodeGenerateApplication {
    public static void main(String[] args) {
        // SpringApplication.run(CodeGenerateApplication.class, args);

        SpringApplication springApplication = new SpringApplication(CodeGenerateApplication.class);
        // 初始化ApplicationContext，保证在所有bean实例化前面
        springApplication.addInitializers(SpringContextUtil.newInstance());
        springApplication.run(args);
    }
}