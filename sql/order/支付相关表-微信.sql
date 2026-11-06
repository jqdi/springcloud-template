-- ----------------------------
-- Table structure for wx_pay
-- ----------------------------
DROP TABLE IF EXISTS `wx_pay`;
CREATE TABLE `wx_pay` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` varchar(32) NOT NULL COMMENT '小程序ID|公众账号ID|应用ID',
  `mchid` varchar(32) NOT NULL COMMENT '商户号',
  `nonce_str` varchar(32) NOT NULL COMMENT '随机字符串',
  `sign` varchar(64) DEFAULT NULL COMMENT '签名',
  `body` varchar(128) NOT NULL COMMENT '商品描述',
  `out_trade_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `total_fee` int(11) NOT NULL COMMENT '订单金额(分)',
  `spbill_create_ip` varchar(64) NOT NULL COMMENT '终端IP',
  `notify_url` varchar(256) NOT NULL COMMENT '通知地址',
  `trade_type` varchar(16) NOT NULL COMMENT '小程序:JSAPI,H5:MWEB,APP:APP',
  `product_id` varchar(32) DEFAULT NULL COMMENT '商品ID(trade_type=NATIVE，此参数必传)',
  `openid` varchar(128) DEFAULT NULL COMMENT '用户标识(trade_type=JSAPI,此参数必传)',
  
  `prepay_id` varchar(64) DEFAULT NULL COMMENT '预支付交易会话标识',
  `mweb_url` varchar(255) DEFAULT NULL COMMENT '支付跳转链接',
  `code_url` varchar(255) DEFAULT NULL COMMENT '二维码链接',
  
  `trade_state` varchar(16) DEFAULT NULL COMMENT '交易状态(SUCCESS:支付成功,REFUND:转入退款,NOTPAY:未支付,CLOSED:已关闭,REVOKED:已撤销(刷卡支付),USERPAYING:用户支付中,PAYERROR:支付失败(其他原因，如银行返回失败),ACCEPT:已接收，等待扣款)',
  `transaction_id` varchar(32) DEFAULT NULL COMMENT '微信支付订单号',
  `time_end` varchar(16) DEFAULT NULL COMMENT '支付完成时间(yyyyMMddHHmmss)',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_otn` (`out_trade_no`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='微信支付表';

-- ----------------------------
-- Table structure for wx_pay_refund
-- ----------------------------
DROP TABLE IF EXISTS `wx_pay_refund`;
CREATE TABLE `wx_pay_refund` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` varchar(32) NOT NULL COMMENT '小程序ID|公众账号ID|应用ID',
  `mchid` varchar(32) NOT NULL COMMENT '商户号',
  `nonce_str` varchar(32) NOT NULL COMMENT '随机字符串',
  `sign` varchar(64) DEFAULT NULL COMMENT '签名',
  `out_trade_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `out_refund_no` varchar(32) NOT NULL COMMENT '商户退款单号',
  `total_fee` int(11) NOT NULL COMMENT '订单金额(分)',
  `refund_fee` int(11) NOT NULL COMMENT '退款金额(分)',
  
  `refund_id` varchar(32) DEFAULT NULL COMMENT '微信退款单号',
  `cash_fee` int(11) DEFAULT NULL COMMENT '现金支付金额(分)',
  
  `refund_status` varchar(16) DEFAULT NULL COMMENT '退款状态(PROCESSING/SUCCESS/CHANGE/REFUNDCLOSE)',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_orn` (`out_refund_no`) USING HASH,
  KEY `idx_otn` (`out_trade_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='微信支付退款表';
