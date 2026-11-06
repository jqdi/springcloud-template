DROP TABLE IF EXISTS `ali_activity_pay`;
CREATE TABLE `ali_activity_pay` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` varchar(32) NOT NULL DEFAULT '' COMMENT '应用ID',
  `out_order_no` varchar(32) NOT NULL DEFAULT '' COMMENT '商户订单号',
  `buyer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '购买者的支付宝uid',
  `total_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额(元)',
  `sale_activity_info_list` varchar(255) NOT NULL DEFAULT '' COMMENT '售卖活动信息列表',
  `pay_body` varchar(2049) NOT NULL DEFAULT '' COMMENT '支付体（前端使用）',
  `trade_status` varchar(20) NOT NULL DEFAULT '' COMMENT '交易结果(TRADE_CLOSED:交易关闭,TRADE_FINISHED:交易完结,TRADE_SUCCESS:支付成功,WAIT_BUYER_PAY:交易创建)',
  
  `trade_no` varchar(64) NOT NULL DEFAULT '' COMMENT '支付宝交易凭证号',
  `order_no` varchar(64) NOT NULL DEFAULT '' COMMENT '购买商家兑换券的营销订单号',
  `gmt_payment` varchar(32) NOT NULL DEFAULT '' COMMENT '交易支付时间',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_otn` (`out_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付宝活动支付表';

DROP TABLE IF EXISTS `ali_activity_pay_refund`;
CREATE TABLE `ali_activity_pay_refund` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` varchar(32) NOT NULL COMMENT '应用ID',
  `out_order_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `out_biz_no` varchar(64) NOT NULL DEFAULT '' COMMENT '商户退款单号',
  `buyer_id` varchar(32) NOT NULL COMMENT '购买者的支付宝uid',
  `refund_activity_info_list` varchar(255) NOT NULL DEFAULT '' COMMENT '退款活动信息列表',
  
  `refund_status` varchar(20) NOT NULL DEFAULT '' COMMENT '退款状态(REFUND_SUCCESS:退款成功)',
  `order_no` varchar(64) NOT NULL DEFAULT '' COMMENT '购买商家兑换券的营销订单号',
  `refund_type` varchar(32) NOT NULL DEFAULT '' COMMENT '退款场景',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_oon` (`out_order_no`) USING BTREE,
  KEY `idx_obn` (`out_biz_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='支付宝活动支付退款表';


DROP TABLE IF EXISTS `ali_activity_notify`;
CREATE TABLE `ali_activity_notify` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `method` varchar(16) NOT NULL DEFAULT '' COMMENT '消息(spiordersend:订单券发放,from:FROM消息)',
  `notify_data` varchar(2048) NOT NULL DEFAULT '' COMMENT '通知数据',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付宝活动通知表';

DROP TABLE IF EXISTS `ali_activity_coupon`;
CREATE TABLE `ali_activity_coupon` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_id` varchar(128) NOT NULL COMMENT '活动id',
  `event_id` varchar(128) NOT NULL DEFAULT '' COMMENT '事件id。可用于业务去重。',
  `event_time` varchar(32) NOT NULL DEFAULT '' COMMENT '事件创建时间',
  `voucher_id` varchar(64) NOT NULL COMMENT '支付宝券ID',
  `voucher_code` varchar(64) NOT NULL COMMENT '用户领取的券码code,支付宝商家券活动才会返回券码，其他优惠券活动该值为空',
  `receive_user_id` varchar(64) NOT NULL COMMENT '领券的支付宝user_id账号',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_eventid` (`event_id`),
  KEY `idx_ai_rui` (`activity_id`,`receive_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付宝活动券码表';
