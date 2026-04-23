package com.company.system.canal;

import com.company.framework.util.JsonUtil;
import com.company.system.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@Component
@CanalTable(value = "sys_user") // 对应的数据库表名
public class SysUserHandler implements EntryHandler<SysUser> {

    @Override
    public void delete(SysUser t) {
        log.info("删除操作: {}", t);
    }

    @Override
    public void insert(SysUser t) {
        log.info("插入操作: {}", t);
    }

    @Override
    public void update(SysUser before, SysUser after) {
        log.info("更新操作，更新前: {},更新后: {}", before, after);
    }
}
