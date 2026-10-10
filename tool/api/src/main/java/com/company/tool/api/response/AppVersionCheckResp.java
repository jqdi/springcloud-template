package com.company.tool.api.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppVersionCheckResp {
    /**
     * 是否有更新（必填）
     */
    Boolean hasUpdate;

    // 以下字段hasUpdate=true 时，有值
    /**
     * 是否强制更新
     */
    Boolean forceUpdate;
    /**
     * 最新版本号
     */
    String latestVersion;
    /**
     * 安装包下载地址
     */
    String downloadUrl;

    /**
     * 发布说明
     */
    String releaseNotes;

    /**
     * 不更新
     */
    public static AppVersionCheckResp noUpdate() {
        AppVersionCheckResp resp = new AppVersionCheckResp();
        resp.setHasUpdate(false);
        return resp;
    }

    /**
     * 提示更新
     */
    public static AppVersionCheckResp tipsUpdate(String latestVersion, String downloadUrl, String releaseNotes) {
        AppVersionCheckResp resp = new AppVersionCheckResp();
        resp.setHasUpdate(true);
        resp.setForceUpdate(false);
        resp.setLatestVersion(latestVersion);
        resp.setDownloadUrl(downloadUrl);
        resp.setReleaseNotes(releaseNotes);
        return resp;
    }

    /**
     * 更新强制
     */
    public static AppVersionCheckResp forceUpdate(String latestVersion, String downloadUrl, String releaseNotes) {
        AppVersionCheckResp resp = new AppVersionCheckResp();
        resp.setHasUpdate(true);
        resp.setForceUpdate(true);
        resp.setLatestVersion(latestVersion);
        resp.setDownloadUrl(downloadUrl);
        resp.setReleaseNotes(releaseNotes);
        return resp;
    }
}
