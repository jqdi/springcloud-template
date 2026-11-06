DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  `order_code` varchar(32) NOT NULL DEFAULT '' COMMENT '订单编号',
  `order_type` varchar(16) NOT NULL COMMENT '订单类型(distribute:配送类,writeoff:核销码,groupmeal:外卖,groupmealhelp:外卖助力)',
  `status` tinyint(1) NOT NULL COMMENT '状态(1:待支付,2:已取消(END)3:已支付[待发货],4:待收货,5.已完成(END),6:退款(END))',
  `sub_status` tinyint(1) NOT NULL COMMENT '子状态(11:待支付,21:已取消(END),30:已付款,31:待发货,32:发货中,33:发货失败,41:已发货,51:待评价,52:已结束(END),61:退款中,62:退款成功(END),63:退款失败)',
  `product_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '商品总额',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单总额(元，商品金额+各种费用总和)',
  `reduce_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '抵扣总额(元，各种优惠、扣减总和)',
  `need_pay_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '需付金额(元)',
  `pay_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额(元)',
  `pay_time` datetime DEFAULT NULL COMMENT '支付/关闭时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额(多次求和)',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间(最后1次)',
  `user_del` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户删除(1:未删除,2:已删除)',
  `sub_order_url` varchar(128) NOT NULL DEFAULT '' COMMENT '子订单查询地址',
  `attach` varchar(255) DEFAULT null COMMENT '附加参数',

  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_ordercode` (`order_code`),
  KEY `idx_ui_substatus` (`user_id`,`sub_status`),
  KEY `idx_ui_status` (`user_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

DROP TABLE IF EXISTS `order_product`;
CREATE TABLE `order_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_code` varchar(32) NOT NULL DEFAULT '' COMMENT '订单编号',
  `number` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `origin_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '原价',
  `sales_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '售价',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总额(售价*数量)',
  `product_code` varchar(32) NOT NULL DEFAULT '' COMMENT '商品编码',
  `product_name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名称',
  `product_image` varchar(255) NOT NULL DEFAULT '' COMMENT '商品小图',
  `attach` varchar(255) DEFAULT null COMMENT '附加参数',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_ordercode` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';