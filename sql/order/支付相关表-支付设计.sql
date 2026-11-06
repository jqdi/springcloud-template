
-- ----------------------------
-- Table structure for order_pay
-- ----------------------------
DROP TABLE IF EXISTS `order_pay`;
CREATE TABLE `order_pay` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_code` varchar(32) NOT NULL COMMENT '订单编号',
  `business_type` varchar(16) NOT NULL COMMENT '业务类型(nomal:普通下单,kill:秒杀下单,member:购买会员)',
  `method` varchar(8) NOT NULL COMMENT '支付方式(ali:支付宝,wx:微信,ios:苹果,quick:云闪付)',
  `amount` decimal(12,2) NOT NULL COMMENT '金额(元)',
  `body` varchar(127) NOT NULL COMMENT '商品描述',
  `product_id` varchar(127) DEFAULT NULL COMMENT '商品Id',
  `status` varchar(8) NOT NULL COMMENT '状态(waitpay:待支付,closed:已关闭,payed:已支付)',
  `notify_url` varchar(128) DEFAULT NULL COMMENT '通知地址(关闭/支付订单都会通知)',
  `notify_attach` varchar(256) DEFAULT NULL COMMENT '通知地址的附加数据',
  `pay_time` datetime DEFAULT NULL COMMENT '关闭/支付时间',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_order_code` (`order_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

-- ----------------------------
-- Table structure for order_pay_refund
-- ----------------------------
DROP TABLE IF EXISTS `order_pay_refund`;
CREATE TABLE `order_pay_refund` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `refund_order_code` varchar(32) NOT NULL COMMENT '退款订单编号',
  `order_code` varchar(32) NOT NULL COMMENT '订单编号',
  `business_type` varchar(16) NOT NULL COMMENT '业务类型(user:用户申请,sys_auto:系统自动退款,amdin:管理后台申请)',
  `method` varchar(8) NOT NULL COMMENT '支付方式(ali:支付宝,wx:微信,ios:苹果,quick:云闪付)',
  `amount` decimal(12,2) NOT NULL COMMENT '金额(元)',
  `refund_amount` decimal(12,2) NOT NULL COMMENT '退款金额(元)',
  `status` varchar(16) NOT NULL COMMENT '状态(wait_apply:待申请,apply_success:申请成功,apply_fail:申请失败,refund_success:退款成功,refund_fail:退款失败)',
  `notify_url` varchar(128) DEFAULT NULL COMMENT '通知地址(关闭/支付订单都会通知)',
  `notify_attach` varchar(256) DEFAULT NULL COMMENT '通知地址的附加数据',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_refundordercode` (`refund_order_code`) USING BTREE,
  KEY `idx_ordercode` (`order_code`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单支付退款表';

-- ----------------------------
-- Table structure for pay_notify
-- ----------------------------
DROP TABLE IF EXISTS `pay_notify`;
CREATE TABLE `pay_notify` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `method` varchar(8) NOT NULL COMMENT '支付方式(ali:支付宝,wx:微信,ios:苹果,quick:云闪付)',
  `notify_data` varchar(2048) DEFAULT NULL COMMENT '通知数据',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付通知表';

-- ----------------------------
-- Table structure for financial_flow
-- ----------------------------
DROP TABLE IF EXISTS `financial_flow`;
CREATE TABLE `financial_flow` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_code` varchar(32) NOT NULL COMMENT '订单编号',
  `trade_no` varchar(64) NOT NULL COMMENT '渠道订单号',
  `business_source` varchar(64) NOT NULL COMMENT '业务来源',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '进出账金额(元)',
  `trade_way` varchar(32) NOT NULL COMMENT '交易方式(wx_pay:微信支付,ali_pay:支付宝支付,mch_pay:微信企业打款)',
  `merchant_no` varchar(32) NOT NULL COMMENT '商户号',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_order_code` (`order_code`),
  KEY `idx_trade_way` (`trade_way`),
  KEY `idx_business_source` (`business_source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务流水表';

-- ----------------------------
-- Table structure for pay_refund_apply
-- ----------------------------
DROP TABLE IF EXISTS `pay_refund_apply`;
CREATE TABLE `pay_refund_apply` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_code` varchar(32) NOT NULL COMMENT '退款订单号',
  `old_order_code` varchar(32) NOT NULL COMMENT '原订单号',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额(元)',
  `business_type` varchar(16) NOT NULL COMMENT '业务类型(user:用户申请,sys_auto:系统自动退款,amdin:管理后台申请)',
  `verify_status` int(11) NOT NULL DEFAULT '0' COMMENT '审核状态(1:待审核,21:通过,22:驳回)',
  `refund_status` int(11) NOT NULL DEFAULT '0' COMMENT '退款状态(1:未退款,21:退款驳回(END),31:处理中,41:申请成功,42:申请失败(END),51:退款成功(END),52:退款失败(END))',
  `reason` varchar(64) NOT NULL COMMENT '退款原因',
  `attach` varchar(256) NOT NULL COMMENT '通知地址的附加数据',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_ordercode` (`order_code`),
  KEY `idx_rs_vs` (`refund_status`,`verify_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- ----------------------------
-- Table structure for inner_callback
-- ----------------------------
DROP TABLE IF EXISTS `inner_callback`;
CREATE TABLE `inner_callback` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `url` varchar(128) NOT NULL COMMENT '回调地址',
  `json_params` varchar(2048) DEFAULT NULL COMMENT 'JSON参数',
  `json_result` varchar(128) DEFAULT NULL COMMENT 'JSON结果',
  `processor_bean_name` varchar(384) DEFAULT NULL COMMENT '处理器bean名称',
  `status` tinyint(4) NOT NULL COMMENT '状态(1:待回调,2:回调成功(END),21.回调失败,22.放弃回调(END))',
  `seconds_strategy` varchar(32) NOT NULL DEFAULT 'increment' COMMENT '递增秒数策略(increment:增量,fix:固定)',
  `increase_seconds` int(11) NOT NULL DEFAULT '2' COMMENT '递增秒数',
  `next_dispose_time` datetime NOT NULL COMMENT '下次处理时间',
  `max_failure` int(11) NOT NULL COMMENT '最大允许失败次数',
  `failure` int(11) NOT NULL DEFAULT '0' COMMENT '当前失败次数',
  `trace_id` varchar(32) DEFAULT NULL COMMENT '追踪ID',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_status_ndt` (`status`,`next_dispose_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='内部回调表';
