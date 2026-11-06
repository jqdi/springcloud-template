package com.company.system.api.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 角色信息
 * 
 * @author CodeGenerator
 * @date 2023-04-14
 */
@Data
public class SysRoleResp {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 角色权限字符串
	 */
	private String roleKey;

	/**
	 * 显示顺序
	 */
	private Integer roleSort;

	/**
	 * 数据范围(1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限)
	 */
	private String dataScope;

	/**
	 * 角色状态(ON:正常,OFF:停用)
	 */
	private String status;

	/**
	 * 角色备注
	 */
	private String roleRemark;

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

	/**
	 * 有权限的菜单id
	 */
	private Set<Integer> menuIds;

}