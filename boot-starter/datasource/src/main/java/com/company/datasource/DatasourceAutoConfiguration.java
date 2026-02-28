package com.company.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.company.datasource.mybatis.plugins.PerformanceInterceptor;
import com.company.datasource.mybatis.plugins.SummarySQLInterceptor;
import com.company.datasource.mybatisplus.handlers.AuditableMetaObjectHandler;
import com.company.datasource.mybatisplus.plugins.AuditableInterceptor;
import com.company.datasource.mybatisplus.plugins.SqlLimitInterceptor;

//@Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
public class DatasourceAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(@Autowired(required = false) CurrentUserProvider currentUserProvider,
        @Value("${template.sqllimit.max:0}") Integer limit) {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        // 审计字段拦截器（mybatis 方案）
        if (currentUserProvider != null) {
            mybatisPlusInterceptor.addInnerInterceptor(new AuditableInterceptor(currentUserProvider));
        }

        // 分页拦截器
		mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());

		// 给没有添加limit的SQL添加limit，防止全量查询导致慢SQL
		if (limit > 0) {
			mybatisPlusInterceptor.addInnerInterceptor(new SqlLimitInterceptor(limit));
		}

        return mybatisPlusInterceptor;
	}

	/**
	 * <pre>
	 * 性能分析拦截器，用于输出每条 SQL 语句及其执行时间
	 *
	 * 结合logback-spring.xml
	 * <logger name="com.company.database.mybatisplus.plugins.PerformanceInterceptor" level="DEBUG" additivity="false">
	 * 输出日志
	 * </pre>
	 */
	@Bean
	public PerformanceInterceptor performanceInterceptor() {
		PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
		performanceInterceptor.setWriteInLog(true);
		return performanceInterceptor;
	}

	/**
	 * <pre>
	 * 打印SQL耗时
	 *
	 * 结合logback-conf-summary.xml
	 * LOG_SUMMARY_SQL
	 * 输出日志
	 * </pre>
	 */
//	@Bean // 待启用，用于监控采集SQL耗时
	public SummarySQLInterceptor summarySQLInterceptor() {
		return new SummarySQLInterceptor();
	}

    /**
     * <pre>
     * 自动填充审计字段（创建人、更新人、创建时间、更新时间）
     * （mybatis plus方案）
     * </pre>
     *
     * @param currentUserProvider
     * @return
     */
    @Bean
    @ConditionalOnBean(CurrentUserProvider.class)
    public AuditableMetaObjectHandler auditableMetaObjectHandler(CurrentUserProvider currentUserProvider) {
        return new AuditableMetaObjectHandler(currentUserProvider);
    }
}
