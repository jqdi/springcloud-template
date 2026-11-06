CREATE TABLE `wallet` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  
  `type` int(11) NOT NULL DEFAULT '0.00' COMMENT '类型(1~9表示总钱包,2位数表示子钱包)',
  `balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '余额(元)',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:正常,2:风控中,3:冻结)',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_ui_type` (`user_id`,type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';


CREATE TABLE `wallet_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `unique_code` varchar(32) NOT NULL DEFAULT '' COMMENT '唯一码(用于幂等，防止重复入出账)',
  `wallet_id` int(11) NOT NULL COMMENT 'wallet.id',

  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '金额(元)',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型(1:入账,2:出账)',
  `balance_before` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '变化前的余额(元)',
  `balance_after` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '变化后的余额(元)',
  
  `attach` varchar(255) NOT NULL DEFAULT '{}' COMMENT '附加信息',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_wi` (`wallet_id`),
  unique KEY `uniq_uc_type` (`unique_code`,type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水表';


CREATE TABLE `wallet_income_use_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `wallet_id` int(11) NOT NULL COMMENT 'wallet.id',
  `wallet_record_id` int(11) NOT NULL COMMENT 'wallet_record.id',
  
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '入账金额(元)',
  `unused_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '未使用金额(元)',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:未使用,2:部分使用,3:已使用,4:已失效)',
  `invalid_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  `bean_name` varchar(32) NOT NULL DEFAULT '' COMMENT '入账处理bean名称',
	
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `uni_wi` (`wallet_id`),
  UNIQUE KEY `uni_wri` (`wallet_record_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包入账使用记录表';


CREATE TABLE `wallet_preincome` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `unique_code` varchar(32) NOT NULL DEFAULT '' COMMENT '唯一码(用于幂等，防止重复入出账)',
  `wallet_id` int(11) NOT NULL COMMENT 'wallet.id',
  
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '入账金额(元)',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:未入账,2:已入账)',
  `bean_name` varchar(32) NOT NULL DEFAULT '' COMMENT '入账处理bean名称',
  
  `attach` varchar(255) NOT NULL DEFAULT '{}' COMMENT '附加信息',
  
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `uni_wi` (`wallet_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包待入账表';


CREATE TABLE `wallet_freeze` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `unique_code` varchar(32) NOT NULL DEFAULT '' COMMENT '唯一码(用于幂等，防止重复入出账)',
  `order_code` varchar(64) NOT NULL DEFAULT '' COMMENT '订单号(用于归还)',
  `wallet_id` int(11) NOT NULL COMMENT 'wallet.id',
  `freeze_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额(元)',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:冻结中,2:已使用,3:已归还)',

  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_uc` (`unique_code`),
  KEY `idx_oc` (`order_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包余额冻结表';


CREATE TABLE `wallet_use_seq` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `unique_code` varchar(32) NOT NULL DEFAULT '' COMMENT '唯一码(用于幂等，防止重复入账)',
  
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  `type` int(11) NOT NULL DEFAULT '0.00' COMMENT '类型(1~9表示总钱包,2位数表示子钱包)',
  
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '金额(元)',
  `left_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '剩余金额(元)',

  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_uc` (`unique_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包使用顺序表';


CREATE TABLE `recharge_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  `order_code` varchar(32) NOT NULL DEFAULT '' COMMENT '订单号',
  `recharge_code` varchar(32) NOT NULL DEFAULT '' COMMENT '充值编码',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '充值金额',
  `gift_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '赠送金额',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_oc` (`order_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值表';
