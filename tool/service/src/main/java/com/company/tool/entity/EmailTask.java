package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("email_task")
@Data
@Accessors(chain = true)
public class EmailTask extends AuditableModel<EmailTask> {
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
	 * 备注信息
	 */
	private String remark;
}