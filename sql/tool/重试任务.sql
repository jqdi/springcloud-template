CREATE TABLE `retry_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `url` varchar(128) NOT NULL COMMENT '回调地址',
  `json_params` varchar(2048) DEFAULT NULL COMMENT 'JSON参数',
  `json_result` varchar(128) DEFAULT NULL COMMENT 'JSON结果',
  `status` tinyint(4) NOT NULL COMMENT '状态(1:待回调,2:回调成功(END),21.回调失败,22.放弃回调(END))',
  `wait_strategy` varchar(32) NOT NULL DEFAULT 'incrementing' COMMENT '等待策略(incrementing:线性递增,fixed:固定等待,exponential:指数退避,fibonacci:斐波那契,random:随机,wechat:微信)',
  `increase_seconds` int(11) NOT NULL DEFAULT '2' COMMENT '递增秒数',
  `next_dispose_time` datetime NOT NULL COMMENT '下次处理时间',
  `max_failure` int(11) NOT NULL COMMENT '最大允许失败次数',
  `failure` int(11) NOT NULL DEFAULT '0' COMMENT '当前失败次数',

  `trace_id` varchar(32) DEFAULT NULL COMMENT '追踪ID',

  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_status_ndt` (`status`,`next_dispose_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重试任务';
