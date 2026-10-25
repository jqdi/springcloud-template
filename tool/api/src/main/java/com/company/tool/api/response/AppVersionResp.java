package com.company.tool.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppVersionResp {

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
     * 发布时间
     */
    private LocalDateTime releaseTime;

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
     * 总设备量（创建/发布时统计 device_info 设备数）
     */
    private Long totalCount;

    /**
     * 已升级设备数（device_info.version = 本记录 version 的设备数）
     */
    private Long updateCount;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
}
