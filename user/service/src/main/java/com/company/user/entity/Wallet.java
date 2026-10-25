package com.company.user.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 钱包
 */
@Data
@Accessors(chain = true)
@TableName("wallet")
public class Wallet extends AuditableModel<Wallet> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * user_info.id
	 */
	private Integer userId;
	/**
	 * 类型(1~9表示总钱包,2位数表示子钱包)
	 */
	private Integer type;
	/**
	 * 余额(元)
	 */
	private BigDecimal balance;
	/**
	 * 状态(1:正常,2:风控中,3:冻结)
	 */
	private Integer status;

	private String remark;
}
