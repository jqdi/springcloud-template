package com.company.order.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("ali_activity_pay")
@Data
@Accessors(chain = true)
public class AliActivityPay extends AuditableModel<AliActivityPay> {
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
	 * 商户订单号，需保持唯一性
	 */
	private String outOrderNo;
	
	/**
	 * 购买者的支付宝uid
	 */
	private String buyerId;
	
	/**
	 * 订单金额(元)
	 */
	private BigDecimal totalAmount;
	
	/**
	 * 售卖活动信息列表。
	 * 
	 * 目前只支持 sale_activity_info_list
	 * 
	 * 中传入单个类型的活动信息。
	 */
	private String saleActivityInfoList;

	/**
	 * 支付体（前端使用）
	 */
	private String payBody;

	/**
	 * 交易结果(TRADE_CLOSED:交易关闭,TRADE_FINISHED:交易完结,TRADE_SUCCESS:支付成功,WAIT_BUYER_PAY:交易创建)
	 */
	private String tradeStatus;
	
	/**
	 * 支付宝交易凭证号
	 */
	private String tradeNo;
	
	/**
	 * 购买商家兑换券的营销订单号
	 */
	private String orderNo;
	
	/**
	 * 交易支付时间
	 */
	private String gmtPayment;
	
	/**
	 * 备注信息
	 */
	private String remark;


}