package com.company.tool.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("app_info")
public class AppInfo extends AuditableModel<AppInfo> {
	private Integer id;

	/**
	 * 唯一标识（写死到客户端）
	 */
	private String appCode;

	/**
	 * 名称
	 */
	private String appName;

	/**
	 * 平台(Android、iOS)
	 */
	private String platform;

	/**
	 * 图标
	 */
	private String logo;

	private String remark;
}
