package com.company.user.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 充值订单表
 */
@Data
@TableName(value ="recharge_order")
public class RechargeOrder extends AuditableModel<RechargeOrder> {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * user_info.id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 订单编号
     */
    @TableField(value = "order_code")
    private String orderCode;
    
    /**
     * 充值编码
     */
    @TableField(value = "recharge_code")
    private String rechargeCode;
    
    /**
     * 充值金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;
    
    /**
     * 赠送金额
     */
    @TableField(value = "gift_amount")
    private BigDecimal giftAmount;

    /**
     * 备注(多个使用/分隔)
     */
    @TableField(value = "remark")
    private String remark;


}