CREATE TABLE `webhook_template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(16) NOT NULL DEFAULT '' COMMENT '业务类型(system_error:系统异常,xx_send_fail:xx发货失败)',
  `key` varchar(32) NOT NULL DEFAULT '' COMMENT 'key',
  `template_content` varchar(255) NOT NULL DEFAULT '' COMMENT '模板内容',
  `mentioned_list` varchar(255) NOT NULL DEFAULT '' COMMENT '英文逗号分，userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list',
  `mentioned_mobile_list` varchar(255) NOT NULL DEFAULT '' COMMENT '英文逗号分，手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企微机器人模板';

CREATE TABLE `webhook_task` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(16) NOT NULL DEFAULT '' COMMENT '业务类型(system_error:系统异常,xx_send_fail:xx发货失败)',
  `plan_send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计划发送时间',
  `over_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '超时取消发送时间',
  `template_param_json` varchar(255) DEFAULT NULL COMMENT '模板参数json',
  `actual_send_time` datetime DEFAULT NULL COMMENT '实际发送时间',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态(1:待发送,2:待定时发送,11:发送MQ成功,12:MQ消费成功,21:请求成功,22:请求失败,31:取消发送)',
  `key` varchar(32) NOT NULL DEFAULT '' COMMENT 'key',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '企微机器人内容',
  `mentioned_list` varchar(255) NOT NULL DEFAULT '' COMMENT '英文逗号分，userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list',
  `mentioned_mobile_list` varchar(255) NOT NULL DEFAULT '' COMMENT '英文逗号分，手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`),
  KEY `idx_key` (`key`),
  KEY `idx_plansendtime_status` (`plan_send_time`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企微机器人发送任务';
