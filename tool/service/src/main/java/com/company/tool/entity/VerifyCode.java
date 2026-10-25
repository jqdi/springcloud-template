package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("verify_code")
@Data
@Accessors(chain = true)
public class VerifyCode extends AuditableModel<VerifyCode> {
	/**
	 * ID
	 */
	private Integer id;

	/**
	 * 业务(register:注册,login:登录,changepwd:修改密码)
	 */
	private String type;
	/**
	 * 凭证(手机号|UUID)
	 */
	private String certificate;
	/**
	 * 验证码
	 */
	private String code;
	/**
	 * 有效截止时间
	 */
	private LocalDateTime validTime;
	/**
	 * 状态(1:未使用,2:已使用)
	 */
	private Integer status;
	/**
	 * 最大错误次数
	 */
	private Integer maxErrCount;
	/**
	 * 错误次数
	 */
	private Integer errCount;
	
	/**
	 * 备注信息
	 */
	private String remark;

}