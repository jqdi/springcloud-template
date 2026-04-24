package com.company.codegenerate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "code.generate")
public class CodeGeneratorConfig {

    private String targetModule = "adminapi";

    private String templatePath = "classpath:/templates";

    private String outputBasePath = "../";

    private Map<String, ModuleInfo> modules = new HashMap<>();

    @Data
    public static class ModuleInfo {
        private String packageName;
        private String outputPath;
        private String controllerTemplate = "/templates/controller.java.vm";
        private boolean enableValidation = true;
        private boolean enableLogicDelete = false;
    }
}
