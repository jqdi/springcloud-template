-- 一张表存储所有的国际化翻译
CREATE TABLE `common_i18n` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `business_id` int NOT NULL COMMENT '业务ID',
  `business_type` varchar(32) NOT NULL COMMENT '业务类型(建议填写[表.字段]，如banner.title)',
  `locale` varchar(8) NOT NULL DEFAULT '' COMMENT '地区编码',
  `i18n_text` text COMMENT '国际化文案',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` int unsigned NOT NULL DEFAULT '0' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int unsigned NOT NULL DEFAULT '0' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_businessid_businesstype_locale` (`business_id`,`business_type`,`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用国际化';

INSERT INTO `common_i18n` (`id`, `business_id`, `business_type`, `locale`, `i18n_text`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (1, 1, 'banner.title', 'en-US', 'Banner-Common', '', '2026-02-11 17:47:56', 1, '2026-02-11 17:50:14', 1);


-- 根据业务分表存储的国际化翻译
-- banner国际化翻译表
CREATE TABLE `banner_i18n` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `banner_id` int(11) NOT NULL COMMENT 'mk_banner.id',
  `locale` varchar(8) NOT NULL DEFAULT '' COMMENT '地区编码',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '标题',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_bannerid_locale` (`banner_id`,`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图-国际化';

-- 测试数据
INSERT INTO `banner_i18n` (`id`, `banner_id`, `locale`, `title`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (1, 1, 'zh-CN', '轮播图', '', '2026-02-11 17:47:56', 1, '2026-02-11 17:50:14', 1);
INSERT INTO `banner_i18n` (`id`, `banner_id`, `locale`, `title`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (2, 1, 'en-US', 'Banner', '', '2026-02-11 17:47:56', 1, '2026-02-11 17:50:14', 1);

-- app_version国际化翻译表
CREATE TABLE `app_version_i18n` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `app_version_id` int(11) NOT NULL COMMENT 'app_version.id',
  `locale` varchar(8) NOT NULL DEFAULT '' COMMENT '地区编码',
  `release_notes` text COMMENT '发布说明',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_appversionid_locale` (`app_version_id`,`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP版本-国际化';

INSERT INTO `app_version_i18n` (`id`, `app_version_id`, `locale`, `release_notes`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES (1, 4, 'en-US', 'Major bugs in version 1.0.0 have been fixed, and version 1.0.0 is no longer supported', '', '2026-02-11 17:47:56', 1, '2026-02-11 17:50:14', 1);
