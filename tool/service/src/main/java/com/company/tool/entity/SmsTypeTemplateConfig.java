package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("sms_type_template_config")
@Data
@Accessors(chain = true)
public class SmsTypeTemplateConfig extends AuditableModel<SmsTypeTemplateConfig> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * 业务类型(verifycode:验证码,market:营销活动,tips:提示信息)
	 */
	private String type;
	/**
	 * 发送渠道(log:打印日志（测试）,ali:阿里云,tencent:腾讯云)
	 */
	private String channel;
	/**
	 * 模板编码
	 */
	private String templateCode;
	
	/**
	 * 备注信息
	 */
	private String remark;

}