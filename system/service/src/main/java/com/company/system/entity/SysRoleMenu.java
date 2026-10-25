package com.company.system.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 角色和菜单关联
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu extends AuditableModel<SysRoleMenu> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 角色ID
	 */
	private Integer roleId;

	/**
	 * 菜单ID
	 */
	private Integer menuId;

	/**
	 * 备注
	 */
	private String remark;



}