package com.company.user.canal;

import com.company.framework.util.JsonUtil;
import com.company.user.cache.UserInfoCache;
import com.company.user.entity.UserInfo;
import com.company.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@Component
@CanalTable(value = "user_info") // 对应的数据库表名
public class UserInfoHandler implements EntryHandler<UserInfo> {

    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void delete(UserInfo t) {
        log.info("删除操作: {}", t);
        userInfoCache.del(t.getId());
    }

    @Override
    public void insert(UserInfo t) {
        log.info("插入操作: {}", t);
    }

    @Override
    public void update(UserInfo before, UserInfo after) {
        log.info("更新操作，更新前: {},更新后: {}", before, after);
        userInfoCache.del(after.getId());
        userInfoCache.getById(after.getId());

        userInfoService.delByIdCache(after.getId());
        userInfoService.selectByIdCache(after.getId());
    }
}
