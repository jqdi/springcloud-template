package com.company.adminapi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.company.adminapi.easyexcel.converter.SysUserConverter;
import com.company.adminapi.easyexcel.converter.SysUserAuditConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志记录
 *
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysOperLogExcel {

	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	private Integer id;

	/**
	 * sys_user.id
	 */
	@ExcelProperty(value = "操作人", converter = SysUserConverter.class)
	private Integer sysUserId;
	
	/**
	 * 模块标题
	 */
	@ExcelProperty(value = "模块标题")
	private String title;

	/**
	 * 业务类型(0其它 1新增 2修改 3删除)
	 */
	@ExcelProperty(value = "业务类型(0其它 1新增 2修改 3删除)")
	private Integer businessType;

	/**
	 * 方法名称
	 */
	@ExcelProperty(value = "方法名称")
	private String method;

	/**
	 * 请求方式
	 */
	@ExcelProperty(value = "请求方式")
	private String requestMethod;

	/**
	 * 操作类别(0其它 1后台用户 2手机端用户)
	 */
	@ExcelProperty(value = "操作类别(0其它 1后台用户 2手机端用户)")
	private Integer operatorType;

	/**
	 * 操作人员
	 */
	@ExcelProperty(value = "操作人员")
	private String operName;

	/**
	 * 部门名称
	 */
	@ExcelProperty(value = "部门名称")
	private String deptName;

	/**
	 * 请求URL
	 */
	@ExcelProperty(value = "请求URL")
	private String operUrl;

	/**
	 * 主机地址
	 */
	@ExcelProperty(value = "主机地址")
	private String operIp;

	/**
	 * 操作地点
	 */
	@ExcelProperty(value = "操作地点")
	private String operLocation;

	/**
	 * 请求参数
	 */
	@ExcelProperty(value = "请求参数")
	private String operParam;

	/**
	 * 返回参数
	 */
	@ExcelProperty(value = "返回参数")
	private String jsonResult;

	/**
	 * 操作状态(0正常 1异常)
	 */
	@ExcelProperty(value = "操作状态(0正常 1异常)")
	private Integer status;

	/**
	 * 错误消息
	 */
	@ExcelProperty(value = "错误消息")
	private String errorMsg;

	/**
	 * 消耗时间
	 */
	@ExcelProperty(value = "消耗时间")
	private Integer costTime;

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
