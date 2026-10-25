package com.company.system.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 用户和角色关联
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_user_role")
public class SysUserRole extends AuditableModel<SysUserRole> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 角色ID
	 */
	private Integer roleId;

	/**
	 * 备注
	 */
	private String remark;



}