package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysDictTypeExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * 字典名称
	 */
	@ExcelProperty(value = "字典名称")
	private String dictName;

	/**
	 * 字典类型
	 */
	@ExcelProperty(value = "字典类型")
	private String dictType;

	/**
	 * 状态(ON:正常,OFF:停用)
	 */
	@ExcelProperty(value = "状态(ON:正常,OFF:停用)")
	private String status;

	/**
	 * 字典备注
	 */
	@ExcelProperty(value = "字典备注")
	private String dictRemark;

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
