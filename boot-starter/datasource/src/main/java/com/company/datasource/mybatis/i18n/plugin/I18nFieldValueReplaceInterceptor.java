package com.company.datasource.mybatis.i18n.plugin;

import java.sql.Statement;
import java.util.*;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import com.company.datasource.mybatis.i18n.metadata.I18nFieldInfo;
import com.company.datasource.mybatis.i18n.metadata.RelatedI18nValueMapping;
import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.datasource.mybatis.i18n.provider.I18nDataProviderFactory;
import com.company.datasource.mybatis.i18n.toolkit.CollectionUtils;

/**
 * 国际化字段替换拦截器
 *
 * @author JQ棣
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})})
public class I18nFieldValueReplaceInterceptor implements Interceptor {
    private static final Log logger = LogFactory.getLog(I18nFieldValueReplaceInterceptor.class);

    private final I18nReplaceHandler i18nReplaceHandler = new I18nReplaceHandler();

    private final I18nDataProviderFactory i18nDataProviderFactory;

    public I18nFieldValueReplaceInterceptor(I18nDataProviderFactory i18nDataProviderFactory) {
        this.i18nDataProviderFactory = i18nDataProviderFactory;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (result == null) {
            return null;
        }

        try {
            if (result instanceof Collection) {
                Collection<?> list = (Collection<?>)result;
                if (CollectionUtils.isEmpty(list)) {
                    return result;
                }
                Class<?> entityClass = list.iterator().next().getClass();
                replaceI18nFieldValue(entityClass, list);
            } else {
                Class<?> entityClass = result.getClass();
                replaceI18nFieldValue(entityClass, Collections.singletonList(result));
            }
        } catch (Exception e) {
            logger.error("handleI18n error : ", e);
        }
        return result;
    }

    private void replaceI18nFieldValue(Class<?> entityClass, Collection<?> list) {
        List<I18nFieldInfo> i18nFieldInfoList = i18nReplaceHandler.parserI18nMetadata(entityClass);
        if (CollectionUtils.isEmpty(i18nFieldInfoList)) {
            return;
        }
        logger.debug(entityClass.getName() + " i18nMetadata size: " + i18nFieldInfoList.size());

        // 遍历所有的注解字段
        for (I18nFieldInfo i18nFieldInfo : i18nFieldInfoList) {
            Set<Object> relatedFieldValueSet = i18nReplaceHandler.collectRelatedFieldValue(list, i18nFieldInfo);
            if (CollectionUtils.isEmpty(relatedFieldValueSet)) {
                // 关联字段没有值，说明一定查不到国际化字段数据，跳过
                continue;
            }

            I18nDataProvider i18nDataProvider = i18nDataProviderFactory.newInstance(i18nFieldInfo.getI18nDataProvider());
            // 批量查询国际化数据
            List<RelatedI18nValueMapping> valueMappingList =
                i18nDataProvider.getValueMapping(i18nFieldInfo, relatedFieldValueSet);
            logger.debug(entityClass.getName() + " valueMappingList size: " + valueMappingList.size());
            if (CollectionUtils.isEmpty(valueMappingList)) {
                // 查不到国际化字段数据，跳过
                continue;
            }

            i18nReplaceHandler.replaceI18nFieldValue(list, i18nFieldInfo, valueMappingList);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以配置属性
    }
}
