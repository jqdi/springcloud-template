CREATE TABLE `verify_code` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(16) NOT NULL COMMENT '业务(register:注册,login:登录,changepwd:修改密码)',
  `certificate` varchar(32) NOT NULL COMMENT '凭证(手机号|UUID)',
  `code` varchar(8) NOT NULL COMMENT '验证码',
  `valid_time` datetime NOT NULL COMMENT '有效截止时间',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态(1:未使用,2:已使用)',
  `max_err_count` int(11) NOT NULL DEFAULT '0' COMMENT '最大错误次数',
  `err_count` int(11) NOT NULL DEFAULT '0' COMMENT '错误次数',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_certificate` (`certificate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码';
