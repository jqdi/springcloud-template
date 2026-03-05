package com.company.datasource.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.ClassUtils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.company.datasource.AuditableModelProvider;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;

/**
 * 审计字段Mybatis Plus拦截器，用于自动填充审计字段（创建人、创建时间、更新人、更新时间）
 * 
 * <pre>
 * 审计字段需要声明@TableField(fill = FieldFill.INSERT)、@TableField(fill = FieldFill.INSERT_UPDATE)
 * 与AuditableMetaObjectHandler不同的地方是：该拦截器可处理非 BaseMapper API执行的SQL
 * </pre>
 * 
 * @see com.company.datasource.mybatisplus.handlers.AuditableMetaObjectHandler
 * @author JQ棣
 */
public class AuditableInterceptor implements InnerInterceptor {
    private static final Log logger = LogFactory.getLog(AuditableInterceptor.class);
    private static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private final AuditableModelProvider auditableModelProvider;

    public AuditableInterceptor(AuditableModelProvider auditableModelProvider) {
        this.auditableModelProvider = auditableModelProvider;
    }

    /**
     * 从 StatementHandler 中获取 MappedStatement（通过反射）
     */
    private MappedStatement getMappedStatement(StatementHandler sh) {
        MetaObject metaStatementHandler = SystemMetaObject.forObject(sh);
        try {
            return (MappedStatement)metaStatementHandler.getValue("delegate.mappedStatement");
        } catch (Exception e) {
            logger.error("获取 MappedStatement 失败", e);
            return null;
        }
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        try {
            addAuditFieldSql(sh);
        } catch (Exception e) {
            // 捕获所有异常，避免异常中断
            logger.error("添加审计字段失败", e);
        }
    }

    protected void addAuditFieldSql(StatementHandler sh) {
        BoundSql boundSql = sh.getBoundSql();
        MappedStatement ms = this.getMappedStatement(sh);

        if (ms == null) {
            return;
        }

        // 仅处理 INSERT UPDATE 操作
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (SqlCommandType.INSERT != sqlCommandType && SqlCommandType.UPDATE != sqlCommandType) {
            return;
        }

        String resource = ms.getResource();
        int index = resource.indexOf(".java");
        if (index == -1) {
            return;
        }
        String className = resource.substring(0, index).replace("/", ".");

        Class<?> entityClass = getEntityClassByMapperClassName(className);
        if (entityClass == null) {
            return;
        }

        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        if (tableInfo == null) {
            return;
        }
        if (!tableInfo.isWithInsertFill() && !tableInfo.isWithUpdateFill()) {
            return;
        }

        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, TableFieldInfo> propertyThisMap =
            fieldList.stream().collect(Collectors.toMap(TableFieldInfo::getProperty, a -> a, (a, b) -> b));

        // 记录审计字段与填充值的关系（需保证顺序，用LinkedHashMap）
        Map<TableFieldInfo, String> auditFieldMap = new LinkedHashMap<>();

        AuditableModel<?> auditableModel = auditableModelProvider.getAuditableModel();
        Field[] auditableModelFieldList = auditableModel.getClass().getDeclaredFields();
        for (Field auditableModelField : auditableModelFieldList) {
            String fieldName = auditableModelField.getName();
            TableFieldInfo fieldInfo = propertyThisMap.get(fieldName);
            if (fieldInfo == null) {
                continue;
            }
            Object fieldVal = ReflectionKit.getFieldValue(auditableModel, fieldName);
            auditFieldMap.put(fieldInfo, formatValue(fieldVal));
        }

        if (auditFieldMap.isEmpty()) {
            return;
        }

        String originalSql = boundSql.getSql();
        String newSql;
        if (SqlCommandType.INSERT == sqlCommandType) {
            if (!tableInfo.isWithInsertFill()) {
                return;
            }
            newSql = appendAuditFieldsToInsert(originalSql, auditFieldMap);
        } else {
            if (!tableInfo.isWithUpdateFill()) {
                return;
            }
            newSql = appendAuditFieldsToUpdate(originalSql, auditFieldMap);
        }
        logger.debug("sql change: " + originalSql + " ==> " + newSql);
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(newSql);
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN).format((LocalDateTime)value);
        }
        if (value instanceof Date) {
            DateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
            return dateFormat.format(value);
        }
        return value.toString();
    }

    /**
     * 为INSERT语句拼接审计字段
     */
    private static String appendAuditFieldsToInsert(String originalSql, Map<TableFieldInfo, String> auditFields) {
        if (auditFields.isEmpty()) {
            return originalSql;
        }
        /*
        例：INSERT INTO `sys_config` (`code`, `value`) VALUES ('1', '1');
        将sql语句分成3段
        1. INSERT INTO `sys_config` (`code`, `value`   -> 插入字段名
        2. ) VALUES ('1', '1'   -> 插入字段名
        3. );
        得到：INSERT INTO `sys_config` (`code`, `value`, `create_time`, `create_by`, `update_time`, `update_by`) VALUES ('1', '1', '2020-01-01 00:00:00', '1', '2020-01-01 00:00:00', '1');
         */
        String originalSqlU = originalSql.toUpperCase();

        // 检测是否包含ON DUPLICATE KEY UPDATE子句
        int duplicateKeyUpdateIndex = originalSqlU.indexOf("ON DUPLICATE KEY UPDATE");
        boolean hasDuplicateKeyUpdate = duplicateKeyUpdateIndex != -1;

        // 找到VALUES的前一个右括号的位置（字段列表的结束位置）
        int valuesIndex = originalSqlU.indexOf("VALUES");
        if (valuesIndex == -1) {
            return originalSql;
        }
        String valuesEndStr = originalSqlU.substring(0, valuesIndex);
        int fieldsEndIndex = valuesEndStr.lastIndexOf(")");
        if (fieldsEndIndex == -1) {
            return originalSql;
        }

        // 找到值列表的开始位置
        int valuesStartIndex = originalSqlU.indexOf("VALUES", fieldsEndIndex);
        if (valuesStartIndex == -1) {
            return originalSql;
        }

        // 找到值列表的开始位置（左括号）
        int valuesLeftBracketIndex = originalSqlU.indexOf("(", valuesStartIndex);
        if (valuesLeftBracketIndex == -1) {
            return originalSql;
        }

        // 找到值列表的结束位置（右括号）
        int valuesEndIndex;
        if (hasDuplicateKeyUpdate) {
            String valuesStr = originalSqlU.substring(valuesLeftBracketIndex, duplicateKeyUpdateIndex);
            valuesEndIndex = valuesLeftBracketIndex + valuesStr.lastIndexOf(")");
        } else {
            valuesEndIndex = originalSqlU.lastIndexOf(")");
        }

        if (valuesEndIndex == -1) {
            return originalSql;
        }
        String sqlPart1 = originalSql.substring(0, fieldsEndIndex);
        String sqlPart2 = originalSql.substring(fieldsEndIndex, valuesEndIndex);
        String sqlPart3 = originalSql.substring(valuesEndIndex);

        List<String> columnList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        Set<Map.Entry<TableFieldInfo, String>> entries = auditFields.entrySet();
        for (Map.Entry<TableFieldInfo, String> entry : entries) {
            TableFieldInfo fieldInfo = entry.getKey();
            if (fieldInfo == null || !fieldInfo.isWithInsertFill()) {
                continue;
            }
            String column = getColumnIfNotExist(originalSql, fieldInfo);
            if (column == null) {
                continue;
            }
            columnList.add(column);
            valueList.add(String.format("'%s'", entry.getValue()));
        }
        String columnSplit = String.join(", ", columnList);
        String valueSplit = String.join(", ", valueList);

        // 拼接新的SQL语句
        String newSql = sqlPart1 + ", " + columnSplit + sqlPart2 + ", " + valueSplit + sqlPart3;

        // 如果包含ON DUPLICATE KEY UPDATE子句，则在其后面添加审计字段的更新
        if (hasDuplicateKeyUpdate) {
            List<String> columnValueList = new ArrayList<>();
            for (Map.Entry<TableFieldInfo, String> entry : auditFields.entrySet()) {
                TableFieldInfo fieldInfo = entry.getKey();
                if (fieldInfo == null || !fieldInfo.isWithUpdateFill()) {
                    continue;
                }
                String column = getColumnIfNotExist(originalSql, fieldInfo);
                if (column == null) {
                    continue;
                }
                columnValueList.add(column + " = " + String.format("'%s'", entry.getValue()));
            }
            String columnValueSplit = String.join(", ", columnValueList);

            newSql = newSql + ", " + columnValueSplit;
        }

        return newSql;
    }

    /**
     * 为UPDATE语句拼接审计字段
     */
    private static String appendAuditFieldsToUpdate(String originalSql, Map<TableFieldInfo, String> auditFields) {
        if (auditFields.isEmpty()) {
            return originalSql;
        }
        /*
        例：UPDATE `sys_config` SET `value` = '11' WHERE `code` = 2;
        将sql语句分成2段
        1. UPDATE `sys_config` SET `value` = '11'   -> 插入字段名=字段值
        2. WHERE `code` = 2;
        得到：UPDATE `sys_config` SET `value` = '11', `update_time` = '2020-01-01 00:00:00', `update_by` = '1' WHERE `code` = 2;
         */
        String originalSqlU = originalSql.toUpperCase();

        // 找到SET子句的结束位置（WHERE子句开始位置）
        int whereIndex = originalSqlU.indexOf(" WHERE");
        int setEndIndex = whereIndex != -1 ? whereIndex : originalSql.length();

        String sqlPart1 = originalSql.substring(0, setEndIndex);
        String sqlPart2 = originalSql.substring(setEndIndex);

        List<String> columnValueList = new ArrayList<>();
        for (Map.Entry<TableFieldInfo, String> entry : auditFields.entrySet()) {
            TableFieldInfo fieldInfo = entry.getKey();
            if (fieldInfo == null || !fieldInfo.isWithUpdateFill()) {
                continue;
            }
            String column = getColumnIfNotExist(originalSql, fieldInfo);
            if (column == null) {
                continue;
            }
            columnValueList.add(column + " = " + String.format("'%s'", entry.getValue()));
        }
        String columnValueSplit = String.join(", ", columnValueList);

        // 拼接新的SQL语句
        String newSql = sqlPart1 + ", " + columnValueSplit + sqlPart2;
        return newSql;
    }

    private static String getColumnIfNotExist(String originalSql, TableFieldInfo tableFieldInfo) {
        if (tableFieldInfo == null) {
            return null;
        }
        String column = tableFieldInfo.getColumn();
        if (originalSql.contains(column)) {
            return null;
        }
        return column;
    }

    /**
     * 通过 Mapper 全类名获取对应的实体类 Class
     * 
     * @param mapperClassName Mapper 全类名
     * @return 关联的实体类 Class
     */
    public Class<?> getEntityClassByMapperClassName(String mapperClassName) {
        // 1. 根据全类名加载 Mapper 接口的 Class 对象
        Class<?> mapperClass = null;
        try {
            mapperClass = ClassUtils.forName(mapperClassName, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }

        // 2. 解析 Mapper 接口的泛型父接口（BaseMapper<T>）
        Type[] genericInterfaces = mapperClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            // 只处理参数化类型（即带泛型的 BaseMapper<T>）
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
                Type rawType = parameterizedType.getRawType();
                // 判断是否是 BaseMapper 接口
                if (rawType instanceof Class<?> && BaseMapper.class.isAssignableFrom((Class<?>)rawType)) {
                    // 3. 提取泛型参数（T），即实体类 Class
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?>) {
                        return (Class<?>)actualTypeArguments[0];
                    }
                }
            }
        }
        return null;
    }
}
