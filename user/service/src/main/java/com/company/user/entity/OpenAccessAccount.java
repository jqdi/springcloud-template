package com.company.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("open_access_account")
public class OpenAccessAccount extends AuditableModel<OpenAccessAccount> {
	private Integer id;
	
	private String appid;
	private String appKey;
	
	private String remark;
}
