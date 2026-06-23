package com.company.codegenerate.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.company.codegenerate.config.CodeGeneratorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGeneratorService {
    
    private final CodeGeneratorConfig codeGeneratorConfig;
    
    /**
     * 根据表名和模块名生成代码
     *
     * @param tableName 表名
     * @param moduleName 模块名
     */
    public void generateCode(String tableName, String moduleName) {
        String projectPath = System.getProperty("user.dir");
//        String outputPath = Paths.get(projectPath, moduleName).toString();
        String outputPath = Paths.get(projectPath, moduleName, "service").toString();

        log.info("开始生成代码，表名: {}，模块名: {}，输出路径: {}", tableName, moduleName, outputPath);

        // 构建数据源配置
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(
                codeGeneratorConfig.getDatabase().getUrl(),
                codeGeneratorConfig.getDatabase().getUsername(),
                codeGeneratorConfig.getDatabase().getPassword()
        );

        FastAutoGenerator.create(dataSourceConfigBuilder)
                // 全局配置
                .globalConfig(builder -> builder
                        .author("CodeGenerate") // 设置作者
                        .enableSwagger() // 开启 swagger 模式
//                        .fileOverride() // 覆盖已生成文件
                        .outputDir(outputPath + "/src/main/java") // 指定输出目录
                        .dateType(DateType.TIME_PACK) // 时间策略
                        .build())
                // 包配置
                .packageConfig(builder -> builder
                        .parent(getPackageParent(moduleName)) // 设置父包名
                        .moduleName(getModuleName(moduleName)) // 设置父包模块名
                        .entity("entity") // Entity包名
                        .service("service") // Service包名
                        .serviceImpl("service.impl") // Service Impl包名
                        .mapper("mapper") // Mapper包名
                        .xml("mapper.xml") // Mapper XML包名
                        .controller("controller") // Controller包名
                        .pathInfo(generatorPathInfo(outputPath)) // 配置其他路径
                        .build())
                // 策略配置
                .strategyConfig(builder -> builder
                        .addInclude(tableName) // 设置需要生成的表名
                        .entityBuilder() // 实体策略配置
                        .enableLombok() // 开启 Lombok
//                        .logicDeleteColumnName("deleted") // 逻辑删除字段
                        .build()
                        .controllerBuilder() // 控制器策略配置
                        .enableRestStyle() // 开启@RestController注解
                        .enableHyphenStyle() // 开启驼峰转连字符
                        .build()
                        .serviceBuilder() // 服务策略配置
                        .formatServiceFileName("%sService") // service命名方式
                        .formatServiceImplFileName("%sServiceImpl") // service impl命名方式
                        .build()
                        .mapperBuilder() // Mapper策略配置
                        .enableMapperAnnotation() // 开启@Mapper注解
                        .enableBaseResultMap() // 启用 BaseResultMap 生成
                        .enableBaseColumnList() // 启用 BaseColumnList
                        .build())
                // 模板配置
                .templateConfig(builder -> builder
//                        .disable(TemplateConfig.builder().build()) // 禁用默认模板引擎
                        .entity("/templates/entity.java.vm") // 实体模板
                        .service("/templates/service.java.vm") // 服务接口模板
                        .serviceImpl("/templates/serviceImpl.java.vm") // 服务实现模板
                        .mapper("/templates/mapper.java.vm") // Mapper模板
                        .xml("/templates/mapper.xml.vm") // Mapper XML模板
                        .controller(getControllerTemplate(moduleName)) // 控制器模板
                        .build())
                // 注入配置
                .injectionConfig(consumer -> {
                    // 自定义配置
//                    consumer.customFile(builder -> builder
//                            .fileName("DTO.java")
//                            .filePath("")
//                            .build());
                })
                // 使用Velocity引擎模板
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        log.info("代码生成完成，表名: {}，模块名: {}", tableName, moduleName);
    }
    
    /**
     * 获取包名父级
     *
     * @param moduleName 模块名
     * @return 包名父级
     */
    private String getPackageParent(String moduleName) {
        switch (moduleName) {
            case "adminapi":
                return "com.company.adminapi";
            case "admin":
                return "com.company.admin";
            case "system":
                return "com.company.system";
            case "order":
                return "com.company.order";
            case "user":
                return "com.company.user";
            case "tool":
                return "com.company.tool";
            default:
                return "com.company." + moduleName;
        }
    }
    
    /**
     * 获取模块名称
     *
     * @param moduleName 模块名
     * @return 模块名称
     */
    private String getModuleName(String moduleName) {
        // 对于特定模块，我们不需要额外的模块名
        List<String> noModuleNames = Arrays.asList("adminapi", "admin", "system", "order", "user", "tool");
        if (noModuleNames.contains(moduleName)) {
            return "";
        }
        return "";
    }
    
    /**
     * 根据模块名获取控制器模板
     *
     * @param moduleName 模块名
     * @return 控制器模板路径
     */
    private String getControllerTemplate(String moduleName) {
        if ("adminapi".equals(moduleName)) {
            return "/templates/adminapi-controller.java.vm";
        }
        return "/templates/controller.java.vm";
    }
    
    /**
     * 生成路径信息
     *
     * @param outputPath 输出路径
     * @return 路径信息
     */
    private java.util.Map<OutputFile, String> generatorPathInfo(String outputPath) {
        java.util.Map<OutputFile, String> pathInfo = new java.util.HashMap<>();
        pathInfo.put(OutputFile.mapper, outputPath + "/src/main/resources/mapper");
        return pathInfo;
    }
}