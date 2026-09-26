package com.company.tool.api.interfaces;


import com.company.tool.api.response.AppVersionCheckResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface AppVersionApi {

    /**
     * 检查
     *
     * @param appCode
     * @param currentVersion
     * @return
     */
    @GetMapping("/check")
    AppVersionCheckResp check(@RequestParam("appCode") String appCode, @RequestParam("currentVersion") String currentVersion);
}