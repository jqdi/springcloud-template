package com.company.tool.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.message.MessageResolver;
import com.company.tool.entity.CommonI18n;
import com.company.tool.service.CommonI18nService;

/**
 * 基于 common_i18n 表的消息解析器。
 *
 * <p>将消息编码 code 作为 {@code business_type}、{@code business_id} 固定为
 * {@link MessageResolver#GLOBAL_BUSINESS_ID}（全局系统消息），按 locale 查询 {@code i18n_text}。
 *
 * <p>与 {@link CommonI18nDataProvider} 的区别：后者面向「业务实体字段」级翻译（按表/字段/实体ID批量扫描），
 * 本解析器面向「消息编码」级翻译（如 test.hello、test.hello.name），供 {@code MysqlMessage} 使用。
 *
 * @author JQ棣
 */
@Component
public class CommonI18nMessageResolver implements MessageResolver {

	@Autowired
	private CommonI18nService commonI18nService;

	@Override
	public String resolve(String code, String localeTag) {
		CommonI18n row = commonI18nService.selectByBusinessTypeBusinessIdLocale(
				code, GLOBAL_BUSINESS_ID, localeTag);
		return row == null ? null : row.getI18nText();
	}
}
