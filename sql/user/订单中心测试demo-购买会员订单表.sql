DROP TABLE IF EXISTS `member_buy_order`;
CREATE TABLE `member_buy_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  `order_code` varchar(32) NOT NULL DEFAULT '' COMMENT '订单编号',
  `user_coupon_id` int(11) NOT NULL DEFAULT '0' COMMENT 'user_coupon.id',
  `product_code` varchar(32) NOT NULL DEFAULT '' COMMENT '会员续期编码',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '会员续期费用(元)',
  `add_days` int(11) NOT NULL DEFAULT '0' COMMENT '续期增加天数',
  `refund_service_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款手续费',

  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_ordercode` (`order_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购买会员订单表';
