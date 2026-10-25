package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("common_i18n")
public class CommonI18n extends AuditableModel<CommonI18n> {
    private Integer id;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private Integer businessId;

    /**
     * 国际化语言编码
     */
    private String locale;

    /**
     * 文案
     */
    private String text;

    /**
     * 国际化文案
     */
    private String i18nText;

    private String remark;
}
