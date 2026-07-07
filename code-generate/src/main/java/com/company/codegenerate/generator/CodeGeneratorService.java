package com.company.codegenerate.generator;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
    private final DataSource dataSource;

    /**
     * 根据表名和模块名生成代码
     *
     * @param tableNames 表名
     * @param moduleName 模块名
     */
    public void generateCode(String tableNames, String moduleName) {
        String projectPath = System.getProperty("user.dir");
        String outputPath = Paths.get(projectPath, moduleName, "service").toString();
        String apiPath = Paths.get(projectPath, moduleName, "api", "src/main/java", "com", "company", "order", "api").toString();
        String adminapiPath = Paths.get(projectPath, "adminapi", "src/main/java", "com", "company", "adminapi").toString();

        log.info("开始生成代码，表名: {}，模块名: {}，输出路径: {}，api路径: {}，adminapi路径: {}", tableNames, moduleName, outputPath, apiPath,
            adminapiPath);

        // 构建数据源配置
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(dataSource);

        String packageParent = "com.company";

        FastAutoGenerator.create(dataSourceConfigBuilder)
            // 全局配置
            .globalConfig(builder -> {
                builder//
                    .author(codeGeneratorConfig.getAuthor()) // 设置作者
                    // .enableSwagger() // 开启 swagger 模式
                    .disableOpenDir() // 禁止打开输出目录
                    .outputDir(outputPath + "/src/main/java") // 指定输出目录
                    .dateType(DateType.TIME_PACK) // 使用 java.time 包下的 java8 新的时间类型
                ;
            })
            // 包配置
            .packageConfig(builder -> {
                Map<OutputFile, String> pathInfo = new HashMap<>();
                pathInfo.put(OutputFile.xml, outputPath + "/src/main/resources/mapper");

                builder//
                    .parent(packageParent) // 设置父包名
                    .moduleName(moduleName) // 设置父包模块名
                    .entity("entity") // Entity包名
                    .mapper("mapper") // Mapper包名
                    // .xml("mapper.xml") // Mapper XML包名
                    .service("service") // Service包名
                    // .serviceImpl("service.impl") // Service Impl包名
                    .controller("controller") // Controller包名
                    .pathInfo(pathInfo) // 配置其他路径
                ;
            })
            // 策略配置
            .strategyConfig(builder -> {
                builder.addInclude(tableNames);

                builder.entityBuilder() // 实体策略配置
                    .javaTemplate("/templates/entity.java.vm")//
                    .enableFileOverride()//
                    // .enableLombok() // 开启 Lombok
                    // .logicDeleteColumnName("deleted") // 逻辑删除字段
                    .build();

                builder.mapperBuilder() // Mapper策略配置
                    .mapperTemplate("/templates/mapper.java.vm")//
                    .mapperXmlTemplate("/templates/mapper.xml.vm")//
                    .enableFileOverride()//
                    // .enableMapperAnnotation() // 开启@Mapper注解
                    // .enableBaseResultMap() // 启用 BaseResultMap 生成
                    // .enableBaseColumnList() // 启用 BaseColumnList
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
                    // .enableRestStyle() // 开启@RestController注解
                    // .enableHyphenStyle() // 开启驼峰转连字符
                    .build();
            })
            // 注入配置
            .injectionConfig(consumer -> {
                // 自定义配置
                Map<String, Object> customMap = Maps.newHashMap();
                customMap.put("apiPackage", "com.company." + moduleName + ".api");
                customMap.put("adminapiPackage", "com.company.adminapi");
                consumer.customMap(customMap);

                // 在每个文件输出前，动态设置首字母小写的 entity 变量
                consumer.beforeOutputFile((tableInfo, objectMap) -> {
                    String entityName = tableInfo.getEntityName();
                    String _entity = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
                    objectMap.put("_entity", _entity);

                    String serviceName = tableInfo.getServiceName();
                    String _serviceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
                    objectMap.put("_serviceName", _serviceName);
                });

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

        log.info("代码生成完成，表名: {}，模块名: {}", tableNames, moduleName);
    }
}
