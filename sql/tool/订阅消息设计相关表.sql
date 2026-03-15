CREATE TABLE `subscribe_template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pri_tmpl_id` varchar(64) NOT NULL DEFAULT '' COMMENT '添加至账号下的模板 id，发送小程序订阅消息时所需',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '模版标题',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '模板内容',
  `example` varchar(255) NOT NULL DEFAULT '' COMMENT '模板内容示例',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '模版类型，2 为一次性订阅，3 为长期订阅',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_pritmplid` (`pri_tmpl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订阅消息模板';

INSERT INTO `subscribe_template`(`id`, `pri_tmpl_id`, `title`, `content`, `example`, `type`, `remark`, `create_time`, `update_time`) VALUES (1, '11111111111111111111mr5QnaV1pBwIwYK1111', '优惠券使用提醒', '优惠券名称:{{thing1.DATA}}\r\n优惠券金额:{{amount5.DATA}}\r\n数量:{{number7.DATA}}\r\n有效期:{{time8.DATA}}\r\n温馨提示:{{thing3.DATA}}\r\n', '优惠券名称:预定用户专享券\r\n优惠券金额:200元\r\n数量:2\r\n有效期:2021-04-22 12：00：00\r\n温馨提示:支付尾款时可直接抵扣，请尽快使用哦\r\n', 2, NULL, '2023-09-21 15:18:03', '2023-09-21 15:18:03');
INSERT INTO `subscribe_template`(`id`, `pri_tmpl_id`, `title`, `content`, `example`, `type`, `remark`, `create_time`, `update_time`) VALUES (2, '11111mr5QnaV1pBwIwYK1111111111111111111', '活动进度通知', '活动进度:{{thing1.DATA}}\r\n活动名称:{{thing2.DATA}}\r\n温馨提示:{{thing3.DATA}}\r\n', '活动进度:恭喜您获得10元红包\r\n活动名称:美食0元抢\r\n温馨提示:点击查看我的活动奖励>>\r\n', 2, NULL, '2023-09-21 15:18:04', '2023-09-21 15:18:04');
INSERT INTO `subscribe_template`(`id`, `pri_tmpl_id`, `title`, `content`, `example`, `type`, `remark`, `create_time`, `update_time`) VALUES (3, 'mr5QnaV1pBwIwYK-dJeSB4_bCQiCnA22223k63A', '活动开始通知', '活动名称:{{thing1.DATA}}\r\n备注:{{thing2.DATA}}\r\n', '活动名称:限时领红包\r\n备注:您订阅的限时活动已开始，快来参与吧\r\n', 2, NULL, '2023-09-21 15:18:04', '2023-09-21 15:18:04');
INSERT INTO `subscribe_template`(`id`, `pri_tmpl_id`, `title`, `content`, `example`, `type`, `remark`, `create_time`, `update_time`) VALUES (4, '4OJ7GO3N0On7EFxDVa1r9c0py33333mcBq1G2s1M-x8', '优惠券使用提醒', '优惠券名称:{{thing1.DATA}}\r\n优惠券金额:{{amount5.DATA}}\r\n温馨提示:{{thing3.DATA}}\r\n有效期:{{time8.DATA}}\r\n', '优惠券名称:预定用户专享券\r\n优惠券金额:200元\r\n温馨提示:支付尾款时可直接抵扣，请尽快使用哦\r\n有效期:2021-04-22 12：00：00\r\n', 2, NULL, '2023-09-21 15:18:04', '2023-09-21 15:18:04');
INSERT INTO `subscribe_template`(`id`, `pri_tmpl_id`, `title`, `content`, `example`, `type`, `remark`, `create_time`, `update_time`) VALUES (5, 'ypn44447bfNCwzcn_973utAl5Ztv-hM52Lfk', '拼团结果通知', '拼团结果:{{phrase1.DATA}}\r\n订单编号:{{character_string2.DATA}}\r\n活动名称:{{thing3.DATA}}\r\n备注:{{thing5.DATA}}\r\n', '拼团结果:拼团成功\r\n订单编号:aaaa132165432131325\r\n活动名称:拼团活动（3人成团）\r\n备注:拼团成功，订单已享受0.5元优惠\r\n', 2, NULL, '2023-09-21 15:18:04', '2023-09-21 15:18:04');


CREATE TABLE `subscribe_type_template_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '业务类型',
  `param_index` varchar(32) NOT NULL DEFAULT '' COMMENT '参数索引（逗号分隔，空代表按顺序）',
  `template_code` varchar(64) NOT NULL DEFAULT '' COMMENT '模板编码(subscribe_template.pri_tmpl_id)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订阅消息业务-模板配置';

INSERT INTO `subscribe_type_template_config` (`id`, `type`, `param_index`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('1', 'coupon_use', '', '4OJ7GO3N0On333338L2aa5TaQKOsxqKgYnj4', NULL, '2023-09-21 16:44:34', '2023-09-21 16:44:34');
INSERT INTO `subscribe_type_template_config` (`id`, `type`, `param_index`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('2', 'active_begin', '', 'mr5QnaV1pBwIwYK-dJeSB4_bCQiCnA22223k63A', NULL, '2023-10-15 15:59:13', '2023-10-15 15:59:37');
INSERT INTO `subscribe_type_template_config` (`id`, `type`, `param_index`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('3', 'group_result', '', 'ypn44447bfNCwzcn_973utAl5Ztv-hM52Lfk', NULL, '2023-10-15 15:59:45', '2023-10-15 15:59:54');
INSERT INTO `subscribe_type_template_config` (`id`, `type`, `param_index`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('4', 'avtive_process', '0,2,3', '11111mr5QnaV1pBwIwYK1111111111111111111', NULL, '2023-10-15 16:00:54', '2023-10-15 16:01:11');


CREATE TABLE `subscribe_task` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(32) NOT NULL COMMENT '业务类型',
  `plan_send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计划发送时间',
  `over_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '超时取消发送时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订阅消息发送任务';

CREATE TABLE `subscribe_task_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_id` int(11) unsigned NOT NULL COMMENT 'subscribe_task.id',
  `openid` varchar(32) NOT NULL COMMENT 'openid',
  `page` varchar(32) NOT NULL COMMENT '跳转页面',
  `template_param_json` varchar(255) DEFAULT NULL COMMENT '模板参数json',
  `plan_send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计划发送时间',
  `actual_send_time` datetime DEFAULT NULL COMMENT '实际发送时间',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态(1:待发送,2:待定时发送,11:发送MQ成功,12:MQ消费成功,21:请求成功,22:请求失败,31:取消发送)',
  `template_code` varchar(64) NOT NULL DEFAULT '' COMMENT '模板编码(subscribe_template.pri_tmpl_id)',
  `content` varchar(255) DEFAULT NULL COMMENT '订阅消息内容',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_taskid` (`task_id`),
  KEY `idx_openid` (`openid`),
  KEY `idx_plansendtime_status` (`plan_send_time`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订阅消息发送任务明细';

CREATE TABLE `subscribe_template_grant` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `openid` varchar(32) NOT NULL DEFAULT '' COMMENT 'openid',
  `template_code` varchar(64) NOT NULL COMMENT '模板编码(subscribe_template.pri_tmpl_id)',
  `total_num` int(11) NOT NULL DEFAULT '0' COMMENT '授权总次数',
  `use_num` int(11) NOT NULL DEFAULT '0' COMMENT '已使用次数',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_openid_templatecode` (`openid`,`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订阅消息-授权记录';

CREATE TABLE `subscribe_group_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group` varchar(32) NOT NULL DEFAULT '' COMMENT '订阅组',
  `types` varchar(128) NOT NULL COMMENT '业务类型(多个英文逗号分隔,最多3个)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_group` (`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订阅消息-订阅组';

INSERT INTO `subscribe_group_type` (`id`, `group`, `types`, `remark`, `create_time`, `update_time`) VALUES ('1', 'order_finish', 'coupon_use,active_begin,group_result', NULL, '2023-10-15 15:58:43', '2023-10-15 16:00:08');
