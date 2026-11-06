package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysUserExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * 账号
	 */
	@ExcelProperty(value = "账号")
	private String account;

	/**
	 * 昵称
	 */
	@ExcelProperty(value = "昵称")
	private String nickname;

	/**
	 * 用户邮箱
	 */
	@ExcelProperty(value = "用户邮箱")
	private String email;

	/**
	 * 手机号码
	 */
	@ExcelProperty(value = "手机号码")
	private String phonenumber;

	/**
	 * 用户性别(0男 1女 2未知)
	 */
	@ExcelProperty(value = "用户性别(0男 1女 2未知)")
	private String sex;

	/**
	 * 头像地址
	 */
	@ExcelProperty(value = "头像地址")
	private String avatar;

	/**
	 * 帐号状态(ON:正常,OFF:停用)
	 */
	@ExcelProperty(value = "帐号状态(ON:正常,OFF:停用)")
	private String status;

	/**
	 * 部门ID
	 */
	@ExcelProperty(value = "部门ID")
	private Integer deptId;

	/**
	 * 用户备注
	 */
	@ExcelProperty(value = "用户备注")
	private String userRemark;

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
