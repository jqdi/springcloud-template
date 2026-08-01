package com.company.codegenerate;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.company.codegenerate.config.CodeGeneratorConfig;
import com.company.codegenerate.generator.CodeGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandRunner implements CommandLineRunner {

    private final CodeGeneratorConfig codeGeneratorConfig;
    private final CodeGeneratorService codeGeneratorService;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("====欢迎使用代码生成器====");
        System.out.println("=================================");

        while (true) {
            System.out.print("请输入模块名称 (当前值:" + codeGeneratorConfig.getModuleName() + "): ");
            String moduleName = scanner.nextLine().trim();
            if (moduleName.isEmpty()) {
                moduleName = codeGeneratorConfig.getModuleName();
            }

            System.out.print("请输入要生成代码的表名(多个表名用英文逗号分隔,当前值:" + codeGeneratorConfig.getTableNames() + "): ");
            String tableNames = scanner.nextLine().trim();
            if (tableNames.isEmpty()) {
                tableNames = codeGeneratorConfig.getTableNames();
            }

            try {
                codeGeneratorService.generateCode(tableNames, moduleName);
                System.out.println("代码生成成功！");
            } catch (Exception e) {
                log.error("代码生成失败", e);
                System.out.println("代码生成失败: " + e.getMessage());
            }
        }
    }
}
