package com.company.datasource.mybatisplus.handlers;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.company.datasource.CurrentUserProvider;

/**
 * 审计字段Mybatis Plus处理器，用于自动填充审计字段（创建人、创建时间、更新人、更新时间）
 * 
 * <pre>
 * 审计字段需要声明@TableField(fill = FieldFill.INSERT)、@TableField(fill = FieldFill.INSERT_UPDATE)
 * </pre>
 * 
 * @author JQ棣
 */
public class AuditableMetaObjectHandler implements MetaObjectHandler {

    private static final int DEFAULT_CURRENT_USER_ID = 0;

    private final CurrentUserProvider currentUserProvider;

    public AuditableMetaObjectHandler(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Integer currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            Object createBy = this.getFieldValByName("createBy", metaObject);
            if (createBy == null) {
                currentUser = DEFAULT_CURRENT_USER_ID;
            } else {
                currentUser = Integer.valueOf(createBy.toString());
            }
        }
        LocalDateTime now = LocalDateTime.now();
        // 创建人
        this.strictInsertFill(metaObject, "createBy", Integer.class, currentUser);
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        // 更新人
        this.strictInsertFill(metaObject, "updateBy", Integer.class, currentUser);
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Integer currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            Object updateBy = this.getFieldValByName("updateBy", metaObject);
            if (updateBy == null) {
                currentUser = DEFAULT_CURRENT_USER_ID;
            } else {
                currentUser = Integer.valueOf(updateBy.toString());
            }
        }
        LocalDateTime now = LocalDateTime.now();
        // 更新人
        this.strictInsertFill(metaObject, "updateBy", Integer.class, currentUser);
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }
}
