-- 优惠券
CREATE TABLE `coupon_use_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'bean名称(UseCondition的实现类)',
  `descrpition` varchar(255) DEFAULT NULL COMMENT '描述',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_beanname` (`bean_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='营销-优惠券使用条件';


CREATE TABLE `coupon_template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '优惠券名称',
  `max_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '最大优惠金额(元)',
  `discount` decimal(12,2) NOT NULL DEFAULT '1.00' COMMENT '折扣',
  `condition_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '满X元可用',
  `begin_time` datetime NOT NULL COMMENT '发放开始时间',
  `end_time` datetime NOT NULL COMMENT '发放结束时间',
  `period_type` varchar(8) NOT NULL DEFAULT '' COMMENT '有效期计算方式(fix:领券起固定有效期,ps:固定开始时间-开始时间,fx:指定行为激活后X天有效)',
  `total_num` int(11) NOT NULL DEFAULT '0' COMMENT '发放量',
  `left_num` int(11) NOT NULL DEFAULT '0' COMMENT '剩余量',
  `limit_num` int(11) NOT NULL DEFAULT '1' COMMENT '每用户限领量',
  `use_description` text COMMENT '使用说明',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板';


CREATE TABLE `coupon_template_condition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `coupon_template_id` int(11) NOT NULL COMMENT 'coupon_template.id',
  `use_condition` varchar(32) NOT NULL DEFAULT '' COMMENT '使用条件(bean名称,coupon_use_condition.bean_name)',
  `use_condition_value` varchar(255) DEFAULT '' COMMENT '使用条件值',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_coupontemplateid` (`coupon_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='营销-优惠券模板-使用条件';



CREATE TABLE `user_coupon` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'user_info.id',
  `coupon_template_id` int(11) NOT NULL COMMENT 'coupon_template.id',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '优惠券名称',
  `max_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '最大优惠金额',
  `discount` decimal(12,2) NOT NULL DEFAULT '1.00' COMMENT '折扣',
  `condition_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '满X金额可用',
  `begin_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `status` varchar(8) NOT NULL DEFAULT '' COMMENT '状态(nouse:未使用/已使用/已过期/未激活/已失效)',
  `max_num` int(11) NOT NULL COMMENT '最大叠加数',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userid` (`user_id`),
  KEY `idx_coupontemplateid` (`coupon_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='营销-用户优惠券';


CREATE TABLE `coupon_grant` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `coupon_template_id` int(11) NOT NULL COMMENT 'coupon_template.id',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT 'coupon_template.name',
  `total_num` int(11) NOT NULL DEFAULT '0' COMMENT '发放量',
  `actual_num` int(11) NOT NULL DEFAULT '0' COMMENT '实际发放量',
  `grant_time` datetime NOT NULL COMMENT '发放时间',
  `grant_condition` varchar(255) DEFAULT '' COMMENT '发放条件',
  `draw_condition` varchar(255) DEFAULT '' COMMENT '领取条件',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coupontemplateid` (`coupon_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销-优惠券发放';


