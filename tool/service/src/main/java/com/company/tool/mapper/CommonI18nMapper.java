package com.company.tool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.tool.entity.CommonI18n;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonI18nMapper extends BaseMapper<CommonI18n> {
    List<CommonI18n> selectByBusinessTypeBusinessidsLocale(@Param("businessType") String businessType,
        @Param("businessTypeIds") List<Integer> businessTypeIds, @Param("locale") String locale);
}