package com.company.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("wx_pay_refund")
@Data
@Accessors(chain = true)
public class WxPayRefund extends AuditableModel<WxPayRefund> {
	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 申请商户号的appid或商户号绑定的appid
	 */
	private String appid;

	/**
	 * 商户号
	 */
	private String mchid;

	/**
	 * 随机字符串
	 */
	private String nonceStr;

	/**
	 * 签名
	 */
	private String sign;

	/**
	 * 商户订单号
	 */
	private String outTradeNo;
	
	/**
	 * 商户退款单号
	 */
	private String outRefundNo;
	/**
	 * 订单金额(分)
	 */
	private Integer totalFee;
	/**
	 * 订单金额(分)
	 */
	private Integer refundFee;

	/**
	 * 微信退款单号
	 */
	private String refundId;
	/**
	 * 现金支付金额(分)
	 */
	private Integer cashFee;
	/**
	 * 回调-退款状态(PROCESSING/SUCCESS/CHANGE/REFUNDCLOSE)
	 */
	private String refundStatus;
	
	/**
	 * 备注信息
	 */
	private String remark;


}