-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
  `code` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `value` varchar(512) NOT NULL DEFAULT '' COMMENT '值',
  `config_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '参数备注',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '参数配置表';

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父部门id',
  `parent_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '父部门id列表',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` varchar(8) NOT NULL DEFAULT '' COMMENT '部门状态(ON:正常,OFF:停用)',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '部门表';

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `dict_type` varchar(32) NOT NULL DEFAULT '' COMMENT '字典类型',
  `dict_code` varchar(32) NOT NULL DEFAULT '' COMMENT '字典编码',
  `dict_value` varchar(255) NOT NULL DEFAULT '' COMMENT '字典值',
  `dict_sort` int(4) NOT NULL DEFAULT 0 COMMENT '字典排序',
  `is_default` char(1) NULL DEFAULT 'N' COMMENT '是否默认(Y:是,N:否)',
  `status` varchar(8) NULL DEFAULT '0' COMMENT '状态(ON:正常,OFF:停用)',
  `dict_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '字典备注',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_dicttype_dictcode`(`dict_type`, `dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '字典数据表';

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `dict_name` varchar(32) NOT NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(32) NOT NULL DEFAULT '' COMMENT '字典类型',
  `status` varchar(8) NOT NULL DEFAULT '0' COMMENT '状态(ON:正常,OFF:停用)',
  `dict_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '字典备注',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_dicttype`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '字典类型表';

-- ----------------------------
-- Table structure for sys_logininfo
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfo`;
CREATE TABLE `sys_logininfo`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sys_user_id` int(11) NOT NULL COMMENT 'sys_user.id',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `account` varchar(32) NOT NULL DEFAULT '' COMMENT '登录账户',
  `device` varchar(16) NOT NULL DEFAULT '' COMMENT '设备(ADMIN:admin端)',
  `platform` varchar(32) NOT NULL DEFAULT '' COMMENT '平台',
  `operator` varchar(32) NOT NULL DEFAULT '' COMMENT '操作系统',
  `version` varchar(32) NOT NULL DEFAULT '' COMMENT '版本',
  `deviceid` varchar(32) NOT NULL DEFAULT '' COMMENT '设备ID',
  `channel` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道',
  `ip` varchar(32) NOT NULL DEFAULT '' COMMENT 'ip',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '登录地区(根据ip获得)',
  `source` varchar(32) NOT NULL DEFAULT '' COMMENT '来源',
  `lang` varchar(32) NOT NULL DEFAULT '' COMMENT '语言',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sysuserid`(`sys_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '用户登录日志表';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `icon` varchar(128) NOT NULL DEFAULT '' COMMENT '菜单图标',
  `menu_name` varchar(64) NOT NULL DEFAULT '' COMMENT '菜单名称',
  `order_num` int(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) NOT NULL DEFAULT '' COMMENT '路由地址',
  `redirect` varchar(255) NOT NULL DEFAULT '' COMMENT '重定向',
  `component` varchar(255) NOT NULL DEFAULT '' COMMENT '组件路径',
  `query` varchar(255) NOT NULL DEFAULT '' COMMENT '路由参数',
  `menu_type` char(1) NOT NULL DEFAULT '' COMMENT '菜单类型(M:目录,C:菜单,F:按钮)',
  `status` varchar(8) NOT NULL DEFAULT 'OFF' COMMENT '菜单状态(ON:正常,OFF:停用)',
  `visible` tinyint(4) NOT NULL DEFAULT 0 COMMENT '显示状态(1:显示,0:隐藏)',
  `perms` varchar(128) NOT NULL DEFAULT '' COMMENT '权限标识',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '菜单权限表';

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sys_user_id` int(11) NOT NULL COMMENT 'sys_user.id',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NOT NULL DEFAULT 0 COMMENT '业务类型(0:其它,1:新增,2:修改,3:删除)',
  `method` varchar(128) NOT NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) NOT NULL DEFAULT '' COMMENT '请求方式',
  `oper_url` varchar(255) NOT NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) NOT NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) NOT NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) NOT NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) NOT NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NOT NULL DEFAULT 0 COMMENT '操作状态(0:正常,1:异常)',
  `error_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '错误消息',
  `cost_time` int(11) NOT NULL DEFAULT 0 COMMENT '耗时(毫秒)',
  `oper_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_businesstype`(`business_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_createtime`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '操作日志记录';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_name` varchar(32) NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_key` varchar(128) NOT NULL DEFAULT '' COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `data_scope` char(1) NULL DEFAULT '1' COMMENT '数据范围(1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限)',
  `status` varchar(8) NOT NULL DEFAULT '' COMMENT '角色状态(ON:正常,OFF:停用)',
  `role_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '角色备注',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '角色信息表';

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
  `dept_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '部门ID',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_roleid_deptid`(`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '角色和菜单关联表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
  `menu_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单ID',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_roleid_menuid`(`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '角色和菜单关联表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `account` varchar(32) NOT NULL DEFAULT '' COMMENT '账号',
  `nickname` varchar(32) NOT NULL DEFAULT '' COMMENT '昵称',
  `email` varchar(64) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) NOT NULL DEFAULT '0' COMMENT '用户性别(0:男,1:女,2:未知)',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '头像地址',
  `status` varchar(8) NOT NULL DEFAULT 'ON' COMMENT '帐号状态(ON:正常,OFF:停用)',
  `dept_id` int(11) NOT NULL DEFAULT 0 COMMENT '部门ID',
  `user_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '用户备注',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_account`(`account`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '用户信息表';

-- ----------------------------
-- Table structure for sys_user_password
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_password`;
CREATE TABLE `sys_user_password`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sys_user_id` int(10) UNSIGNED NOT NULL COMMENT 'sys_user.id',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `expire_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  `expire_login_times` int(11) NOT NULL DEFAULT 0 COMMENT '过期后登录次数',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sysuserid`(`sys_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '用户密码表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
  `role_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_userid_roleid`(`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '用户和角色关联表';
