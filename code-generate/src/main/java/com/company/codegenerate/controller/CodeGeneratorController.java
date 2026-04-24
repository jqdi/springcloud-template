package com.company.codegenerate.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

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

    /**
     * 生成指定模块的代码
     *
     * @param moduleName 模块名称
     * @param tableName 表名
     * @return 生成结果
     */
    @PostMapping("/{moduleName}")
    public Map<String, String> generateCode(@PathVariable String moduleName, @RequestParam String tableName) {
        log.info("开始生成代码，模块名: {}，表名: {}", moduleName, tableName);
        // TODO
        return Collections.singletonMap("message", "开始生成代码，模块名: " + moduleName + "，表名: " + tableName);
    }
}