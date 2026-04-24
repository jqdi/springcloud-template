package com.company.codegenerate.controller;

import com.company.codegenerate.dto.GenCodeReq;
import com.company.codegenerate.dto.GenCodeResp;
import com.company.codegenerate.service.CodeGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/code/generate")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final CodeGenerateService codeGenerateService;

    @PostMapping
    public GenCodeResp generateCode(@RequestBody GenCodeReq req) {
        log.info("开始生成代码，模块名: {}，表名: {}", req.getModuleName(), req.getTableNames());
        return codeGenerateService.generate(req);
    }
}
