package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatis.i18n.annotation.I18nField;
import com.company.tool.i18n.CommonI18nDataProvider;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("banner")
public class Banner {
	private Integer id;

	/**
	 * 有效期开始时间
	 */
	private LocalDateTime beginTime;

	/**
	 * 有效期结束时间
	 */
	private LocalDateTime endTime;

	/**
	 * 状态(off:下架,on:上架)
	 */
	private String status;

	/**
	 * 优先级(值越大，优先级越高)
	 */
	private Integer priority;

	/**
     * 标题
     */
//    @I18nField(i18nTable = "banner_i18n", i18nColumn = "title", i18nRelatedColumn = "banner_id", relatedValueFromField = "id") // 独立国际化表方式
    @I18nField(i18nTable = "banner_i18n", i18nColumn = "title", i18nRelatedColumn = "banner_id", relatedValueFromField = "id", i18nDataProvider = CommonI18nDataProvider.class) // 统一国际化表方式
    private String title;

	/**
	 * 图片
	 */
	private String image;

	/**
	 * 类型(redirect_http:跳转http链接,redirect_mini:跳转小程序链接,redirect_other_mini:跳转其他小程序链接)
	 */
	private String type;

	/**
	 * 类型值
	 */
	private String value;

	private String remark;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
}
