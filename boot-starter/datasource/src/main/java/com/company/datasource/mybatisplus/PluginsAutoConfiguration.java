package com.company.datasource.mybatisplus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.company.datasource.mybatis.i18n.plugin.I18nFieldValueReplaceInterceptor;
import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.datasource.mybatis.i18n.provider.I18nDataProviderFactory;
import com.company.datasource.mybatis.i18n.spring.JdbcTemplateI18nDataProvider;
import com.company.datasource.mybatis.i18n.spring.SpringI18nDataProviderFactory;
import com.company.datasource.mybatisplus.plugins.PerformanceInterceptor;
import com.company.datasource.mybatisplus.plugins.SqlLimitInterceptor;
import com.company.datasource.mybatisplus.plugins.SummarySQLInterceptor;

//@Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
public class PluginsAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(@Value("${template.sqllimit.max:0}") Integer limit) {
		MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
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

    /* 国际化字段替换 */
    @Bean
    public I18nDataProvider defaultI18nDataProvider(JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateI18nDataProvider(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public I18nDataProviderFactory i18nDataProviderFactory(I18nDataProvider defaultI18nDataProvider) {
        return new SpringI18nDataProviderFactory(defaultI18nDataProvider);
    }

    @Bean
    public I18nFieldValueReplaceInterceptor i18nFieldValueReplaceInterceptor(I18nDataProviderFactory i18nDataProviderFactory) {
        return new I18nFieldValueReplaceInterceptor(i18nDataProviderFactory);
    }
    /* 国际化字段替换 */
}
