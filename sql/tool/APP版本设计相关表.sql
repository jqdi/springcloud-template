CREATE TABLE `app_info` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `app_code` varchar(32) NOT NULL COMMENT '唯一标识（写死到客户端）',
  `app_name` varchar(32) NOT NULL COMMENT '名称',
  `platform` varchar(16) NOT NULL COMMENT '平台(Android、iOS)',
  `logo` varchar(255) DEFAULT NULL COMMENT '图标',

  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_appcode` (`app_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP信息';

INSERT INTO app_info (id, app_code, app_name, platform, logo, remark, create_time, update_time, create_by, update_by) VALUES (1, 'jd_android', '京东', 'Android', 'http://image.aa.com/jd_logo.jpg', '', '2025-06-11 20:57:04', '2025-06-11 21:02:29', '', '');
INSERT INTO app_info (id, app_code, app_name, platform, logo, remark, create_time, update_time, create_by, update_by) VALUES (2, 'jd_ios', '京东', 'iOS', 'http://image.aa.com/jd_logo.jpg', '', '2025-06-11 20:58:17', '2025-06-11 21:02:29', '', '');
INSERT INTO app_info (id, app_code, app_name, platform, logo, remark, create_time, update_time, create_by, update_by) VALUES (3, 'taobao_android', '淘宝', 'Android', 'http://image.aa.com/taobao_logo.jpg', '', '2025-06-11 20:58:17', '2025-06-11 21:02:29', '', '');
INSERT INTO app_info (id, app_code, app_name, platform, logo, remark, create_time, update_time, create_by, update_by) VALUES (4, 'taobao_ios', '淘宝', 'iOS', 'http://image.aa.com/taobao_logo.jpg', '', '2025-06-11 20:58:17', '2025-06-11 21:02:29', '', '');

CREATE TABLE `app_version` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `app_code` varchar(32) NOT NULL COMMENT 'app_info.app_code',
  `version` varchar(32) NOT NULL COMMENT '版本号',
  `min_supported_version` varchar(32) DEFAULT NULL COMMENT '最低支持版本（低于此版本必须升级）',
  `release_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `download_url` varchar(255) NOT NULL COMMENT '安装包下载地址',
  `release_notes` text COMMENT '发布说明',

  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_appcode_releasetime` (`app_code`,`release_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP版本';

INSERT INTO app_version (id, app_code, version, min_supported_version, release_time, download_url, release_notes, remark, create_time, update_time, create_by, update_by) VALUES (1, 'jd_android', '1.0.0', '1.0.0', '2025-06-11 21:00:20', 'https://dw.jd.com/jd_andriod_1.0.0.apk', '第一版', '', '2025-06-11 21:00:20', '2025-06-11 21:01:42', ' ', ' ');
INSERT INTO app_version (id, app_code, version, min_supported_version, release_time, download_url, release_notes, remark, create_time, update_time, create_by, update_by) VALUES (2, 'jd_android', '1.0.1', '1.0.0', '2025-06-11 21:00:20', 'https://dw.jd.com/jd_andriod_1.0.1.apk', '修复了用户体验', '', '2025-06-11 21:00:20', '2025-06-11 21:01:42', ' ', ' ');
INSERT INTO app_version (id, app_code, version, min_supported_version, release_time, download_url, release_notes, remark, create_time, update_time, create_by, update_by) VALUES (3, 'taobao_android', '1.0.0', '1.0.0', '2025-06-11 21:00:20', 'https://dw.jd.com/taobao_andriod_1.0.1.apk', '第一版', '', '2025-06-11 21:00:20', '2025-06-11 21:01:42', ' ', ' ');
INSERT INTO app_version (id, app_code, version, min_supported_version, release_time, download_url, release_notes, remark, create_time, update_time, create_by, update_by) VALUES (4, 'jd_android', '1.0.2', '1.0.1', '2025-06-11 21:00:20', 'https://dw.jd.com/jd_andriod_1.0.2.apk', '修复了1.0.0中的重大bug，不再支持1.0.0版本', '', '2025-06-11 21:00:20', '2025-06-12 10:15:37', ' ', ' ');
