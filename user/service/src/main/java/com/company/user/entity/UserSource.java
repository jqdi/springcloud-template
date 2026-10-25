package com.company.user.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user_source")
public class UserSource extends AuditableModel<UserSource> {
	private Integer id;

	/**
	 * 设备ID
	 */
	private String deviceid;
	
	/**
	 * 来源
	 */
	private String source;
	
	/**
	 * 时间
	 */
	private LocalDateTime time;

	private String remark;
}
