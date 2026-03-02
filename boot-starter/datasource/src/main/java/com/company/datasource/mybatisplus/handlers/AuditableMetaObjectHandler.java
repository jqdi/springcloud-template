package com.company.datasource.mybatisplus.handlers;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import com.company.datasource.CurrentUserProvider;
import com.company.datasource.mybatisplus.base.AuditableModel;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

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
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof AuditableModel) {
            AuditableModel auditableModel = (AuditableModel)originalObject;
        }
        Field[] declaredFields = AuditableModel.class.getDeclaredFields();
        LocalDateTime now = LocalDateTime.now();
        // 创建人
//        Object createBy = this.getFieldValByName("createBy", metaObject);
//        if (createBy == null) {
//            this.setFieldValByName("createBy", currentUser, metaObject);
//        }
        this.strictInsertFill(metaObject, "createBy", Object.class, currentUser);
        // 创建时间
//        Object createTime = this.getFieldValByName("createTime", metaObject);
//        if (createTime == null) {
//            this.setFieldValByName("createTime", now, metaObject);
//        }
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        // 更新人
//        Object updateBy = this.getFieldValByName("updateBy", metaObject);
//        if (updateBy == null) {
//            this.setFieldValByName("updateBy", currentUser, metaObject);
//        }
        this.strictInsertFill(metaObject, "updateBy", Object.class, currentUser);
        // 更新时间
//        Object updateTime = this.getFieldValByName("updateTime", metaObject);
//        if (updateTime == null) {
//            this.setFieldValByName("updateTime", now, metaObject);
//        }
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
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
//        this.setFieldValByName("updateBy", currentUser, metaObject);
        this.strictInsertFill(metaObject, "updateBy", Object.class, currentUser);
        // 更新时间
//        this.setFieldValByName("updateTime", now, metaObject);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }
}
