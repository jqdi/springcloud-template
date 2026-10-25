package com.company.tool.api.request;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppVersionReq {

    /**
     * 主键（编辑时传，新增时不传）
     */
    private Integer id;

    /**
     * app_info.app_code
     */
    private String appCode;

    /**
     * 版本号
     */
    private String version;

    /**
     * 最低支持版本（低于此版本必须升级）
     */
    private String minSupportedVersion;

    /**
     * 安装包下载地址
     */
    private String downloadUrl;

    /**
     * 发布说明
     */
    private String releaseNotes;

    /**
     * 灰度比例(0.00-1.00)
     */
    private BigDecimal grayPercent;

    /**
     * 备注
     */
    private String remark;
}
