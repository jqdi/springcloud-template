package com.company.codegenerate.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenCodeReq {

    private String moduleName;
    private List<String> tableNames;
    private String author = "CodeGenerator";
    private boolean overwrite = false;
}
