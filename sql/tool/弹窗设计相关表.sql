DROP TABLE IF EXISTS `popup_pop_condition`;
CREATE TABLE `popup_pop_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'bean名称(PopCondition的实现类)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `has_param` char(1) NOT NULL DEFAULT 'N' COMMENT '有参数（Y是 N否）',
  `sort` int(4) NOT NULL DEFAULT '0' COMMENT '排序',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_beanname` (`bean_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='弹窗条件';

-- 测试数据
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (1, 'FrequencyCondition', '弹窗频率', '参数：count，frequency (once:只弹1次,times:count天/次,daily:count次/天)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (2, 'SenceCondition', '场景', '参数：sences(mini_home:首页,takeout_index:外卖首页,takeout_meallist:外卖商家详情页,takeout_groupshare:分享链接-进入拼团页)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (3, 'PushAreaCondition', '推送地区', '参数：cityCodes', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (4, 'PushPlatformCondition', '推送平台', '参数：platforms(0:小程序·安卓, 1:小程序·IOS, 2:APP·安卓, 3:APP·IOS)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (5, 'SourceCondition', '来源渠道', '参数：sources', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (6, 'SpecifyUserCondition', '指定用户', '参数：appUserIds', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (7, 'LoginedCondition', '已登录用户', '', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (8, 'PopTimeRangeCondition', '指定时间段', '参数：beginTime，endTime(格式HH-MM-SS)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (10, 'UnuseCouponCondition', '有未使用优惠券的用户', '', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `popup_pop_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (11, 'WeekDayCondition', '指定星期几', '参数：weekDays(1表示周日，2表示周一)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');


DROP TABLE IF EXISTS `popup`;
CREATE TABLE `popup` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `begin_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `status` varchar(8) NOT NULL DEFAULT '' COMMENT '状态(off:下架,on:上架)',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级(值越大，优先级越高)',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '标题',
  `text` varchar(128) NOT NULL DEFAULT '' COMMENT '文案',
  `bg_img` varchar(255) NOT NULL DEFAULT '' COMMENT '背景图json数据',
  `close_btn` varchar(255) NOT NULL DEFAULT '' COMMENT '关闭按钮json数据',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_begintime` (`begin_time`),
  KEY `idx_endtime` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='弹窗';

-- 测试数据
INSERT INTO `popup`(`id`, `begin_time`, `end_time`, `status`, `priority`, `title`, `text`, `bg_img`, `close_btn`, `remark`, `create_time`, `update_time`) VALUES (1, '2023-09-04 10:29:39', '2023-09-05 10:29:37', 'on', 10, '新人有礼', '欢迎注册！！！', '{\"imgUrl\":\"https://images.domain.com/aaa/bbb.jpg\",\"type\":\"redirct\",\"value\":\"/page/aaa/bbb?userId={userId}\"}', '{\"type\":\"close\",\"text\":\"X\",\"value\":\"\"}', '', '2023-09-04 10:29:39', '2023-09-04 14:10:57');
INSERT INTO `popup`(`id`, `begin_time`, `end_time`, `status`, `priority`, `title`, `text`, `bg_img`, `close_btn`, `remark`, `create_time`, `update_time`) VALUES (3, '2023-09-04 14:03:13', '2023-09-05 14:03:10', 'on', 3, '优惠券发放', '恭喜你获得1张5元优惠券', '{\"imgUrl\":\"https://images.domain.com/aaa/bbb.jpg\",\"type\":\"redirct\",\"value\":\"/page/aaa/bbb?userId={userId}\"}', '{\"type\":\"close\",\"text\":\"X\",\"value\":\"\"}', '', '2023-09-04 14:03:13', '2023-09-04 14:04:26');
INSERT INTO `popup`(`id`, `begin_time`, `end_time`, `status`, `priority`, `title`, `text`, `bg_img`, `close_btn`, `remark`, `create_time`, `update_time`) VALUES (10, '2023-11-17 16:12:23', '2023-11-25 16:12:23', 'on', 449, '新人有礼', '欢迎注册！！！', '{\"imgUrl\":\"https://images.domain.com/aaa/bbb.jpg\",\"type\":\"redirct\",\"value\":\"/page/aaa/bbb?userId={userId}\"}', '{\"type\":\"close\",\"text\":\"X\",\"value\":\"\"}', '', '2023-09-04 16:13:27', '2023-09-04 16:13:27');
INSERT INTO `popup`(`id`, `begin_time`, `end_time`, `status`, `priority`, `title`, `text`, `bg_img`, `close_btn`, `remark`, `create_time`, `update_time`) VALUES (11, '2023-12-05 16:12:23', '2024-01-02 16:12:23', 'on', 828, '新人有礼', '欢迎注册！！！', '{\"imgUrl\":\"https://images.domain.com/aaa/bbb.jpg\",\"type\":\"redirct\",\"value\":\"/page/aaa/bbb?userId={userId}\"}', '{\"type\":\"close\",\"text\":\"X\",\"value\":\"\"}', '', '2023-09-04 16:13:27', '2023-09-04 16:13:27');


DROP TABLE IF EXISTS `popup_condition`;
CREATE TABLE `popup_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `popup_id` int(11) NOT NULL COMMENT 'popup.id',
  `pop_condition` varchar(32) NOT NULL DEFAULT '' COMMENT '弹窗条件(bean名称,popup_pop_condition.bean_name)',
  `pop_condition_value` varchar(255) DEFAULT '' COMMENT '弹窗条件值',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_popupid` (`popup_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='弹窗-弹窗条件';

-- 测试数据
INSERT INTO `template`.`popup_condition`(`id`, `popup_id`, `pop_condition`, `pop_condition_value`, `remark`, `create_time`, `update_time`) VALUES (1, 1, 'PushAreaCondition', '{\"areaCodes\":\"0\"}', '', '2023-09-04 11:41:12', '2023-09-04 11:41:42');
INSERT INTO `template`.`popup_condition`(`id`, `popup_id`, `pop_condition`, `pop_condition_value`, `remark`, `create_time`, `update_time`) VALUES (2, 3, 'PushAreaCondition', '{\"areaCodes\":\"0\"}', '', '2023-09-04 11:41:12', '2023-09-04 11:41:42');
INSERT INTO `template`.`popup_condition`(`id`, `popup_id`, `pop_condition`, `pop_condition_value`, `remark`, `create_time`, `update_time`) VALUES (12205, 10, 'PushAreaCondition', '{\"areaCodes\":\"0\"}', '', '2023-09-04 16:13:27', '2023-09-04 16:13:27');
INSERT INTO `template`.`popup_condition`(`id`, `popup_id`, `pop_condition`, `pop_condition_value`, `remark`, `create_time`, `update_time`) VALUES (12206, 11, 'PushAreaCondition', '{\"areaCodes\":\"0\"}', '', '2023-09-04 16:13:27', '2023-09-04 16:13:27');


DROP TABLE IF EXISTS `user_popup`;
CREATE TABLE `user_popup` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  `begin_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `status` varchar(8) NOT NULL DEFAULT '' COMMENT '状态(off:下架,on:上架)',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级(值越大，优先级越高)',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '标题',
  `text` varchar(128) NOT NULL DEFAULT '' COMMENT '文案',
  `bg_img` varchar(255) NOT NULL DEFAULT '' COMMENT '背景图json数据',
  `close_btn` varchar(255) NOT NULL DEFAULT '' COMMENT '关闭按钮json数据',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userid` (`user_id`),
  KEY `idx_userid_begintime` (`user_id`,`begin_time`),
  KEY `idx_userid_endtime` (`user_id`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户弹窗';



DROP TABLE IF EXISTS `popup_log`;
CREATE TABLE `popup_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `business_type` varchar(64) NOT NULL DEFAULT '' COMMENT '业务类型(popup:popup,user_popup:user_popup)',
  `business_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '业务ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT 'user_info.id',
  `deviceid` varchar(64) NOT NULL DEFAULT '' COMMENT '设备号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userid` (`user_id`),
  KEY `idx_deviceid` (`deviceid`),
  KEY `idx_businessid_businesstype` (`business_id`,`business_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户弹窗记录';

