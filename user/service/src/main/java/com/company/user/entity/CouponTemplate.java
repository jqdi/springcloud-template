package com.company.user.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("coupon_template")
public class CouponTemplate extends AuditableModel<CouponTemplate> {
	private Integer id;

	/**
	 * 优惠券名称
	 */
	private String name;

	/**
	 * 最大优惠金额(元)
	 */
	private BigDecimal maxAmount;

	/**
	 * 折扣
	 */
	private BigDecimal discount;

	/**
	 * 满X元可用
	 */
	private BigDecimal conditionAmount;

	/**
	 * 发放开始时间
	 */
	private LocalDateTime beginTime;

	/**
	 * 发放结束期限
	 */
	private LocalDateTime endTime;

	/**
	 * 有效期计算方式(fix:领券起固定有效期，ps:固定开始时间-开始时间，fx:指定行为激活后x天有效)
	 */
	private String periodType;

	/**
	 * 发放量
	 */
	private Integer totalNum;

	/**
	 * 剩余量
	 */
	private Integer leftNum;

	/**
	 * 每用户限领量
	 */
	private Integer limitNum;

	/**
	 * 使用说明
	 */
	private String useDescription;

	private String remark;
}
