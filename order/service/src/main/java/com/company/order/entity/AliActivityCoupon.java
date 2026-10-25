package com.company.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("ali_activity_coupon")
@Data
@Accessors(chain = true)
public class AliActivityCoupon extends AuditableModel<AliActivityCoupon> {
	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 活动id
	 */
	private String activityId;
	
	/**
	 * 事件id。可用于业务去重。
	 */
	private String eventId;
	
	/**
	 * 事件创建时间
	 */
	private String eventTime;

	/**
	 * 支付宝券ID
	 */
	private String voucherId;

	/**
	 * 用户领取的券码code,支付宝商家券活动才会返回券码，其他优惠券活动该值为空
	 */
	private String voucherCode;

	/**
	 * 领券的支付宝user_id账号
	 */
	private String receiveUserId;
	
	/**
	 * 备注信息
	 */
	private String remark;


}