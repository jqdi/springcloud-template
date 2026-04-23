package com.company.app.controller;


import com.company.tool.api.feign.AppVersionFeign;
import com.company.tool.api.response.AppVersionCheckResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 版本
 *
 * @author JQ棣
 */
@Validated
@RestController
@RequestMapping("/version")
public class VersionController {

    @Autowired
    private AppVersionFeign appVersionFeign;

    /**
     * 检查
     */
    @GetMapping("/check")
    public AppVersionCheckResp check(@NotBlank(message = "appCode不能为空") String appCode, @NotBlank(message = "version不能为空") String version) {
        return appVersionFeign.check(appCode, version);
    }
}
