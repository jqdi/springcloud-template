package com.company.datasource.mybatis.i18n.spring;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;

import com.company.datasource.mybatis.i18n.metadata.I18nFieldInfo;
import com.company.datasource.mybatis.i18n.metadata.RelatedI18nValueMapping;
import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;

/**
 * 基于JdbcTemplate的国际化数据提供者
 *
 * @author JQ棣
 */
public class JdbcTemplateI18nDataProvider implements I18nDataProvider {
    /*
     * 构建查询sql：select {i18nRelatedColumn},{i18nColumn} from {i18nTable} where {i18nRelatedColumn} in (?,?,...) and {i18nLocaleColumn} = ?
     */
    private static final String SQL_PATTERN = "select {i18nRelatedColumn},{i18nColumn}"
            + " from {i18nTable}"
            + " where {i18nRelatedColumn} in ({relatedFieldPlaceholders})"
            + " and {i18nLocaleColumn} = ?";

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateI18nDataProvider() {}

    public JdbcTemplateI18nDataProvider(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RelatedI18nValueMapping> getValueMapping(I18nFieldInfo i18nFieldInfo, Set<Object> relatedFieldValueSet) {
        String i18nTable = i18nFieldInfo.getI18nTable();
        String i18nColumn = i18nFieldInfo.getI18nColumn();
        String i18nRelatedColumn = i18nFieldInfo.getI18nRelatedColumn();
        String i18nLocaleColumn = i18nFieldInfo.getI18nLocaleColumn();

        String relatedFieldPlaceholders = String.join(",", Collections.nCopies(relatedFieldValueSet.size(), "?"));

        String i18nQuerySql = SQL_PATTERN
                .replace("{i18nRelatedColumn}", i18nRelatedColumn)
                .replace("{i18nColumn}", i18nColumn)
                .replace("{i18nTable}", i18nTable)
                .replace("{relatedFieldPlaceholders}", relatedFieldPlaceholders)
                .replace("{i18nLocaleColumn}", i18nLocaleColumn)
        ;

        List<Object> argList = new ArrayList<>(relatedFieldValueSet);
        argList.add(LocaleContextHolder.getLocale().toLanguageTag());
        List<Map<String, Object>> i18nEntityList = jdbcTemplate.queryForList(i18nQuerySql, argList.toArray());
        return i18nEntityList.stream().map(v -> new RelatedI18nValueMapping(v.get(i18nRelatedColumn), v.get(i18nColumn)))
            .collect(Collectors.toList());
    }
}
