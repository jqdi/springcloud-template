package com.company.system.api.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 参数配置
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysConfigResp {

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