package com.company.datasource.mybatisplus.plugins;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;

/**
 * 给没有添加limit的SQL添加limit，防止全量查询导致慢SQL
 *
 * @author JQ棣
 */
@SuppressWarnings({"rawtypes"})
public class SqlLimitInterceptor implements InnerInterceptor {
    private static final Log logger = LogFactory.getLog(SqlLimitInterceptor.class);

    /**
     * 最大条数限制
     */
    protected long maxLimit;
    
	public SqlLimitInterceptor(long maxLimit) {
		this.maxLimit = maxLimit;
	}

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		if (maxLimit <= 0) {
			return;
		}
		
        String buildSql = boundSql.getSql();
		if (buildSql.contains(" limit ") || buildSql.contains(" LIMIT ")) {
			return;
		}

        IDialect dialect = findIDialect(executor);
        
        final Configuration configuration = ms.getConfiguration();
        DialectModel model = dialect.buildPaginationSql(buildSql, 0, maxLimit);
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);

        List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
        Map<String, Object> additionalParameter = mpBoundSql.additionalParameters();
        model.consumers(mappings, configuration, additionalParameter);
        mpBoundSql.sql(model.getDialectSql());
		mpBoundSql.parameterMappings(mappings);
        logger.debug(ms.getId() + ",sql has added limit " + maxLimit);
	}

    /**
     * 获取分页方言类的逻辑
     *
     * @param executor Executor
     * @return 分页方言类
     */
    protected IDialect findIDialect(Executor executor) {
        return DialectFactory.getDialect(JdbcUtils.getDbType(executor));
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
