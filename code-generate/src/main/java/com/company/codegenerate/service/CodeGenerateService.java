package com.company.codegenerate.service;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.company.codegenerate.config.CodeGenerateProperties;
import com.company.codegenerate.dto.GenCodeReq;
import com.company.codegenerate.dto.GenCodeResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGenerateService {

    private final CodeGenerateProperties properties;

    public GenCodeResp generate(GenCodeReq req) {
        String moduleName = req.getModuleName();
        if (moduleName == null || moduleName.trim().isEmpty()) {
            GenCodeResp resp = new GenCodeResp();
            resp.setMessage("moduleName 不能为空");
            return resp;
        }

        String projectRoot = Paths.get(System.getProperty("user.dir"))
                .resolve(properties.getOutputBasePath())
                .normalize()
                .toAbsolutePath()
                .toString();

        List<String> generated = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (String tableName : req.getTableNames()) {
            generateForTable(moduleName, tableName, req.getAuthor(), req.isOverwrite(), generated, skipped, projectRoot);
        }

        return GenCodeResp.success(generated, skipped);
    }

    private void generateForTable(String moduleName, String tableName, String author, boolean overwrite,
                                  List<String> generated, List<String> skipped, String projectRoot) {
        boolean isSplit = isSplitModule(moduleName, projectRoot);
        String serviceJavaPath = serviceJavaPath(moduleName, isSplit, projectRoot);
        String serviceXmlPath = serviceXmlPath(moduleName, isSplit, projectRoot);
        String apiJavaPath = apiJavaPath(moduleName, isSplit, projectRoot);
        String adminapiJavaPath = adminapiJavaPath(projectRoot);

        // Compute entity name from table name (strip prefix, convert to PascalCase)
        String strippedTable = tableName;
        for (String prefix : properties.getTablePrefixes()) {
            if (tableName.startsWith(prefix)) {
                strippedTable = tableName.substring(prefix.length());
                break;
            }
        }
        String entityName = toPascalCase(strippedTable);

        Map<String, Object> customMap = buildCustomMap(moduleName, entityName, isSplit);

        List<CustomFile> customFiles = buildCustomFiles(moduleName, entityName, apiJavaPath, adminapiJavaPath, overwrite);

        final String entityNameFinal = entityName;

        FastAutoGenerator.create(
                        properties.getDatabase().getUrl(),
                        properties.getDatabase().getUsername(),
                        properties.getDatabase().getPassword())
                .globalConfig(builder -> builder
                        .author(author)
                        .outputDir(serviceJavaPath)
                        .disableOpenDir())
                .packageConfig(builder -> builder
                        .parent("com.company." + moduleName)
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, serviceXmlPath)))
                .templateConfig(builder -> builder
                        .entity("/templates/entity.java")
                        .mapper("/templates/mapper.java")
                        .service("/templates/service.java")
                        .controller("/templates/controller.java")
                        .disable(TemplateType.SERVICE_IMPL))
                .strategyConfig(builder -> builder
                        .addInclude(tableName)
                        .addTablePrefix(properties.getTablePrefixes().toArray(new String[0]))
                        .entityBuilder()
                        .enableLombok()
                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .controllerBuilder()
                        .enableRestStyle())
                .injectionConfig(builder -> builder
                        .customMap(customMap)
                        .customFile(customFiles))
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        log.info("表 {} 代码生成完成，entity={}", tableName, entityNameFinal);
        generated.add(tableName + " -> " + entityNameFinal);
    }

    private List<CustomFile> buildCustomFiles(String moduleName, String entityName,
                                              String apiJavaPath, String adminapiJavaPath, boolean overwrite) {
        List<CustomFile> files = new ArrayList<>();

        // feign
        CustomFile.Builder feignBuilder = new CustomFile.Builder()
                .fileName(entityName + "Feign.java")
                .filePath(apiJavaPath + "/com/company/" + moduleName + "/api/feign/")
                .templatePath("/templates/feign.java.vm");
        if (overwrite) feignBuilder.enableFileOverride();
        files.add(feignBuilder.build());

        // req
        CustomFile.Builder reqBuilder = new CustomFile.Builder()
                .fileName(entityName + "Req.java")
                .filePath(apiJavaPath + "/com/company/" + moduleName + "/api/request/")
                .templatePath("/templates/req.java.vm");
        if (overwrite) reqBuilder.enableFileOverride();
        files.add(reqBuilder.build());

        // resp
        CustomFile.Builder respBuilder = new CustomFile.Builder()
                .fileName(entityName + "Resp.java")
                .filePath(apiJavaPath + "/com/company/" + moduleName + "/api/response/")
                .templatePath("/templates/resp.java.vm");
        if (overwrite) respBuilder.enableFileOverride();
        files.add(respBuilder.build());

        // enum
        CustomFile.Builder enumBuilder = new CustomFile.Builder()
                .fileName(entityName + "Enum.java")
                .filePath(apiJavaPath + "/com/company/" + moduleName + "/api/enums/")
                .templatePath("/templates/enum.java.vm");
        if (overwrite) enumBuilder.enableFileOverride();
        files.add(enumBuilder.build());

        // adminapi controller
        CustomFile.Builder adminCtrlBuilder = new CustomFile.Builder()
                .fileName(entityName + "Controller.java")
                .filePath(adminapiJavaPath + "/com/company/adminapi/controller/")
                .templatePath("/templates/adminapiController.java.vm");
        if (overwrite) adminCtrlBuilder.enableFileOverride();
        files.add(adminCtrlBuilder.build());

        // adminapi excel
        CustomFile.Builder adminExcelBuilder = new CustomFile.Builder()
                .fileName(entityName + "Excel.java")
                .filePath(adminapiJavaPath + "/com/company/adminapi/excel/")
                .templatePath("/templates/adminapiExcel.java.vm");
        if (overwrite) adminExcelBuilder.enableFileOverride();
        files.add(adminExcelBuilder.build());

        return files;
    }

    private boolean isSplitModule(String moduleName, String projectRoot) {
        return Files.isDirectory(Paths.get(projectRoot, moduleName, "api"))
                && Files.isDirectory(Paths.get(projectRoot, moduleName, "service"));
    }

    private String serviceJavaPath(String moduleName, boolean isSplit, String projectRoot) {
        if (isSplit) {
            return projectRoot + File.separator + moduleName + File.separator + "service"
                    + File.separator + "src" + File.separator + "main" + File.separator + "java";
        }
        return projectRoot + File.separator + moduleName
                + File.separator + "src" + File.separator + "main" + File.separator + "java";
    }

    private String serviceXmlPath(String moduleName, boolean isSplit, String projectRoot) {
        if (isSplit) {
            return projectRoot + File.separator + moduleName + File.separator + "service"
                    + File.separator + "src" + File.separator + "main" + File.separator + "resources"
                    + File.separator + "mapper";
        }
        return projectRoot + File.separator + moduleName
                + File.separator + "src" + File.separator + "main" + File.separator + "resources"
                + File.separator + "mapper";
    }

    private String apiJavaPath(String moduleName, boolean isSplit, String projectRoot) {
        if (isSplit) {
            return projectRoot + File.separator + moduleName + File.separator + "api"
                    + File.separator + "src" + File.separator + "main" + File.separator + "java";
        }
        return projectRoot + File.separator + moduleName
                + File.separator + "src" + File.separator + "main" + File.separator + "java";
    }

    private String adminapiJavaPath(String projectRoot) {
        return projectRoot + File.separator + "adminapi"
                + File.separator + "src" + File.separator + "main" + File.separator + "java";
    }

    private Map<String, Object> buildCustomMap(String moduleName, String entityName, boolean isSplit) {
        Map<String, Object> map = new HashMap<>();
        String modulePkg = "com.company." + moduleName;
        String feignPkg = modulePkg + (isSplit ? ".api.feign" : ".feign");
        String reqPkg = modulePkg + (isSplit ? ".api.request" : ".request");
        String respPkg = modulePkg + (isSplit ? ".api.response" : ".response");
        String enumPkg = modulePkg + (isSplit ? ".api.enums" : ".enums");
        String entityLower = Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);
        String feignClientValue = "template-" + moduleName;

        map.put("moduleName", moduleName);
        map.put("modulePkg", modulePkg);
        map.put("feignPkg", feignPkg);
        map.put("reqPkg", reqPkg);
        map.put("respPkg", respPkg);
        map.put("enumPkg", enumPkg);
        map.put("feignPath", "/" + entityLower);
        map.put("feignClientValue", feignClientValue);
        map.put("entityLower", entityLower);
        map.put("adminapiPkg", "com.company.adminapi");
        return map;
    }

    private String toPascalCase(String input) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = true;
        for (char c : input.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
