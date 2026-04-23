package com.company.tool.controller;

import cn.hutool.core.text.CharSequenceUtil;

import com.company.tool.api.feign.AppVersionFeign;
import com.company.tool.api.response.AppVersionCheckResp;
import com.company.tool.entity.AppVersion;
import com.company.tool.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/appVersion")
public class AppVersionController implements AppVersionFeign {
    @Autowired
    private AppVersionService appVersionService;

    @Override
    public AppVersionCheckResp check(String appCode, String currentVersion) {
        AppVersion lastAppVersion = appVersionService.selectLastByAppCode(appCode);
        if (lastAppVersion == null) {
            // 未找到应用版本信息，无需更新
            AppVersionCheckResp resp = new AppVersionCheckResp();
            resp.setHasUpdate(false);
            return resp;
        }

        String version = lastAppVersion.getVersion();
        String minSupportedVersion = lastAppVersion.getMinSupportedVersion();

        if (CharSequenceUtil.compareVersion(version, currentVersion) == 0) {
            // 当前版本是最新版本，无需更新
            AppVersionCheckResp resp = new AppVersionCheckResp();
            resp.setHasUpdate(false);
            return resp;
        }

        // 需要更新
        AppVersionCheckResp resp = new AppVersionCheckResp();
        resp.setHasUpdate(true);
        if (CharSequenceUtil.compareVersion(minSupportedVersion, currentVersion) > 0) {
            // 当前版本过低，强制更新
            resp.setForceUpdate(true);
        } else {
            // 当前版本不是最低支持版本，提示更新
            resp.setForceUpdate(false);
        }
        resp.setLatestVersion(version);
        resp.setDownloadUrl(lastAppVersion.getDownloadUrl());
        resp.setReleaseNotes(lastAppVersion.getReleaseNotes());
        return resp;
    }
}
