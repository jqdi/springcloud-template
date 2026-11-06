CREATE TABLE `user_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `deviceid` varchar(32) NOT NULL DEFAULT '' COMMENT '设备ID',
  `source` varchar(32) NOT NULL DEFAULT '' COMMENT '来源',
  `time` datetime NOT NULL COMMENT '时间',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_deviceid` (`deviceid`),
  KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户来源表';


