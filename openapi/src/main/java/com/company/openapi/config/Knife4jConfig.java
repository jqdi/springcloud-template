package com.company.openapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.framework.constant.CommonConstants;
import com.company.framework.context.SpringContextUtil;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration(proxyBeanMethods = false)
@EnableSwagger2WebMvc
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
    	String applicationName = SpringContextUtil.getProperty("spring.application.name");
         ApiInfo apiInfo = new ApiInfoBuilder()
				.description(applicationName + "接口测试文档")
				.contact(new Contact("JQ棣", "https://gitee.com/jq_di", "897665787@qq.com"))
				.version("v1.0.0")
				.title(applicationName + " API文档")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage(CommonConstants.BASE_PACKAGE + ".openapi.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
