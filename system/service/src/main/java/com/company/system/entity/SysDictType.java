package com.company.system.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 字典类型
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_dict_type")
public class SysDictType extends AuditableModel<SysDictType> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 字典名称
	 */
	private String dictName;

	/**
	 * 字典类型
	 */
	private String dictType;

	/**
	 * 状态(ON:正常,OFF:停用)
	 */
	private String status;

	/**
	 * 字典备注
	 */
	private String dictRemark;

	/**
	 * 备注
	 */
	private String remark;
}