package com.company.order.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 订单表
 */
@Data
@TableName(value ="`order`")
public class Order {
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
     * 订单类型(buy_member:开通会员,distribute:配送类,writeoff:核销码)
     */
    @TableField(value = "order_type")
    private String orderType;

    /**
     * 状态(1:待支付,2:已取消(END)3:已支付[待发货],4:待收货,5.已完成(END),6:退款(END))
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 子状态(11:待支付,21:已取消(END),31:待发货,32:发货中,33:发货失败,41:已发货,51:待评价,52:已结束(END),61:待审核,61:退款中,62:退款成功(END),63:退款失败,64:部分退款成功(END))
     */
    @TableField(value = "sub_status")
    private Integer subStatus;

    /**
     * 商品总额
     */
    @TableField(value = "product_amount")
    private BigDecimal productAmount;

    /**
     * 订单总额(元，商品金额+各种费用总和)
     */
    @TableField(value = "order_amount")
    private BigDecimal orderAmount;
    
    /**
     * 抵扣总额(元，各种优惠、扣减总和)
     */
    @TableField(value = "reduce_amount")
    private BigDecimal reduceAmount;
    
    /**
     * 需付金额(元)
     */
    @TableField(value = "need_pay_amount")
    private BigDecimal needPayAmount;

    /**
     * 实付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    private LocalDateTime payTime;
    
	/**
	 * 完成时间
	 */
	@TableField(value = "finish_time")
	private LocalDateTime finishTime;

    /**
     * 退款金额(多次求和)
     */
    @TableField(value = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退款时间(最后1次)
     */
    @TableField(value = "refund_time")
    private LocalDateTime refundTime;
    
    /**
     * 用户删除(1:未删除,2:已删除)
     */
    @TableField(value = "user_del")
    private Integer userDel;
    
    /**
     * 子订单查询地址
     */
    @TableField(value = "sub_order_url")
    private String subOrderUrl;
	
	/**
	 * 附加参数
	 */
	@TableField("attach")
	private String attach;

    /**
     * 备注(多个使用/分隔)
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}