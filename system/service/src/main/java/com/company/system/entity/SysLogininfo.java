package com.company.system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 系统访问记录
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_logininfo")
public class SysLogininfo extends AuditableModel<SysLogininfo> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * sys_user.id
	 */
	private Integer sysUserId;
	
	/**
	 * 登录时间
	 */
	private LocalDateTime loginTime;

	/**
	 * 登录账户
	 */
	private String account;

	/**
	 * 设备(WEB:web端)
	 */
	private String device;

	/**
	 * 平台
	 */
	private String platform;

	/**
	 * 操作系统
	 */
	private String operator;

	/**
	 * 版本
	 */
	private String version;

	/**
	 * 设备ID
	 */
	private String deviceid;
	
	/**
	 * 渠道
	 */
	private String channel;

	/**
	 * ip
	 */
	private String ip;

	/**
	 * 登录地区(根据ip获得)
	 */
	private String address;

	/**
	 * 来源
	 */
	private String source;

	/**
	 * 语言
	 */
	private String lang;

	/**
	 * 备注
	 */
	private String remark;



}