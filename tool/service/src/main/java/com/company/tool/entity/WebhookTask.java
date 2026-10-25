package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("webhook_task")
@Data
@Accessors(chain = true)
public class WebhookTask extends AuditableModel<WebhookTask> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * 业务类型(verifycode:验证码,market:营销活动,tips:提示信息)
	 */
	private String type;
	/**
	 * 计划发送时间
	 */
	private LocalDateTime planSendTime;
	/**
	 * 超时取消发送时间
	 */
	private LocalDateTime overTime;
	/**
	 * 模板参数json
	 */
	private String templateParamJson;
	/**
	 * 实际发送时间
	 */
	private LocalDateTime actualSendTime;
	/**
	 * 状态(1:待发送,2:待定时发送,11:发送MQ成功,12:MQ消费成功,21:请求成功,22:请求失败,31:取消发送)
	 */
	private Integer status;
	/**
	 * key
	 */
	@TableField(value = "`key`")
	private String key;
	/**
	 * 企微机器人内容
	 */
	private String content;
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