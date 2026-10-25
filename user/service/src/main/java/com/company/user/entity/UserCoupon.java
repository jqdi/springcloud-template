package com.company.user.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user_coupon")
public class UserCoupon extends AuditableModel<UserCoupon> {
	private Integer id;

	/**
	 * user_info.id
	 */
	private Integer userId;

	/**
	 * coupon_template.id
	 */
	private Integer couponTemplateId;

	/**
	 * 优惠券名称
	 */
	private String name;

	/**
	 * 最大优惠金额
	 */
	private BigDecimal maxAmount;

	/**
	 * 折扣
	 */
	private BigDecimal discount;

	/**
	 * 满x金额可用
	 */
	private BigDecimal conditionAmount;

	/**
	 * 有效期开始时间
	 */
	private LocalDateTime beginTime;

	/**
	 * 有效期结束时间
	 */
	private LocalDateTime endTime;

	/**
	 * 状态(nouse:未使用/已使用/已过期/未激活/已失效)
	 */
	private String status;

	/**
	 * 最大叠加数
	 */
	private Integer maxNum;

	private String remark;
}
