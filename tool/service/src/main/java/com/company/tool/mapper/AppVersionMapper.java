package com.company.tool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.tool.entity.AppVersion;

public interface AppVersionMapper extends BaseMapper<AppVersion> {
    @Select("SELECT * FROM app_version WHERE app_code = #{appCode} and release_time <= now() and gray_percent >=1 ORDER BY id DESC LIMIT 1")
    AppVersion selectLastFullByAppCode(@Param("appCode") String appCode);

    @Select("SELECT * FROM app_version WHERE app_code = #{appCode} and release_time <= now() and id >= #{fullId} ORDER BY id DESC")
    List<AppVersion> selectByAppCodeGeId(@Param("appCode") String appCode, @Param("fullId") Integer fullId);
}
