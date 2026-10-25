package com.company.user.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;

/**
 * 购买会员订单表
 */
@Data
@TableName(value ="member_buy_order")
public class MemberBuyOrder extends AuditableModel<MemberBuyOrder> {
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
     * user_coupon.id
     */
    @TableField(value = "user_coupon_id")
    private Integer userCouponId;

    /**
     * 会员续期编码
     */
    @TableField(value = "product_code")
    private String productCode;
    
    /**
     * 会员续期费用
     */
    @TableField(value = "amount")
    private BigDecimal amount;
    
    /**
     * 续期增加天数
     */
    @TableField(value = "add_days")
    private Integer addDays;
    
    /**
     * 退款手续费
     */
    @TableField(value = "refund_service_amount")
    private BigDecimal refundServiceAmount;

    /**
     * 备注(多个使用/分隔)
     */
    @TableField(value = "remark")
    private String remark;


}
