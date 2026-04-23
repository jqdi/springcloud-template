CREATE TABLE `sms_type_template_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(16) NOT NULL COMMENT '业务类型(verifycode:验证码,market:营销活动,tips:提示信息)',
  `channel` varchar(16) NOT NULL COMMENT '发送渠道(log:打印日志（测试）,ali:阿里云,tencent:腾讯云)',
  `template_code` varchar(32) NOT NULL COMMENT '模板编码',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_type_channel` (`type`,`channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信业务-模板配置';

-- 测试数据
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('1', 'verifycode', 'ali', 'SMS_213693660', NULL, '2023-05-28 14:55:17', '2023-05-28 14:55:17');
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('2', 'verifycode', 'tencent', 'SMS_213693662', NULL, '2023-05-28 14:55:29', '2023-05-28 14:55:29');
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('3', 'verifycode', 'log', 'LOG_213693660', NULL, '2023-05-28 14:55:58', '2023-06-23 16:25:54');
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('4', 'market', 'ali', 'SMS_213693661', NULL, '2023-05-28 14:55:17', '2023-05-28 15:39:21');
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('5', 'market', 'tencent', 'SMS_213693663', NULL, '2023-05-28 14:55:29', '2023-05-28 15:39:24');
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('6', 'market', 'log', 'LOG_213222122', NULL, '2023-05-28 14:55:58', '2023-06-23 16:25:46');
-- INSERT INTO `sms_type_template_config` (`id`, `type`, `channel`, `template_code`, `remark`, `create_time`, `update_time`) VALUES ('8', 'verifycode', 'qiniu', 'SMS_213693132', '', '2023-05-28 14:55:58', '2023-06-23 14:46:05');
--

CREATE TABLE `sms_template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `channel` varchar(16) NOT NULL COMMENT '发送渠道(log:打印日志（测试）,ali:阿里云,tencent:腾讯云)',
  `template_code` varchar(32) NOT NULL COMMENT '模板编码',
  `template_content` varchar(255) DEFAULT NULL COMMENT '模板内容',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_channel_templatecode` (`channel`,`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信模板';

-- 测试数据
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('1', 'ali', 'SMS_213693660', '您的验证码${code}，该验证码5分钟内有效，请勿泄漏于他人！', '验证码短信', '2023-05-13 19:28:04', '2023-05-28 15:25:53');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('2', 'ali', 'SMS_213693661', '您有新的订单待处理，当前状态：${status}，订单摘要：${content}，请及时处理。', '通知短信', '2023-05-13 19:28:04', '2023-05-28 15:26:38');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('3', 'tencent', 'SMS_213693662', '您的验证码是${code}', '验证码短信', '2023-05-13 19:28:04', '2023-05-28 15:26:46');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('5', 'log', 'LOG_213693660', '您的验证码${code}，该验证码5分钟内有效，请勿泄漏于他人！', '验证码短信', '2023-05-13 19:28:04', '2023-06-27 00:31:37');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('7', 'qiniu', 'SMS_213693132', '您的验证码是 ${code} （5分钟内有效）', '验证码短信', '2023-05-13 19:28:04', '2023-06-23 14:44:21');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('9', 'qiniu', 'SMS_213691555', '没赶上618？七牛云年中大促，狂欢到月底！存储秒杀低至0.001元/G，云主机0元抢，另有多款产品5折起，每日限量购，手慢无！https://dwz.cn/VjNhsExH', '营销短信', '2023-05-13 19:28:04', '2023-06-23 14:45:19');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('10', 'baidu', 'sms-tmpl-wHoJXL09355', '您的验证码是${code},将在${minutes}分钟后失效。', '验证码短信', '2023-05-13 19:28:04', '2023-06-23 14:48:43');
-- INSERT INTO `sms_template` (`id`, `channel`, `template_code`, `template_content`, `remark`, `create_time`, `update_time`) VALUES ('15', 'log', 'LOG_213222122', 'XXX活动开始啦，快来参加版！退订回T', '营销短信', '2023-05-13 19:28:04', '2023-06-23 16:26:26');
--

CREATE TABLE `sms_task` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(16) NOT NULL COMMENT '业务类型(verifycode:验证码,market:营销活动,tips:提示信息)',
  `plan_send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计划发送时间',
  `over_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '超时取消发送时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信发送任务';

CREATE TABLE `sms_task_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_id` int(11) unsigned NOT NULL COMMENT 'sms_task.id',
  `mobile` varchar(11) NOT NULL COMMENT '手机号码',
  `template_param_json` varchar(255) DEFAULT NULL COMMENT '模板参数json',
  `plan_send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计划发送时间',
  `actual_send_time` datetime DEFAULT NULL COMMENT '实际发送时间',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态(1:待发送,2:待定时发送,11:发送MQ成功,12:MQ消费成功,21:请求成功,22:请求失败,31:取消发送)',
  `content` varchar(255) DEFAULT NULL COMMENT '短信内容',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_taskid` (`task_id`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_plansendtime_status` (plan_send_time,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信发送任务明细';


CREATE TABLE `sms_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `channel` varchar(16) NOT NULL COMMENT '发送渠道(log:打印日志（测试）,ali:阿里云,tencent:腾讯云)',
  `mobile` varchar(11) NOT NULL COMMENT '手机号码',
  `sign_name` varchar(12) NOT NULL COMMENT '短信签名(2~12个字符)，如：【XX公司】，【】不用填写',
  `template_code` varchar(32) NOT NULL COMMENT '模板编码',
  `template_param_json` varchar(255) DEFAULT NULL COMMENT '模板参数json',
  `content` varchar(255) DEFAULT NULL COMMENT '短信内容',
  `result` varchar(8) NOT NULL COMMENT '结果(success:成功,fail:失败)',
  `message` varchar(255) DEFAULT NULL COMMENT '信息',
  `request_id` varchar(64) DEFAULT NULL COMMENT '请求ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信发送记录';
