package com.company.datasource.mybatisplus.plugins;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
 * <p>
 * 审计字段拦截器，用于自动填充审计字段，如创建人、创建时间、更新人、更新时间等
 * </p>
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

        // 仅处理 UPDATE 操作
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (SqlCommandType.INSERT != sqlCommandType && SqlCommandType.UPDATE != sqlCommandType) {
            return;
        }

        Object currentUser = currentUserProvider.currentUser();
        if (currentUser == null) {
            currentUser = DEFAULT_CURRENT_USER_ID;
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

        String originalSql = boundSql.getSql();

        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, TableFieldInfo> propertyThisMap =
            fieldList.stream().collect(Collectors.toMap(TableFieldInfo::getProperty, a -> a, (a, b) -> b));
        if (SqlCommandType.INSERT == sqlCommandType) {
            // 拼接SQL
        } else {
            List<String> appendSqlList = new ArrayList<>();

            TableFieldInfo createByFieldInfo = propertyThisMap.get("createBy");
            if (createByFieldInfo != null) {
                // boolean withInsertFill = createByFieldInfo.isWithInsertFill();
                String column = createByFieldInfo.getColumn();
                if (!originalSql.contains(column)) {
                    appendSqlList.add(String.format("%s = '%s'", column, currentUser));
                }
            }

            TableFieldInfo createTimeFieldInfo = propertyThisMap.get("createTime");
            if (createTimeFieldInfo != null) {
                String column = createTimeFieldInfo.getColumn();
                if (!originalSql.contains(column)) {
                    appendSqlList.add(String.format("%s = '%s'", column, now));
                }
            }

            TableFieldInfo updateByFieldInfo = propertyThisMap.get("updateBy");
            if (updateByFieldInfo != null) {
                String column = updateByFieldInfo.getColumn();
                if (!originalSql.contains(column)) {
                    appendSqlList.add(String.format("%s = '%s'", column, currentUser));
                }
            }

            TableFieldInfo updateTimeFieldInfo = propertyThisMap.get("updateTime");
            if (updateTimeFieldInfo != null) {
                String column = updateTimeFieldInfo.getColumn();
                if (!originalSql.contains(column)) {
                    appendSqlList.add(String.format("%s = '%s'", column, now));
                }
            }

            if (appendSqlList.isEmpty()) {
                return;
            }
            String appendSql = String.join(",", appendSqlList);
            String newSql = originalSql.replaceFirst("where", ", " + appendSql + " where");
            logger.debug("sql change: " + originalSql + " ==> " + newSql);
            PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
            mpBoundSql.sql(newSql);
        }
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
