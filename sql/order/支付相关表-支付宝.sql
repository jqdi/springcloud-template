
-- ----------------------------
-- Table structure for ali_pay
-- ----------------------------
DROP TABLE IF EXISTS `ali_pay`;
CREATE TABLE `ali_pay` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` varchar(32) NOT NULL COMMENT '应用ID',
  `notify_url` varchar(256) DEFAULT NULL COMMENT '通知地址',
  `out_trade_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `subject` varchar(128) NOT NULL COMMENT '商品标题',
  `total_amount` decimal(12,2) NOT NULL COMMENT '订单金额(元)',
  `pay_body` varchar(2049) DEFAULT NULL COMMENT '支付体（前端使用）',
  
  `trade_status` varchar(20) DEFAULT NULL COMMENT '交易结果(TRADE_CLOSED:交易关闭,TRADE_FINISHED:交易完结,TRADE_SUCCESS:支付成功,WAIT_BUYER_PAY:交易创建)',
  `trade_no` varchar(64) DEFAULT NULL COMMENT '支付宝交易凭证号',
  `gmt_payment` varchar(32) DEFAULT NULL COMMENT '交易支付时间(yyyy-MM-dd HH:mm:ss)',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_otn` (`out_trade_no`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='支付宝支付表';

-- ----------------------------
-- Table structure for ali_pay_refund
-- ----------------------------
DROP TABLE IF EXISTS `ali_pay_refund`;
CREATE TABLE `ali_pay_refund` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` varchar(32) NOT NULL COMMENT '应用ID',
  `out_trade_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `out_request_no` varchar(64) NOT NULL COMMENT '商户退款单号',
  `refund_amount` decimal(12,2) NOT NULL COMMENT '退款金额(元)',
  
  `trade_no` varchar(64) DEFAULT NULL COMMENT '支付宝交易凭证号',
  `refund_status` varchar(20) DEFAULT NULL COMMENT '退款状态(REFUND_SUCCESS:退款成功)',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_rn` (`out_request_no`) USING HASH,
  KEY `idx_otn` (`out_trade_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='支付宝支付退款表';

