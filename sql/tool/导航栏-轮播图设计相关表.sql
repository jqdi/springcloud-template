
DROP TABLE IF EXISTS `banner_show_condition`;
CREATE TABLE `banner_show_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(32) NOT NULL DEFAULT '' COMMENT 'bean名称(BannerShowCondition的实现类)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序（由大到小排列）',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_beanname` (`bean_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图-展示条件';

-- 测试数据
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (2, 'banner-SenceCondition', '场景', '参数：sences(mini_home:首页,takeout_index:外卖首页)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (3, 'banner-PushAreaCondition', '推送地区', '参数：cityCodes', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (4, 'banner-PushPlatformCondition', '推送平台', '参数：platforms(0:小程序·安卓, 1:小程序·IOS, 2:APP·安卓, 3:APP·IOS)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (5, 'banner-SourceCondition', '来源渠道', '参数：sources', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (6, 'banner-SpecifyUserCondition', '指定用户', '参数：appUserIds', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (7, 'banner-LoginedCondition', '已登录用户', '', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (8, 'banner-TimeRangeCondition', '指定时间段', '参数：beginTime，endTime(格式HH-MM-SS)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');
INSERT INTO `banner_show_condition`(`id`, `bean_name`, `description`, `remark`, `create_time`, `update_time`) VALUES (11, 'banner-WeekDayCondition', '指定星期几', '参数：weekDays(1表示周日，2表示周一)', '2023-09-21 11:52:34', '2023-09-21 11:52:42');


DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `begin_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `status` varchar(8) NOT NULL DEFAULT 'off' COMMENT '状态(off:下架,on:上架)',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级(值越大，优先级越高)',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '标题',
  `image` varchar(255) NOT NULL DEFAULT '' COMMENT '图片',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '类型(redirect_no:不跳转,redirect_http:跳转http链接,redirect_mini:跳转小程序链接,redirect_other_mini:跳转其他小程序链接)',
  `value` varchar(255) NOT NULL DEFAULT '' COMMENT '类型值',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_begintime` (`begin_time`),
  KEY `idx_endtime` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图';


-- 测试数据
INSERT INTO `banner`(`id`, `begin_time`, `end_time`, `status`, `priority`, `title`, `image`, `type`, `value`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES (1, '2024-03-26 16:31:44', '2025-03-26 16:31:42', 'on', 0, '1111', 'http://asds/aa.jpg', 'redirect_mini', 'sadsadsad', '', '2024-03-26 16:31:44', '2024-03-26 16:32:14', '', '');


DROP TABLE IF EXISTS `banner_condition`;
CREATE TABLE `banner_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `banner_id` int(11) NOT NULL COMMENT 'banner.id',
  `show_condition` varchar(32) NOT NULL DEFAULT '' COMMENT '展示条件(bean名称,banner_show_condition.bean_name)',
  `show_condition_value` varchar(255) NOT NULL DEFAULT '' COMMENT '展示条件值',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_bannerid` (`banner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图-条件';


-- 测试数据
INSERT INTO `banner_condition`(`id`, `banner_id`, `show_condition`, `show_condition_value`, `remark`) VALUES (1, 1, 'banner-SenceCondition', '{\"sences\":\"a,b\"}', '');
