package com.company.codegenerate.controller;

import com.company.codegenerate.config.CodeGeneratorConfig;
import com.company.codegenerate.generator.CodeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/code/generate")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final CodeGeneratorService codeGeneratorService;
    private final CodeGeneratorConfig codeGeneratorConfig;

    @PostMapping("/{moduleName}")
    public String generateCode(@PathVariable String moduleName, @RequestParam String tableName) {
        try {
            if (!codeGeneratorConfig.getModules().containsKey(moduleName.toLowerCase())) {
                return "不支持的模块: " + moduleName;
            }

            codeGeneratorService.generateCode(tableName, moduleName.toLowerCase());
            return "代码生成成功: 模块=" + moduleName + ", 表=" + tableName;
        } catch (Exception e) {
            log.error("代码生成失败", e);
            return "代码生成失败: " + e.getMessage();
        }
    }

    @PostMapping("/{moduleName}/batch")
    public String generateCodeBatch(@PathVariable String moduleName, @RequestBody List<String> tableNames) {
        try {
            if (!codeGeneratorConfig.getModules().containsKey(moduleName.toLowerCase())) {
                return "不支持的模块: " + moduleName;
            }

            StringBuilder result = new StringBuilder();
            for (String tableName : tableNames) {
                try {
                    codeGeneratorService.generateCode(tableName, moduleName.toLowerCase());
                    result.append("代码生成成功: 表=").append(tableName).append("\n");
                } catch (Exception e) {
                    log.error("代码生成失败: " + tableName, e);
                    result.append("代码生成失败: 表=").append(tableName).append(", 错误=").append(e.getMessage()).append("\n");
                }
            }
            return result.toString();
        } catch (Exception e) {
            log.error("批量代码生成失败", e);
            return "批量代码生成失败: " + e.getMessage();
        }
    }

    @GetMapping("/modules")
    public List<String> getSupportedModules() {
        return new ArrayList<>(codeGeneratorConfig.getModules().keySet());
    }
}
