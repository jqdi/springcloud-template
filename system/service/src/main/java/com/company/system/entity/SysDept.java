package com.company.system.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 部门
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_dept")
public class SysDept extends AuditableModel<SysDept> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 父部门id
	 */
	private Integer parentId;

	/**
	 * 父部门id列表
	 */
	private String parentIds;

	/**
	 * 部门名称
	 */
	private String name;

	/**
	 * 显示顺序
	 */
	private Integer orderNum;

	/**
	 * 部门状态(ON:正常,OFF:停用)
	 */
	private String status;

	/**
	 * 备注
	 */
	private String remark;
}