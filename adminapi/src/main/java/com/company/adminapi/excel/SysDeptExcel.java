package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysDeptExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * 父部门id
	 */
	@ExcelProperty(value = "父部门id")
	private Integer parentId;

	/**
	 * 父部门id列表
	 */
	@ExcelProperty(value = "父部门id列表")
	private String parentIds;

	/**
	 * 部门名称
	 */
	@ExcelProperty(value = "部门名称")
	private String name;

	/**
	 * 显示顺序
	 */
	@ExcelProperty(value = "显示顺序")
	private Integer orderNum;

	/**
	 * 部门状态(ON:正常,OFF:停用)
	 */
	@ExcelProperty(value = "部门状态(ON:正常,OFF:停用)")
	private String status;

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
