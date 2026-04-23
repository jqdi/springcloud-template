package com.company.tool.canal;

import com.company.framework.util.JsonUtil;
import com.company.tool.cache.AppVersionCache;
import com.company.tool.entity.AppVersion;
import com.company.tool.service.AppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@Component
@CanalTable(value = "app_version") // 对应的数据库表名
public class AppVersionHandler implements EntryHandler<AppVersion> {

    @Autowired
    private AppVersionCache appVersionCache;
    @Autowired
    private AppVersionService appVersionService;

    @Override
    public void delete(AppVersion t) {
        log.info("删除操作: {}", t);
        appVersionCache.del(t.getAppCode());
        appVersionService.delLastByAppCodeCache(t.getAppCode());
    }

    @Override
    public void insert(AppVersion t) {
        log.info("插入操作: {}", t);
    }

    @Override
    public void update(AppVersion before, AppVersion after) {
        log.info("更新操作，更新前: {},更新后: {}", before, after);
        appVersionCache.del(after.getAppCode());
        appVersionCache.selectLastByAppCode(after.getAppCode());

        appVersionService.delLastByAppCodeCache(after.getAppCode());
        appVersionService.selectLastByAppCodeCache(after.getAppCode());
    }
}
