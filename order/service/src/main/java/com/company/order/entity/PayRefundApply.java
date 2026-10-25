package com.company.order.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@TableName("pay_refund_apply")
@Data
@Accessors(chain = true)
public class PayRefundApply extends AuditableModel<PayRefundApply> {
	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 退款订单号
	 */
	private String orderCode;

	/**
	 * 原订单号
	 */
	private String oldOrderCode;
	
	/**
	 * 退款金额(元)
	 */
	private BigDecimal amount;

	/**
	 * 业务类型(user:用户申请,sys_auto:系统自动退款,amdin:管理后台申请)
	 */
	private String businessType;
	
	/**
	 * 审核状态(1:待审核,21:通过,22:驳回)
	 */
	private Integer verifyStatus;
	
	/**
	 * 退款状态(1:未退款,21:退款驳回(END),31:处理中,41:申请成功,42:申请失败(END),51:退款成功(END),52:退款失败(END))
	 */
	private Integer refundStatus;
	
	/**
	 * 退款原因
	 */
	private String reason;
	
	/**
	 * 附加数据
	 */
	private String attach;

	/**
	 * 备注信息
	 */
	private String remark;


}