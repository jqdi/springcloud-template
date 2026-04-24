package com.company.codegenerate.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.company.codegenerate.config.CodeGeneratorConfig;
import com.company.codegenerate.config.CodeGeneratorConfig.ModuleInfo;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGeneratorService {

    private final CodeGeneratorConfig codeGeneratorConfig;
    private final DataSource dataSource;

    public void generateCode(String tableName, String moduleName) {
        ModuleInfo moduleInfo = codeGeneratorConfig.getModules().get(moduleName);
        if (moduleInfo == null) {
            throw new IllegalArgumentException("未找到模块配置: " + moduleName);
        }

        String outputPath = moduleInfo.getOutputPath();
        String controllerTemplate = moduleInfo.getControllerTemplate();

        log.info("开始生成代码，表名: {}，模块名: {}，输出路径: {}", tableName, moduleName, outputPath);

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(
                hikariDataSource.getJdbcUrl(),
                hikariDataSource.getUsername(),
                hikariDataSource.getPassword()
        );

        FastAutoGenerator.create(dataSourceConfigBuilder)
                .globalConfig(builder -> builder
                        .author("CodeGenerate")
                        .enableSwagger()
                        .outputDir(outputPath)
                        .dateType(DateType.ONLY_DATE)
                        .build())
                .packageConfig(builder -> builder
                        .parent(moduleInfo.getPackageName())
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        .xml("mapper.xml")
                        .controller("controller")
                        .pathInfo(generatorPathInfo(outputPath))
                        .build())
                .strategyConfig(builder -> builder
                        .addInclude(tableName)
                        .entityBuilder()
                        .enableLombok()
                        .logicDeleteColumnName(moduleInfo.isEnableLogicDelete() ? "deleted" : null)
                        .build()
                        .controllerBuilder()
                        .enableRestStyle()
                        .enableHyphenStyle()
                        .build()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .build()
                        .mapperBuilder()
                        .enableMapperAnnotation()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .build())
                .templateConfig(builder -> builder
                        .entity("/templates/entity.java.vm")
                        .service("/templates/service.java.vm")
                        .serviceImpl("/templates/serviceImpl.java.vm")
                        .mapper("/templates/mapper.java.vm")
                        .xml("/templates/mapper.xml.vm")
                        .controller(controllerTemplate)
                        .build())
                .injectionConfig(consumer -> {
                    consumer.customFile(new HashMap<String, String>() {{
                        put("Req.java", "/templates/req.java.vm");
                        put("Resp.java", "/templates/resp.java.vm");
                    }});
                    consumer.beforeOutputFile((tableInfo, map) -> {
                        tableInfo.setEntityName(tableInfo.getEntityName().replace("Entity", ""));
                    });
                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        log.info("代码生成完成，表名: {}，模块名: {}", tableName, moduleName);
    }

    private Map<OutputFile, String> generatorPathInfo(String outputPath) {
        Map<OutputFile, String> pathInfo = new java.util.HashMap<>();
        pathInfo.put(OutputFile.mapper, outputPath + "/../resources/mapper");
        return pathInfo;
    }
}
