package com.company.codegenerate;

import com.company.codegenerate.generator.CodeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandRunner implements CommandLineRunner {
    
    private final CodeGeneratorService codeGeneratorService;
    
    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================");
        System.out.println("====欢迎使用代码生成器====");
        System.out.println("=================================");
        
        // 显示支持的模块
        List<String> supportedModules = Arrays.asList("adminapi", "admin", "system", "order", "user", "tool");
        System.out.println("支持的模块: " + String.join(", ", supportedModules));
        
        while (true) {
            System.out.println("\n请选择操作:");
            System.out.println("1. 生成代码");
            System.out.println("2. 退出");
            System.out.print("请输入选项 (1-2): ");
            
            String option = scanner.nextLine();
            
            if ("2".equals(option)) {
                System.out.println("退出代码生成器");
                System.exit(0);
                break;
            }
            
            if ("1".equals(option)) {
                System.out.print("请输入模块名称 (默认order): ");
                String moduleName = scanner.nextLine().trim();
                if (moduleName.isEmpty()) {
                    moduleName = "order";
                }
                
                // 验证模块是否支持
                if (!supportedModules.contains(moduleName.toLowerCase())) {
                    System.out.println("不支持的模块: " + moduleName);
                    continue;
                }
                
                System.out.print("请输入要生成代码的表名: ");
                String tableName = scanner.nextLine().trim();
                
                if (tableName.isEmpty()) {
                    System.out.println("表名不能为空，请重新输入");
                    continue;
                }
                
                try {
                    codeGeneratorService.generateCode(tableName, moduleName);
                    System.out.println("代码生成成功！");
                } catch (Exception e) {
                    log.error("代码生成失败", e);
                    System.out.println("代码生成失败: " + e.getMessage());
                }
            } else {
                System.out.println("无效选项，请重新输入");
            }
        }
        
        scanner.close();
    }
}