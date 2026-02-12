package com.company.datasource.mybatis.i18n.plugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.company.datasource.mybatis.i18n.annotation.I18nField;
import com.company.datasource.mybatis.i18n.metadata.I18nFieldInfo;
import com.company.datasource.mybatis.i18n.metadata.RelatedI18nValueMapping;
import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.datasource.mybatis.i18n.toolkit.CollectionUtils;
import com.company.datasource.mybatis.i18n.toolkit.ReflectionKit;

/**
 * 国际化字段值替换处理器
 *
 * @author JQ棣
 */
public class I18nReplaceHandler {
    private static final Log logger = LogFactory.getLog(I18nReplaceHandler.class);

    private final ConcurrentHashMap<Class<?>, List<I18nFieldInfo>> CLASS_METADATA_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析类中使用的@I18nField注解的字段
     *
     * @param entityClass 数据库实体类class
     * @return 国际化字段信息
     */
    public List<I18nFieldInfo> parserI18nMetadata(Class<?> entityClass) {
        // 参考ReflectionKit.getFieldList实现缓存
        return CollectionUtils.computeIfAbsent(CLASS_METADATA_CACHE, entityClass, k -> {
            List<I18nFieldInfo> i18nFieldInfoList = new ArrayList<>();
            List<Field> allFields = ReflectionKit.getFieldList(k);
            Map<String, Field> fieldMap = ReflectionKit.getFieldMap(k);
            for (Field field : allFields) {
                I18nField i18nField = field.getAnnotation(I18nField.class);
                if (i18nField == null) {
                    continue;
                }
                String i18nTable = i18nField.i18nTable();
                String i18nColumn = i18nField.i18nColumn();
                Field relatedValueFromField = fieldMap.get(i18nField.relatedValueFromField());
                Class<? extends I18nDataProvider> i18nDataProviderClass = i18nField.i18nDataProvider();
                I18nFieldInfo i18nFieldInfo = new I18nFieldInfo(field, i18nTable, i18nColumn, i18nField.i18nRelatedColumn(),
                    relatedValueFromField, i18nField.i18nLocaleColumn(), i18nDataProviderClass);
                i18nFieldInfoList.add(i18nFieldInfo);
            }
            return i18nFieldInfoList;
        });
    }

    /**
     * 收集关联字段值（去重）
     *
     * @param entityList    实体类结果集
     * @param i18nFieldInfo 国际化字段信息
     * @return 关联字段值列表（去重）
     */
    public Set<Object> collectRelatedFieldValue(Collection<?> entityList, I18nFieldInfo i18nFieldInfo) {
        // 使用反射将entityList中的每个对象的relatedValueFromField对应的值取出
        Set<Object> relatedFieldValueSet = new HashSet<>();
        Field relatedValueFromField = i18nFieldInfo.getRelatedValueFromField();
        for (Object entity : entityList) {
            Object relatedValue = getFieldValue(entity, relatedValueFromField);
            if (relatedValue == null) {
                logger.warn(relatedValueFromField.getName() + " relatedValue is null");
                continue;
            }
            relatedFieldValueSet.add(relatedValue);
        }
        return relatedFieldValueSet;
    }

    /**
     * 替换国际化字段值
     *
     * @param entityList    实体类结果集
     * @param i18nFieldInfo 国际化字段信息
     */
    public void replaceI18nFieldValue(Collection<?> entityList, I18nFieldInfo i18nFieldInfo,
                                      List<RelatedI18nValueMapping> valueMappingList) {
        // 注入国际化数据
        Field relatedValueFromField = i18nFieldInfo.getRelatedValueFromField();
        Map<Object, Object> i18nRelatedColumnI18nValueMap =
            valueMappingList.stream().filter(v -> v.getI18nRelatedValue() != null && v.getI18nValue() != null).collect(Collectors
                .toMap(RelatedI18nValueMapping::getI18nRelatedValue, RelatedI18nValueMapping::getI18nValue, (a, b) -> b));
        for (Object entity : entityList) {
            Object relatedValue = getFieldValue(entity, relatedValueFromField);
            if (relatedValue == null) {
                logger.warn(relatedValueFromField.getName() + " relatedValue is null");
                continue;
            }

            Object i18nValue = i18nRelatedColumnI18nValueMap.get(relatedValue);
            if (i18nValue == null) {
                logger.warn(relatedValue + " i18nValue is null");
                continue;
            }
            Field field = i18nFieldInfo.getField();
            setFieldValue(entity, field, i18nValue);
        }
    }

    private static Object getFieldValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            field.setAccessible(false);
            return value;
        } catch (ReflectiveOperationException e) {
            logger.error("Error: Cannot read field in " + entity.getClass().getSimpleName() + ".  Cause:", e);
        }
        return null;
    }

    private static void setFieldValue(Object entity, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(entity, value);
            field.setAccessible(false);
        } catch (ReflectiveOperationException e) {
            logger.error("Error: Cannot set field in " + entity.getClass().getSimpleName() + ".  Cause:", e);
        }
    }

}
