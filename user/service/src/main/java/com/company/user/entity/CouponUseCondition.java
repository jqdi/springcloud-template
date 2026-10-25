package com.company.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("coupon_use_condition")
public class CouponUseCondition extends AuditableModel<CouponUseCondition> {
	private Integer id;

	/**
	 * bean名称(UseCondition的实现类)
	 */
	private String beanName;

	/**
	 * 描述
	 */
	private String descrpition;

	private String remark;
}
