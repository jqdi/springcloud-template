package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("banner_condition")
public class BannerCondition extends AuditableModel<BannerCondition> {
	private Integer id;

	/**
	 * banner.id
	 */
	private Integer bannerId;

	/**
	 * 展示条件(bean名称,banner_show_condition.bean_name)
	 */
	private String showCondition;
	/**
	 * 展示条件值
	 */
	private String showConditionValue;

	private String remark;
}
