
CREATE TABLE `device_info` (
 `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
 `deviceid` varchar(64) NOT NULL COMMENT '设备ID',
 `app_code` varchar(32) NOT NULL DEFAULT '' COMMENT '应用编码(写死到客户端)',
 `platform` varchar(32) DEFAULT NULL COMMENT '平台：app(APP)、mini(微信小程序)、h5(H5页面)、alimini(支付宝小程序)、alipaymini(支付宝小程序内H5)',
 `operator` varchar(32) DEFAULT NULL COMMENT '操作系统：ios(iOS)、mac(iOS)、android(Android)、win(Windows,如果获取不到可以不要)、devtools(小程序开发工具)',
 `channel` varchar(32) DEFAULT NULL COMMENT '渠道：wx(微信小程序)、ali(支付宝小程序)、dev(开发包)、sit(测试包)、uat(验收包)、ios(苹果应用商店)、xiaomi(小米应用商店)、huawei(华为应用商店)等',
 `version` varchar(32) DEFAULT NULL COMMENT '版本号',
 `requestip` varchar(32) DEFAULT NULL COMMENT '请求IP',
 `request_user_agent` varchar(255) DEFAULT NULL COMMENT '请求UserAgent',
 `time` datetime DEFAULT NULL COMMENT '时间',

 `remark` varchar(255) DEFAULT NULL COMMENT '备注',
 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`id`) USING BTREE,
 UNIQUE KEY `uk_deviceid_appcode` (`deviceid`,`app_code`),
 KEY `idx_appcode` (`app_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息';


-- 监听登录事件，登录后记录
CREATE TABLE `user_device` (
 `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
 `user_id` int(11) NOT NULL COMMENT 'user_info.id',
 `deviceid` varchar(64) NOT NULL COMMENT '设备ID',
 `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
 `last_logout_time` datetime DEFAULT NULL COMMENT '最后登出时间',

 `remark` varchar(255) DEFAULT NULL COMMENT '备注',
 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`id`) USING BTREE,
 unique KEY `uniq_userid_deviceid` (`user_id`,`deviceid`),
 KEY `idx_deviceid` (`deviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备关系表';

-- 根据用户ID查询设备ID：select deviceid from user_device where user_id = 1 order by update_time desc limit 1
-- 根据设备ID查询用户ID：select user_id from user_device where deviceid = 'xxx' order by update_time desc limit 1
