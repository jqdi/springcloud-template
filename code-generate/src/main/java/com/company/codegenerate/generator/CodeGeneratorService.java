package com.company.codegenerate.generator;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.company.codegenerate.config.CodeGeneratorConfig;
import com.google.common.collect.Maps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        String outputPath = Paths.get(projectPath, moduleName, "service").toString();
//        String apiPath = Paths.get(projectPath, moduleName, "api", "src", "main", "java").toString();
        String apiPath = "D:\\code\\me\\springcloud-template\\order\\api\\src\\main\\java\\com\\company\\order\\api";
//        String adminapiPath = Paths.get(projectPath, "adminapi", "src", "main", "java").toString();
        String adminapiPath = "D:\\code\\me\\springcloud-template\\adminapi\\src\\main\\java\\com\\company\\adminapi";

        log.info("开始生成代码，表名: {}，模块名: {}，输出路径: {}，api路径: {}，adminapi路径: {}",
            tableName, moduleName, outputPath, apiPath, adminapiPath);

        // 构建数据源配置
        DataSourceConfig.Builder dataSourceConfigBuilder =
            new DataSourceConfig.Builder(codeGeneratorConfig.getDatabase().getUrl(),
                codeGeneratorConfig.getDatabase().getUsername(), codeGeneratorConfig.getDatabase().getPassword());

        FastAutoGenerator.create(dataSourceConfigBuilder)
            // 全局配置
            .globalConfig(builder -> {
                builder//
                    .author("CodeGenerate") // 设置作者
                    .enableSwagger() // 开启 swagger 模式
                    .disableOpenDir() // 禁止打开输出目录
                    .outputDir(outputPath + "/src/main/java") // 指定输出目录
                    .dateType(DateType.TIME_PACK) // 时间策略
                ;
            })
            // 包配置
            .packageConfig(builder -> {
                Map<OutputFile, String> pathInfo = new HashMap<>();
                pathInfo.put(OutputFile.xml, outputPath + "/src/main/resources/mapper");

                builder//
                       // .parent(getPackageParent(moduleName)) // 设置父包名
                       // .parent("com.company."+moduleName) // 设置父包名
                    .parent("com.company") // 设置父包名
                    // .moduleName(getModuleName(moduleName)) // 设置父包模块名
                    .moduleName(moduleName) // 设置父包模块名
                    .entity("entity") // Entity包名
                    .mapper("mapper") // Mapper包名
                    .xml("mapper.xml") // Mapper XML包名
                    .service("service") // Service包名
                    // .serviceImpl("service.impl") // Service Impl包名
                    .controller("controller") // Controller包名
                    .pathInfo(pathInfo) // 配置其他路径
                ;
            })
            // 策略配置
            .strategyConfig(builder -> {
                builder.addInclude(tableName);

                builder.entityBuilder() // 实体策略配置
                    .javaTemplate("/templates/entity.java.vm")//
                    .enableFileOverride()//
                    .enableLombok() // 开启 Lombok
                    // .logicDeleteColumnName("deleted") // 逻辑删除字段
                    .build();

                builder.mapperBuilder() // Mapper策略配置
                    .mapperTemplate("/templates/mapper.java.vm")//
                    .mapperXmlTemplate("/templates/mapper.xml.vm")//
                    .enableFileOverride()//
                    // .enableMapperAnnotation() // 开启@Mapper注解
                    .enableBaseResultMap() // 启用 BaseResultMap 生成
                    .enableBaseColumnList() // 启用 BaseColumnList
                    .build();

                builder.serviceBuilder() // 服务策略配置
                    .serviceTemplate("/templates/service.java.vm")//
                    // .serviceImplTemplate("/templates/serviceImpl.java.vm")//
                    .enableFileOverride()//
                    .formatServiceFileName("%sService") // service命名方式
                    .disableServiceImpl()//
                    // .formatServiceImplFileName("%sServiceImpl") // service impl命名方式
                    .build();

                builder.controllerBuilder() // 控制器策略配置
                    .template("/templates/controller.java.vm")//
                    .enableFileOverride()//
                    .enableRestStyle() // 开启@RestController注解
                    .enableHyphenStyle() // 开启驼峰转连字符
                    .build();
            })
            // 注入配置
            .injectionConfig(consumer -> {
                // 自定义配置
                Map<String, Object> customMap = Maps.newHashMap();
                customMap.put("lowEntity", "aaaa");
                customMap.put("apiPackage", "com.company." + moduleName + ".api");
                customMap.put("adminapiPackage", "com.company.adminapi");
                consumer.customMap(customMap);

                consumer.customFile(builder -> builder//
                    .templatePath("/templates/entityReq.java.vm")//
                    .filePath(apiPath)//
                    .packageName("request")//
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Req")//
                    .fileName(".java")//
                    .enableFileOverride()//
                );
                consumer.customFile(builder -> builder//
                    .templatePath("/templates/entityResp.java.vm")//
                    .filePath(apiPath)//
                    .packageName("response")//
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Resp")//
                    .fileName(".java")//
                    .enableFileOverride()//
                );
                consumer.customFile(builder -> builder//
                    .templatePath("/templates/feign.java.vm")//
                    .filePath(apiPath)//
                    .packageName("feign")//
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Feign")//
                    .fileName(".java")//
                    .enableFileOverride()//
                );
                consumer.customFile(builder -> builder//
                    .templatePath("/templates/adminapi-controller.java.vm")//
                    .filePath(adminapiPath)//
                    .packageName("controller")//
                    .formatNameFunction(tableInfo -> tableInfo.getControllerName())//
                    .fileName(".java")//
                    .enableFileOverride()//
                );
                consumer.customFile(builder -> builder//
                    .templatePath("/templates/adminapi-entityExcel.java.vm")//
                    .filePath(adminapiPath)//
                    .packageName("excel")//
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Excel")//
                    .fileName(".java")//
                    .enableFileOverride()//
                );
            })
            // 使用Velocity引擎模板
            .templateEngine(new VelocityTemplateEngine()) // 默认VelocityTemplateEngine
            .execute();

        log.info("代码生成完成，表名: {}，模块名: {}", tableName, moduleName);
    }
}
