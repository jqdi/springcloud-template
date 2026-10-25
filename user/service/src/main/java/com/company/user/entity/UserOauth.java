package com.company.user.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import io.github.encrypt.annotation.FieldEncrypt;
import io.github.encrypt.bean.Encrypted;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user_oauth")
public class UserOauth extends AuditableModel<UserOauth> implements Encrypted {
	private Integer id;
	
	@TableField("user_id")
	private Integer userId;
	
	@TableField("identity_type")
	private String identityType;
	@FieldEncrypt
	private String identifier;
	private String certificate;

	private String remark;
}
