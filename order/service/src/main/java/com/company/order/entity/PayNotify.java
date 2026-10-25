package com.company.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("pay_notify")
@Data
@Accessors(chain = true)
public class PayNotify extends AuditableModel<PayNotify> {
	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 支付方式(ali:支付宝,wx:微信,ios:苹果,quick:云闪付)
	 */
	private String method;

	/**
	 * 通知数据
	 */
	private String notifyData;

	/**
	 * 备注信息
	 */
	private String remark;


}