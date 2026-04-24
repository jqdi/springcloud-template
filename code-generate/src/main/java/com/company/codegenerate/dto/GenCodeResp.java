package com.company.codegenerate.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GenCodeResp {

    private List<String> generatedFiles = new ArrayList<>();
    private List<String> skippedFiles = new ArrayList<>();
    private String message;

    public static GenCodeResp success(List<String> generated, List<String> skipped) {
        GenCodeResp resp = new GenCodeResp();
        resp.setGeneratedFiles(generated);
        resp.setSkippedFiles(skipped);
        resp.setMessage("代码生成完成，生成 " + generated.size() + " 个文件，跳过 " + skipped.size() + " 个文件");
        return resp;
    }
}
