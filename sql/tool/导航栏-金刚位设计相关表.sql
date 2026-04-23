DROP TABLE IF EXISTS `nav_item_show_condition`;
CREATE TABLE `nav_item_show_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'bean名称(NavShowCondition的实现类)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序（由大到小排列）',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_beanname` (`bean_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='展示条件';

-- 测试数据
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (2, 'nav-SenceCondition', '场景', 0, '参数：sences(mini_home:首页,takeout_index:外卖首页)', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (3, 'nav-PushAreaCondition', '推送地区', 0, '参数：cityCodes', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (4, 'nav-PushPlatformCondition', '推送平台', 0, '参数：platforms(0:小程序·安卓, 1:小程序·IOS, 2:APP·安卓, 3:APP·IOS)', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (5, 'nav-SourceCondition', '来源渠道', 0, '参数：sources', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (6, 'nav-SpecifyUserCondition', '指定用户', 0, '参数：appUserIds', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (7, 'nav-LoginedCondition', '已登录用户', 0, '', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (8, 'nav-TimeRangeCondition', '指定时间段', 0, '参数：beginTime，endTime(格式HH-MM-SS)', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');
INSERT INTO `nav_item_show_condition` (`id`, `bean_name`, `description`, `sort`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (11, 'nav-WeekDayCondition', '指定星期几', 0, '参数：weekDays(1表示周日，2表示周一)', '2023-09-21 11:52:34', '', '2023-09-21 11:52:42', '');


DROP TABLE IF EXISTS `nav_item`;
CREATE TABLE `nav_item` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `begin_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `status` varchar(8) NOT NULL DEFAULT 'off' COMMENT '状态(off:下架,on:上架)',
  `position` int(11) NOT NULL DEFAULT '99' COMMENT '位置(0代表任意位置,左->右,上->下)',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级(值越大，优先级越高)',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '标题',
  `logo` varchar(255) NOT NULL DEFAULT '' COMMENT '图标',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '类型(redirect_http:跳转http链接,redirect_mini:跳转小程序链接,redirect_other_mini:跳转其他小程序链接)',
  `value` varchar(255) NOT NULL DEFAULT '' COMMENT '类型值',
  `attach_json` varchar(255) NOT NULL DEFAULT '{}' COMMENT '附加json',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_begintime` (`begin_time`),
  KEY `idx_endtime` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航-金刚位';

-- 测试数据
INSERT INTO `nav_item` (`id`, `begin_time`, `end_time`, `status`, `position`, `priority`, `title`, `logo`, `type`, `value`, `attach_json`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (1, '2024-03-26 15:35:22', '2025-03-26 15:35:21', 'on', 1, 9, '测试1', 'http://www.soiadsa.com/iamge/aaa.jpg', 'redirect_mini', '/pagesLocalcityfastfood/goupfoodonlineactivity/webview?fullPath=%2FcircleReward%3FactivityId%3D99', '{\"tagValue\":\"111\",\"aaa\":133}', '', '2024-03-26 15:35:22', '', '2024-03-26 15:46:55', '');
INSERT INTO `nav_item` (`id`, `begin_time`, `end_time`, `status`, `position`, `priority`, `title`, `logo`, `type`, `value`, `attach_json`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (2, '2024-03-26 15:35:22', '2025-03-26 15:35:21', 'on', 1, 11, '测试2', 'http://www.soiadsa.com/iamge/aaa.jpg', 'redirect_other_mini', 'wx3c82b96ef51cda47|?VerseId=10003&Channel=mypay&OutUid={userId}|trial', '{}', '', '2024-03-26 15:35:22', '', '2024-03-27 10:08:41', '');
INSERT INTO `nav_item` (`id`, `begin_time`, `end_time`, `status`, `position`, `priority`, `title`, `logo`, `type`, `value`, `attach_json`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (3, '2024-03-26 15:35:22', '2025-03-26 15:35:21', 'on', 99, 0, '榜单-{bdgoods.name}', '{bdgoods.image}', 'redirect_mini', '/pagesLocalcityfastfood/aaaa?productCode={bdgoods.code}', '{}', '', '2024-03-26 15:35:22', '', '2024-03-27 10:06:14', '');


DROP TABLE IF EXISTS `nav_item_condition`;
CREATE TABLE `nav_item_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `nav_item_id` int(11) NOT NULL DEFAULT '0' COMMENT 'nav_item.id',
  `show_condition` varchar(32) NOT NULL DEFAULT '' COMMENT '展示条件(bean名称,nav_item_show_condition.bean_name)',
  `show_condition_value` varchar(255) NOT NULL DEFAULT '' COMMENT '展示条件值',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_navitemid` (`nav_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销-导航-金刚位-展示条件';


-- 测试数据
INSERT INTO `nav_item_condition` (`id`, `nav_item_id`, `show_condition`, `show_condition_value`, `remark`, `create_time`, `update_time`) VALUES (1, 1, 'nav-SenceCondition', '{\"sences\":\"a\"}', '', '2024-03-26 15:36:32', '2024-03-26 15:37:10');
INSERT INTO `nav_item_condition` (`id`, `nav_item_id`, `show_condition`, `show_condition_value`, `remark`, `create_time`, `update_time`) VALUES (2, 2, 'nav-SenceCondition', '{\"sences\":\"a\"}', '', '2024-03-26 15:36:32', '2024-03-26 15:39:56');
INSERT INTO `nav_item_condition` (`id`, `nav_item_id`, `show_condition`, `show_condition_value`, `remark`, `create_time`, `update_time`) VALUES (3, 3, 'nav-SenceCondition', '{\"sences\":\"a\"}', '', '2024-03-26 15:36:32', '2024-03-26 15:39:56');

