package com.company.codegenerate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.codegenerate.generator.CodeGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/code/generate")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final CodeGeneratorService codeGeneratorService;

    /**
     * 生成指定模块的代码
     *
     * @param moduleName 模块名称
     * @param tableNames  表名
     * @return 生成结果
     */
    @PostMapping("")
    public String generateCode(@RequestParam String moduleName, @RequestParam String tableNames) {
        try {
            codeGeneratorService.generateCode(tableNames, moduleName);
            return "代码生成成功: 模块=" + moduleName + ", 表=" + tableNames;
        } catch (Exception e) {
            log.error("代码生成失败", e);
            return "代码生成失败: " + e.getMessage();
        }
    }
}
