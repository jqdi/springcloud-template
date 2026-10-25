package com.company.system.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 字典数据
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_dict_data")
public class SysDictData extends AuditableModel<SysDictData> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 字典类型
	 */
	private String dictType;

	/**
	 * 字典编码
	 */
	private String dictCode;

	/**
	 * 字典值
	 */
	private String dictValue;

	/**
	 * 字典排序
	 */
	private Integer dictSort;

	/**
	 * 是否默认(Y:是,N:否)
	 */
	private String isDefault;

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