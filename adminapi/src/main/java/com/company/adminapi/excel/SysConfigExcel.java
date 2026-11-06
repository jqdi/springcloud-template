package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参数配置
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysConfigExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * 名称
	 */
	@ExcelProperty(value = "名称")
	private String name;

	/**
	 * 编码
	 */
	@ExcelProperty(value = "编码")
	private String code;

	/**
	 * 值
	 */
	@ExcelProperty(value = "值")
	private String value;

	/**
	 * 参数备注
	 */
	@ExcelProperty(value = "参数备注")
	private String configRemark;

	/**
	 * 备注
	 */
	@ExcelProperty(value = "备注")
	private String remark;

	/**
	 * 创建时间
	 */
	@ExcelProperty(value = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	@ExcelProperty(value = "创建人", converter = SysUserAuditConverter.class)
	private String createBy;

	/**
	 * 更新时间
	 */
	@ExcelProperty(value = "更新时间")
	private LocalDateTime updateTime;

	/**
	 * 更新人
	 */
	@ExcelProperty(value = "更新人", converter = SysUserAuditConverter.class)
	private String updateBy;

}
