package com.company.datasource.mybatisplus.handlers;

import java.time.LocalDateTime;

import com.company.datasource.CurrentUserProvider;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

public class InsertUpdateMetaObjectHandler implements MetaObjectHandler {

    private static final String DEFAULT_CURRENT_USER_ID = "0";

    private final CurrentUserProvider currentUserProvider;

    public InsertUpdateMetaObjectHandler(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
//        Object createTime1 = this.getFieldValByName("createTime", metaObject);
        if (metaObject.hasGetter("createTime")) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        }
        Object createTime = this.getFieldValByName("createTime", metaObject);

        // 填充更新时间（与创建时间一致）
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充创建人
        String currentUserId = currentUserProvider.currentUserId();
        if (currentUserId == null) {
            currentUserId = DEFAULT_CURRENT_USER_ID;
        }
        this.strictInsertFill(metaObject, "createBy", String.class, currentUserId);
        // 填充更新人
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUserId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 填充更新人
        String currentUserId = currentUserProvider.currentUserId();
        if (currentUserId == null) {
            currentUserId = DEFAULT_CURRENT_USER_ID;
        }
        this.strictUpdateFill(metaObject, "updateBy", String.class, currentUserId);
    }
}
