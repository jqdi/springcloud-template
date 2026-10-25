package com.company.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("ali_activity_pay_refund")
@Data
@Accessors(chain = true)
public class AliActivityPayRefund extends AuditableModel<AliActivityPayRefund> {
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
	 * 商户订单号
	 */
	private String outOrderNo;

	/**
	 * 外部业务单号，用作幂等控制。
	 * 
	 * 注：针对同一次退款请求，如果调用接口失败或异常了，重试时需要保证退款请求号不能变更，防止该笔交易重复退款。
	 * 支付宝会保证同样的退款请求号多次请求只会退一次。
	 * 
	 * 该外部业务单号会在后续alipay.marketing.activity.message.refund 退款消息中带上。
	 */
	private String outBizNo;
	
	/**
	 * 购买者的支付宝uid
	 */
	private String buyerId;
	
	/**
	 * 退款活动信息列表
	 * (activity_id,quantity,voucher_code_list)
	 */
	private String refundActivityInfoList;
	
	/**
	 * 退款状态(REFUND_SUCCESS:退款成功)
	 */
	private String refundStatus;

	/**
	 * 购买商家兑换券的营销订单号
	 */
	private String orderNo;
	
	/**
	 * <pre>
	 * 退款场景。 
	 * USER_REFUND:用户主动发起退款的场景。通过退款接口调用成功的，均认为是用户主动发起退款。
	 * AUTO_EXPIRE:平台过期自动退款的场景。
	 * SEND_ERROR_REFUND:由于多次调用服务商实现的spi接口：spi.alipay.marketing.activity.order.send失败，平台发起自动退款。
	 * ALIPAY_EMERGENCY_REFUND:支付宝应急退款。（该场景一定是与服务商协商后才会执行）
	 * </pre>
	 */
	private String refundType;
	
	/**
	 * 备注信息
	 */
	private String remark;


}