package com.company.datasource.mybatisplus.handlers;

import java.lang.reflect.Field;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.company.datasource.AuditableModelProvider;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;

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
    private final AuditableModelProvider auditableModelProvider;

    public AuditableMetaObjectHandler(AuditableModelProvider auditableModelProvider) {
        this.auditableModelProvider = auditableModelProvider;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        AuditableModel<?> auditableModel = auditableModelProvider.getAuditableModel();
        Field[] fieldList = auditableModel.getClass().getDeclaredFields();
        for (Field field : fieldList) {
            String fieldName = field.getName();
            @SuppressWarnings("unchecked")
            Class<Object> fieldType = (Class<Object>)field.getType();
            Object fieldVal = ReflectionKit.getFieldValue(auditableModel, fieldName);
            this.strictInsertFill(metaObject, fieldName, fieldType, fieldVal);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        AuditableModel<?> auditableModel = auditableModelProvider.getAuditableModel();
        Field[] fieldList = auditableModel.getClass().getDeclaredFields();
        for (Field field : fieldList) {
            String fieldName = field.getName();
            @SuppressWarnings("unchecked")
            Class<Object> fieldType = (Class<Object>)field.getType();
            Object fieldVal = ReflectionKit.getFieldValue(auditableModel, fieldName);
            this.strictUpdateFill(metaObject, fieldName, fieldType, fieldVal);
        }
    }
}
