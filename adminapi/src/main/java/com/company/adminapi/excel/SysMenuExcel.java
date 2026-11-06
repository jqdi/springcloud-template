package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单权限
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysMenuExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * 父菜单ID
	 */
	@ExcelProperty(value = "父菜单ID")
	private Integer parentId;

	/**
	 * 菜单图标
	 */
	@ExcelProperty(value = "菜单图标")
	private String icon;

	/**
	 * 菜单名称
	 */
	@ExcelProperty(value = "菜单名称")
	private String menuName;

	/**
	 * 显示顺序
	 */
	@ExcelProperty(value = "显示顺序")
	private Integer orderNum;

	/**
	 * 路由地址
	 */
	@ExcelProperty(value = "路由地址")
	private String path;

	/**
	 * 组件路径
	 */
	@ExcelProperty(value = "组件路径")
	private String component;

	/**
	 * 路由参数
	 */
	@ExcelProperty(value = "路由参数")
	private String query;

	/**
	 * 菜单类型(M:目录,C:菜单,F:按钮)
	 */
	@ExcelProperty(value = "菜单类型(M:目录,C:菜单,F:按钮)")
	private String menuType;

	/**
	 * 菜单状态(ON:正常,OFF:停用)
	 */
	@ExcelProperty(value = "菜单状态(ON:正常,OFF:停用)")
	private String status;

	/**
	 * 权限标识
	 */
	@ExcelProperty(value = "权限标识")
	private String perms;

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
