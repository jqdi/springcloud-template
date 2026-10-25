package com.company.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user_info")
public class UserInfo extends AuditableModel<UserInfo> {
	private Integer id;

	private String nickname;
	private String avatar;

	private String remark;
}
