package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色信息
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysRoleExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * 角色名称
	 */
	@ExcelProperty(value = "角色名称")
	private String roleName;

	/**
	 * 角色权限字符串
	 */
	@ExcelProperty(value = "角色权限字符串")
	private String roleKey;

	/**
	 * 显示顺序
	 */
	@ExcelProperty(value = "显示顺序")
	private Integer roleSort;

	/**
	 * 数据范围(1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限)
	 */
	@ExcelProperty(value = "数据范围(1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限)")
	private String dataScope;

	/**
	 * 角色状态(ON:正常,OFF:停用)
	 */
	@ExcelProperty(value = "角色状态(ON:正常,OFF:停用)")
	private String status;

	/**
	 * 角色备注
	 */
	@ExcelProperty(value = "角色备注")
	private String roleRemark;

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
