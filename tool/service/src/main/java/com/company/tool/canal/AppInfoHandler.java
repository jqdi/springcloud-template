package com.company.tool.canal;

import com.company.framework.util.JsonUtil;
import com.company.tool.cache.AppInfoCache;
import com.company.tool.entity.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@Component
@CanalTable(value = "app_info") // 对应的数据库表名
public class AppInfoHandler implements EntryHandler<AppInfo> {

    @Autowired
    private AppInfoCache appInfoCache;

    @Override
    public void delete(AppInfo t) {
        log.info("删除操作: {}", t);
        appInfoCache.del(t.getId());
    }

    @Override
    public void insert(AppInfo t) {
        log.info("插入操作: {}", t);
    }

    @Override
    public void update(AppInfo before, AppInfo after) {
        log.info("更新操作，更新前: {},更新后: {}", before, after);
        appInfoCache.del(after.getId());
        appInfoCache.getById(after.getId());
    }
}
