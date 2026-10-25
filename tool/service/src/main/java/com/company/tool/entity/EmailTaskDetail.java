package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import io.github.encrypt.annotation.FieldEncrypt;
import io.github.encrypt.bean.Encrypted;
import lombok.Data;
import lombok.experimental.Accessors;

@TableName("email_task_detail")
@Data
@Accessors(chain = true)
public class EmailTaskDetail extends AuditableModel<EmailTaskDetail> implements Encrypted {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * email_task.id
	 */
	private Integer taskId;
	/**
	 * 发件邮箱
	 */
	private String fromEmail;
	/**
	 * 收件邮箱
	 */
	@FieldEncrypt
	private String toEmail;
	/**
	 * 模板参数json
	 */
	private String templateParamJson;
	/**
	 * 计划发送时间
	 */
	private LocalDateTime planSendTime;
	/**
	 * 实际发送时间
	 */
	private LocalDateTime actualSendTime;
	/**
	 * 状态(1:待发送,2:待定时发送,11:发送MQ成功,12:MQ消费成功,21:请求成功,22:请求失败,31:取消发送)
	 */
	private Integer status;
	/**
	 * 邮件标题
	 */
	private String title;
	/**
	 * 邮件内容
	 */
	private String content;

	/**
	 * 备注信息
	 */
	private String remark;
}