package com.company.framework.message;

/**
 * 消息文案解析端口（表存储抽象）。
 *
 * <p>框架层（framework）不直接依赖数据访问层，由下游模块（如 tool-service）提供具体实现，
 * 将「消息编码 + locale」映射到 common_i18n 表中存储的翻译文案。
 *
 * <p>未提供实现时，{@code MysqlMessage} 会平滑降级到 Spring {@code MessageSource}（messages*.properties）。
 *
 * @author JQ棣
 */
public interface MessageResolver {

	/** 全局/系统消息在 common_i18n 中的 business_id 占位值（不绑定具体业务实体行） */
	int GLOBAL_BUSINESS_ID = 0;

	/**
	 * 根据消息编码与 locale 查询翻译文案。
	 *
	 * @param code      消息编码（对应 common_i18n.business_type）
	 * @param localeTag 地区编码，如 zh-CN、en-US（{@link java.util.Locale#toLanguageTag()}）
	 * @return 命中的国际化文案；未命中返回 null
	 */
	String resolve(String code, String localeTag);
}
