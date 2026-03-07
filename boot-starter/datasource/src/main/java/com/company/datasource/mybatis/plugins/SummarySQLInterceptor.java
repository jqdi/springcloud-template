package com.company.datasource.mybatis.plugins;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

/**
 * 打印SQL耗时
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SummarySQLInterceptor implements Interceptor {
    private static final Log logger = LogFactory.getLog(SummarySQLInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String sql = null;
        try {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            sql = boundSql.getSql();
        } catch (Exception e) {
            logger.error("获取sql语句失败", e);
            sql = "获取sql语句失败";
        }
        // 计算执行 SQL 耗时
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long timing = System.currentTimeMillis() - start;
        logger.debug("|" + sql + "|" + timing);
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
    }
}
