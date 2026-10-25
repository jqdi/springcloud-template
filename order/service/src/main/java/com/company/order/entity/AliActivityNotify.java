package com.company.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("ali_activity_notify")
@Data
@Accessors(chain = true)
public class AliActivityNotify extends AuditableModel<AliActivityNotify> {
	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 消息(spiordersend:订单券发放,from:FROM消息)
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