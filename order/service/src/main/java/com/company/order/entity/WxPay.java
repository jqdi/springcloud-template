package com.company.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("wx_pay")
@Data
@Accessors(chain = true)
public class WxPay extends AuditableModel<WxPay> {
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
	 * 商品描述
	 */
	private String body;
	/**
	 * 商户订单号，需保持唯一性
	 */
	private String outTradeNo;
	/**
	 * 标价金额(分)
	 */
	private Integer totalFee;
	/**
	 * 终端IP
	 */
	private String spbillCreateIp;
	/**
	 * 通知地址
	 */
	private String notifyUrl;
	/**
	 * 小程序:JSAPI,H5:MWEB,APP:APP
	 */
	private String tradeType;
	/**
	 * 商品ID(trade_type=NATIVE，此参数必传)
	 */
	private String productId;
	/**
	 * 用户标识(trade_type=JSAPI,此参数必传)
	 */
	private String openid;
	
	/**
	 * 预支付交易会话标识
	 */
	private String prepayId;
	/**
	 * 支付跳转链接
	 */
	private String mwebUrl;
	/**
	 * 二维码链接
	 */
	private String codeUrl;

	/**
	 * <pre>
	 * 交易状态(SUCCESS:支付成功,REFUND:转入退款,NOTPAY:未支付,CLOSED:已关闭,REVOKED:已撤销(刷卡支付),USERPAYING:用户支付中,PAYERROR:支付失败(其他原因，如银行返回失败),ACCEPT:已接收，等待扣款)
	 * </pre>
	 */
	private String tradeState;
	
	/**
	 * 微信支付订单号
	 */
	private String transactionId;
	/**
	 * 支付完成时间(yyyyMMddHHmmss)
	 */
	private String timeEnd;

	/**
	 * 备注信息
	 */
	private String remark;


}