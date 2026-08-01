package com.company.codegenerate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "code.generate")
public class CodeGeneratorConfig {
    /**
     * 模块
     */
    private String moduleName = "order";
    /**
     * 表名，多个表名用逗号分隔
     */
    private String tableNames = "sms_task,sms_task_detail";

    /**
     * 作者
     */
    private String author = "CodeGenerate";

    /**
     * 父包名
     */
    private String parentPackage = "com.company";
}
