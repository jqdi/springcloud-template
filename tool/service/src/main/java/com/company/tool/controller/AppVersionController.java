package com.company.tool.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.framework.context.HeaderContextUtil;
import com.company.tool.api.interfaces.AppVersionApi;
import com.company.tool.api.response.AppVersionCheckResp;
import com.company.tool.entity.AppVersion;
import com.company.tool.service.AppVersionService;

import cn.hutool.core.text.CharSequenceUtil;

@RestController
@RequestMapping(value = "/appVersion")
public class AppVersionController implements AppVersionApi {
    @Autowired
    private AppVersionService appVersionService;

    @Override
    public AppVersionCheckResp check(String appCode, String currentVersion) {
        AppVersion latestAppVersion = appVersionService.selectLastByAppCode(appCode);
        if (latestAppVersion == null) {
            // 未找到应用版本信息，无需更新
            return AppVersionCheckResp.noUpdate();
        }

        String latestVersion = latestAppVersion.getVersion();
        String minSupportedVersion = latestAppVersion.getMinSupportedVersion();

        if (CharSequenceUtil.compareVersion(currentVersion, latestVersion) >= 0) {
            // 当前版本>=最新版本，无需更新
            return AppVersionCheckResp.noUpdate();
        }

        if (CharSequenceUtil.compareVersion(currentVersion, minSupportedVersion) < 0) {
            // 当前版本<最低支持版本，强制更新
            return AppVersionCheckResp.forceUpdate(latestVersion, latestAppVersion.getDownloadUrl(), latestAppVersion.getReleaseNotes());
        }
        // 不强制更新，但不一定提示更新，要看灰度情况
        BigDecimal grayPercent = latestAppVersion.getGrayPercent();
        if (grayPercent.compareTo(BigDecimal.ONE) >= 0) {
            // 灰度比例达到100%，则全量
            return AppVersionCheckResp.tipsUpdate(latestVersion, latestAppVersion.getDownloadUrl(), latestAppVersion.getReleaseNotes());
        }
        // 灰度比例未达到100%
        Integer updateCount = latestAppVersion.getUpdateCount();
        Integer totalCount = latestAppVersion.getTotalCount();
        int grayCount = grayPercent.multiply(new BigDecimal(totalCount)).intValue();
        if (updateCount < grayCount) {
            // 更新量未超过灰度量
            return AppVersionCheckResp.tipsUpdate(latestVersion, latestAppVersion.getDownloadUrl(), latestAppVersion.getReleaseNotes());
        }
        // 更新量超过灰度量
        if (!continueUpdate()) {
            // 不继续更新，不提示更新
            return AppVersionCheckResp.noUpdate();
        }
        return AppVersionCheckResp.tipsUpdate(latestVersion, latestAppVersion.getDownloadUrl(), latestAppVersion.getReleaseNotes());
    }

    private boolean continueUpdate() {
        Integer userId = HeaderContextUtil.currentUserIdInt();
        if (userId == null) {
            return true;
        }
        // TODO: 实现白名单检查逻辑
        return false;
    }
}