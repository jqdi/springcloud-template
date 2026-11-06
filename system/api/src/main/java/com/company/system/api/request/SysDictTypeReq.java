package com.company.system.api.request;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 字典类型
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysDictTypeReq {

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

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 更新人
	 */
	private String updateBy;

}