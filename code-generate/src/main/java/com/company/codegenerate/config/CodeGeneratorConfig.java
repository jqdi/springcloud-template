package com.company.codegenerate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "code.generate")
public class CodeGeneratorConfig {
    /**
     * 目标模块名称
     */
    private String targetModule = "tool";
    /**
     * 作者
     */
    private String author = "CodeGenerate";

    /**
     * 可选的模块列表
     */
    private String moduleOptions = "tool,system,user,order";
    
    /**
     * 模板路径
     */
    private String templatePath = "classpath:/templates";
    
    /**
     * 输出基础路径
     */
    private String outputBasePath = "D:\\code\\springcloud-template";
}