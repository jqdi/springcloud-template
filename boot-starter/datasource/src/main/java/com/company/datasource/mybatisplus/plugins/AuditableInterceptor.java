package com.company.datasource.mybatisplus.plugins;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.company.datasource.CurrentUserProvider;

/**
 * 审计字段Mybatis Plus拦截器，用于自动填充审计字段（创建人、创建时间、更新人、更新时间）
 * 
 * <pre>
 * 与AuditableMetaObjectHandler不同的地方是：该拦截器可处理非 BaseMapper API执行的SQL
 * </pre>
 * 
 * @see com.company.datasource.mybatisplus.handlers.AuditableMetaObjectHandler
 * @author JQ棣
 */
public class AuditableInterceptor implements InnerInterceptor {
    private static final Log logger = LogFactory.getLog(AuditableInterceptor.class);

    private static final String DEFAULT_CURRENT_USER_ID = "0";

    private final CurrentUserProvider currentUserProvider;

    public AuditableInterceptor(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
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
        String className = resource.substring(0, index).replace("/", ".");

        Class<?> entityClass = getEntityClassByMapperClassName(className);
        if (entityClass == null) {
            return;
        }

        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        if (tableInfo == null) {
            return;
        }

        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, TableFieldInfo> propertyThisMap =
            fieldList.stream().collect(Collectors.toMap(TableFieldInfo::getProperty, a -> a, (a, b) -> b));

        if (SqlCommandType.INSERT == sqlCommandType) {
            insertFill(boundSql, tableInfo, propertyThisMap);
        } else {
            updateFill(boundSql, tableInfo, propertyThisMap);
        }
    }

    protected void insertFill(BoundSql boundSql, TableInfo tableInfo, Map<String, TableFieldInfo> propertyThisMap) {
        if (!tableInfo.isWithInsertFill()) {
            return;
        }

        Object currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            currentUser = DEFAULT_CURRENT_USER_ID;
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String originalSql = boundSql.getSql();

        Map<String, Object> auditFieldMap = new HashMap<>();
        TableFieldInfo createByFieldInfo = propertyThisMap.get("createBy");
        if (createByFieldInfo != null && createByFieldInfo.isWithInsertFill()) {
            String createByColumn = getColumnIfNotExist(originalSql, createByFieldInfo);
            if (createByColumn != null) {
                auditFieldMap.put(createByColumn, currentUser);
            }
        }

        TableFieldInfo createTimeFieldInfo = propertyThisMap.get("createTime");
        if (createTimeFieldInfo != null && createTimeFieldInfo.isWithInsertFill()) {
            String createTimeColumn = getColumnIfNotExist(originalSql, createTimeFieldInfo);
            if (createTimeFieldInfo.isWithInsertFill() && createTimeColumn != null) {
                auditFieldMap.put(createTimeColumn, now);
            }
        }

        TableFieldInfo updateByFieldInfo = propertyThisMap.get("updateBy");
        if (updateByFieldInfo != null && updateByFieldInfo.isWithInsertFill()) {
            String updateByColumn = getColumnIfNotExist(originalSql, updateByFieldInfo);
            if (updateByColumn != null) {
                auditFieldMap.put(updateByColumn, currentUser);
            }
        }

        TableFieldInfo updateTimeFieldInfo = propertyThisMap.get("updateTime");
        if (updateTimeFieldInfo != null && updateTimeFieldInfo.isWithInsertFill()) {
            String updateTimeColumn = getColumnIfNotExist(originalSql, updateTimeFieldInfo);
            if (updateTimeColumn != null) {
                auditFieldMap.put(updateTimeColumn, now);
            }
        }

        if (auditFieldMap.isEmpty()) {
            return;
        }
        String newSql = appendAuditFieldsToInsert(originalSql, auditFieldMap);
        logger.debug("sql change: " + originalSql + " ==> " + newSql);
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(newSql);
    }

    protected void updateFill(BoundSql boundSql, TableInfo tableInfo, Map<String, TableFieldInfo> propertyThisMap) {
        if (!tableInfo.isWithUpdateFill()) {
            return;
        }

        Object currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            currentUser = DEFAULT_CURRENT_USER_ID;
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String originalSql = boundSql.getSql();

        Map<String, Object> auditFieldMap = new HashMap<>();

        TableFieldInfo updateByFieldInfo = propertyThisMap.get("updateBy");
        if (updateByFieldInfo != null && updateByFieldInfo.isWithUpdateFill()) {
            String updateByColumn = getColumnIfNotExist(originalSql, updateByFieldInfo);
            if (updateByColumn != null) {
                auditFieldMap.put(updateByColumn, currentUser);
            }
        }

        TableFieldInfo updateTimeFieldInfo = propertyThisMap.get("updateTime");
        if (updateTimeFieldInfo != null && updateTimeFieldInfo.isWithUpdateFill()) {
            String updateTimeColumn = getColumnIfNotExist(originalSql, updateTimeFieldInfo);
            if (updateTimeColumn != null) {
                auditFieldMap.put(updateTimeColumn, now);
            }
        }

        if (auditFieldMap.isEmpty()) {
            return;
        }
        String newSql = appendAuditFieldsToUpdate(originalSql, auditFieldMap);
        logger.debug("sql change: " + originalSql + " ==> " + newSql);
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(newSql);
    }

    private String getColumnIfNotExist(String originalSql, TableFieldInfo tableFieldInfo) {
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
     * 为INSERT语句拼接审计字段
     */
    private static String appendAuditFieldsToInsert(String originalSql, Map<String, Object> auditFields) {
        String originalSqlU = originalSql.toUpperCase();
        // 找到字段列表的结束位置（第一个右括号）
        int fieldsEndIndex = originalSqlU.indexOf(")");
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
        int valuesEndIndex = originalSqlU.indexOf(")", valuesLeftBracketIndex);
        if (valuesEndIndex == -1) {
            return originalSql;
        }

        // 构建新的字段列表
        StringBuilder newFields = new StringBuilder(originalSql.substring(0, fieldsEndIndex));
        for (String field : auditFields.keySet()) {
            newFields.append(",").append(field);
        }
        newFields.append(")");

        // 构建新的值列表
        StringBuilder newValues = new StringBuilder(originalSql.substring(valuesLeftBracketIndex, valuesEndIndex));
        for (Object value : auditFields.values()) {
            newValues.append(",").append(formatValue(value));
        }
        newValues.append(")");

        // 拼接新的SQL语句
        StringBuilder newSql = new StringBuilder();
        newSql.append(newFields.toString())
                .append(originalSql.substring(fieldsEndIndex, valuesLeftBracketIndex))
                .append(newValues.toString());

        // 检测是否包含ON DUPLICATE KEY UPDATE子句
        int duplicateKeyUpdateIndex = originalSqlU.indexOf("ON DUPLICATE KEY UPDATE");
        boolean hasDuplicateKeyUpdate = duplicateKeyUpdateIndex != -1;

        // 如果包含ON DUPLICATE KEY UPDATE子句，则在其后面添加审计字段的更新
        if (hasDuplicateKeyUpdate) {
            // 获取ON DUPLICATE KEY UPDATE子句的内容
            String duplicateKeyUpdateClause = originalSql.substring(duplicateKeyUpdateIndex);

            // 构建审计字段的更新内容
            StringBuilder auditUpdateClause = new StringBuilder();
            for (Map.Entry<String, Object> entry : auditFields.entrySet()) {
                auditUpdateClause.append(",").append(entry.getKey()).append("=").append(formatValue(entry.getValue()));
            }

            // 如果更新子句不是以逗号结尾，则添加逗号
//            if (!duplicateKeyUpdateClause.trim().endsWith(",")) {
//                auditUpdateClause.insert(0, ",");
//            }

            // 将审计字段的更新内容添加到ON DUPLICATE KEY UPDATE子句中
            newSql.append(duplicateKeyUpdateClause).append(auditUpdateClause.toString());
        }

        return newSql.toString();
    }

    /**
     * 为UPDATE语句拼接审计字段
     */
    private static String appendAuditFieldsToUpdate(String originalSql, Map<String, Object> auditFields) {
        // 找到SET子句的结束位置（WHERE子句开始位置）
        int whereIndex = originalSql.toUpperCase().indexOf("WHERE");
        int setEndIndex = whereIndex != -1 ? whereIndex : originalSql.length();

        // 构建新的SET子句
        StringBuilder newSetClause = new StringBuilder(originalSql.substring(0, setEndIndex).trim());
        if (!newSetClause.toString().endsWith(",")) {
            newSetClause.append(",");
        }

        // 添加审计字段
        for (Map.Entry<String, Object> entry : auditFields.entrySet()) {
            newSetClause.append(entry.getKey()).append("=").append(formatValue(entry.getValue())).append(",");
        }

        // 移除最后一个逗号
        if (newSetClause.toString().endsWith(",")) {
            newSetClause.setLength(newSetClause.length() - 1);
        }

        // 拼接新的SQL语句
        return newSetClause.toString() + " " + (whereIndex != -1 ? originalSql.substring(whereIndex) : "");
    }

    /**
     * 格式化值，处理不同类型的值
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof java.util.Date) {
            return "'" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value) + "'";
        }
        if (value instanceof java.time.LocalDateTime) {
            return "'" + value.toString().replace("T", " ") + "'";
        }
        return "'" + value.toString().replace("'", "''") + "'";
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
