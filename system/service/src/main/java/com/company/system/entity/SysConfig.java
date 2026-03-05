package com.company.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;
import lombok.Data;

/**
 * 参数配置
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
@TableName("sys_config")
public class SysConfig extends AuditableModel<SysConfig> {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 编码
	 */
	private String code;

	/**
	 * 值
	 */
	private String value;

	/**
	 * 参数备注
	 */
	private String configRemark;

	/**
	 * 备注
	 */
	private String remark;

    // 审计字段继承AuditableModel或自主添加审计字段
//	/**
//	 * 创建时间
//	 */
//	@TableField(fill = FieldFill.INSERT)
//	private LocalDateTime createTime;
//
//	/**
//	 * 创建人
//	 */
//	@TableField(fill = FieldFill.INSERT)
//	private Integer createBy;
//
//	/**
//	 * 更新时间
//	 */
//	@TableField(fill = FieldFill.INSERT_UPDATE)
//	private LocalDateTime updateTime;
//
//	/**
//	 * 更新人
//	 */
//	@TableField(fill = FieldFill.INSERT_UPDATE)
//	private Integer updateBy;

}