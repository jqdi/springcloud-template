package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("subscribe_group_type")
@Data
@Accessors(chain = true)
public class SubscribeGroupType extends AuditableModel<SubscribeGroupType> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * 订阅组
	 */
	private String group;
	/**
	 * 业务类型(多个英文逗号分隔,最多3个)
	 */
	private String types;
	
	/**
	 * 备注信息
	 */
	private String remark;

}