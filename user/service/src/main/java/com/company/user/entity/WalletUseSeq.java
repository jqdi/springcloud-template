package com.company.user.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 钱包使用顺序
 */
@Data
@Accessors(chain = true)
@TableName("wallet_use_seq")
public class WalletUseSeq extends AuditableModel<WalletUseSeq> {
	/**
	 * ID
	 */
	private Integer id;
	/**
	 * 唯一码(用于幂等，防止重复入账)
	 */
	private String uniqueCode;
	/**
	 * user_info.id
	 */
	private Integer userId;
	/**
	 * 类型(1~9表示总钱包,2位数表示子钱包)
	 */
	private Integer type;
	/**
	 * 金额(元)
	 */
	private BigDecimal amount;
	/**
	 * 剩余金额(元)
	 */
	private BigDecimal leftAmount;

	private String remark;
}
