package com.company.tool.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import io.github.jqdi.i18n.core.annotation.I18nField;
import io.github.jqdi.i18n.core.annotation.I18nTable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("app_version")
@I18nTable(i18nTable = "app_version_i18n", i18nRelatedColumn = "app_version_id", relatedValueFromField = "id")
public class AppVersion extends AuditableModel<AppVersion> {
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
    @I18nField(i18nColumn = "release_notes")
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

	private String remark;
}
