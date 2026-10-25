package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import io.github.encrypt.annotation.FieldEncrypt;
import io.github.encrypt.bean.Encrypted;
import lombok.Data;
import lombok.experimental.Accessors;

@TableName("sms_record")
@Data
@Accessors(chain = true)
public class SmsRecord extends AuditableModel<SmsRecord> implements Encrypted {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * 发送渠道(log:打印日志（测试）,ali:阿里云,tencent:腾讯云)
	 */
	private String channel;
	/**
	 * 手机号码
	 */
	@FieldEncrypt
	private String mobile;
	/**
	 * 短信签名(2~12个字符)，如：【XX公司】，【】不用填写
	 */
	private String signName;
	/**
	 * 模板编码
	 */
	private String templateCode;
	/**
	 * 模板参数json
	 */
	private String templateParamJson;
	/**
	 * 短信内容
	 */
	private String content;
	
	/**
	 * 结果(success:成功,fail:失败)
	 */
	private String result;
	/**
	 * 信息
	 */
	private String message;
	/**
	 * 请求ID
	 */
	private String requestId;
	
	/**
	 * 备注信息
	 */
	private String remark;

}