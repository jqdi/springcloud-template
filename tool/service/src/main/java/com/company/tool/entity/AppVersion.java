package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import io.github.jqdi.i18n.core.annotation.I18nField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("app_version")
public class AppVersion {
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
    @I18nField(i18nTable = "app_version_i18n", i18nColumn = "release_notes", i18nRelatedColumn = "app_version_id", relatedValueFromField = "id")
    private String releaseNotes;

	private String remark;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
	private String createBy;
	private String updateBy;
}
