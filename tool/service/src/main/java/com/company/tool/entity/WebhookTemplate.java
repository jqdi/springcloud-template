package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("webhook_template")
@Data
@Accessors(chain = true)
public class WebhookTemplate extends AuditableModel<WebhookTemplate> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * 业务类型(verifycode:验证码,market:营销活动,tips:提示信息)
	 */
	private String type;
	/**
	 * key
	 */
	@TableField(value = "`key`")
	private String key;
	/**
	 * 模板内容
	 */
	private String templateContent;
	/**
	 * 英文逗号分，隔userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list
	 */
	private String mentionedList;
	/**
	 * 英文逗号分，手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
	 */
	private String mentionedMobileList;

	/**
	 * 备注信息
	 */
	private String remark;

}