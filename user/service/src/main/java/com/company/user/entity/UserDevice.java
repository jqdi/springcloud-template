package com.company.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("user_device")
public class UserDevice extends AuditableModel<UserDevice> {
	private Integer id;

	/**
	 * user_info.id
	 */
	private Integer userId;

	/**
	 * 设备ID
	 */
	private String deviceid;

	/**
	 * 最后登录时间
	 */
	private LocalDateTime lastLoginTime;

	/**
	 * 最后登出时间
	 */
	private LocalDateTime lastLogoutTime;

	private String remark;
}
