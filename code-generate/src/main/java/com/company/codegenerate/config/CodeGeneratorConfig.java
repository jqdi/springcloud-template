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
     * 可选的模块列表
     */
    private String moduleOptions = "tool,admin,system,order,user,adminapi";
    
    /**
     * 模板路径
     */
    private String templatePath = "classpath:/templates";
    
    /**
     * 输出基础路径
     */
    private String outputBasePath = "D:\\code\\springcloud-template";
    
    /**
     * 数据库配置
     */
    private DatabaseConfig database = new DatabaseConfig();
    
    @Data
    public static class DatabaseConfig {
        private String url = "jdbc:mysql://localhost:3306/template?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        private String username = "root";
        private String password = "12345678";
        private String driverClassName = "com.mysql.cj.jdbc.Driver";
    }
}