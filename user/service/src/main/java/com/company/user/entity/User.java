package com.company.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("sys_user")
public class User extends AuditableModel<User> {
	private Long id;
	private String name;
	private String username;
	private String password;
	private Integer deleted;
	private String avatar;

	private Integer status;
	private String creator;
	private String updater;
}
