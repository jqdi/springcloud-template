package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("subscribe_template_grant")
@Data
@Accessors(chain = true)
public class SubscribeTemplateGrant extends AuditableModel<SubscribeTemplateGrant> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * openid
	 */
	private String openid;
	/**
	 * 模板编码(subscribe_template.pri_tmpl_id)
	 */
	private String templateCode;
	/**
	 * 授权总次数
	 */
	private Integer totalNum;
	/**
	 * 已使用次数
	 */
	private Integer useNum;
	
	/**
	 * 备注信息
	 */
	private String remark;

}