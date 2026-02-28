package com.company.datasource.mybatisplus.handlers;

import java.time.LocalDateTime;

import com.company.datasource.CurrentUserProvider;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

public class AuditableMetaObjectHandler implements MetaObjectHandler {

    private static final String DEFAULT_CURRENT_USER_ID = "0";

    private final CurrentUserProvider currentUserProvider;

    public AuditableMetaObjectHandler(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Object currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            Object createBy = this.getFieldValByName("createBy", metaObject);
            if (createBy == null) {
                currentUser = DEFAULT_CURRENT_USER_ID;
            } else {
                currentUser = createBy;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        // 创建人
        Object createBy = this.getFieldValByName("createBy", metaObject);
        if (createBy == null) {
            this.setFieldValByName("createBy", currentUser, metaObject);
        }
        // 创建时间
        Object createTime = this.getFieldValByName("createTime", metaObject);
        if (createTime == null) {
            this.setFieldValByName("createTime", now, metaObject);
        }
        // 更新人
        Object updateBy = this.getFieldValByName("updateBy", metaObject);
        if (updateBy == null) {
            this.setFieldValByName("updateBy", currentUser, metaObject);
        }
        // 更新时间
        Object updateTime = this.getFieldValByName("updateTime", metaObject);
        if (updateTime == null) {
            this.setFieldValByName("updateTime", now, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            Object updateBy = this.getFieldValByName("updateBy", metaObject);
            if (updateBy == null) {
                currentUser = DEFAULT_CURRENT_USER_ID;
            } else {
                currentUser = updateBy;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        // 更新人
        this.setFieldValByName("updateBy", currentUser, metaObject);
        // 更新时间
        this.setFieldValByName("updateTime", now, metaObject);
    }
}
