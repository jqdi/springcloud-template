package com.company.codegenerate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "code.generate")
public class CodeGenerateProperties {

    private String targetModule;
    private List<String> moduleOptions = new ArrayList<>();
    private String outputBasePath = "../";
    private List<String> tablePrefixes = new ArrayList<>();
    private DatabaseConfig database = new DatabaseConfig();

    @Data
    public static class DatabaseConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName = "com.mysql.cj.jdbc.Driver";
    }
}
