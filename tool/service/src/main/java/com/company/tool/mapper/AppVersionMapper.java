package com.company.tool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.tool.entity.AppVersion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AppVersionMapper extends BaseMapper<AppVersion> {
    @Select("SELECT * FROM app_version WHERE app_code = #{appCode} and release_time <= now() ORDER BY id DESC")
    AppVersion selectLastByAppCode(@Param("appCode") String appCode);
}