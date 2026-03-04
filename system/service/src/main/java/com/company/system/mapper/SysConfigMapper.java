package com.company.system.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.system.entity.SysConfig;

public interface SysConfigMapper extends BaseMapper<SysConfig> {

	@Select("select value from sys_config where code = #{code}")
	String getValueByCode(@Param("code") String code);

	@Update("update sys_config set value = #{value} where code = #{code}")
	Integer updateValueByCode(@Param("value") String value, @Param("code") String code);

    @Insert("INSERT INTO sys_config(`code`, `value`) VALUES (#{code}, #{value}) on duplicate key update `value` = #{value}")
    Integer insertConfig(@Param("value") String value, @Param("code") String code);

}