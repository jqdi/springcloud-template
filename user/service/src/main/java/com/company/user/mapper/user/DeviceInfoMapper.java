package com.company.user.mapper.user;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.user.entity.DeviceInfo;

public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {

	@Insert("insert into device_info(deviceid,app_code,platform,operator,channel,version,requestip,request_user_agent,time)"
			+ " values (#{deviceid},#{appCode},#{platform},#{operator},#{channel},#{version},#{requestip},#{requestUserAgent},#{time})"
			+ " ON DUPLICATE KEY UPDATE"
			+ " platform = #{platform},operator = #{operator},channel = #{channel},version = #{version},requestip = #{requestip},request_user_agent = #{requestUserAgent},time = #{time}")
	int saveOrUpdate(@Param("deviceid") String deviceid, @Param("appCode") String appCode, @Param("platform") String platform,
				@Param("operator") String operator, @Param("channel") String channel, @Param("version") String version, @Param("requestip") String requestip, @Param("requestUserAgent") String requestUserAgent, @Param("time") LocalDateTime time);

	@Select("select * from device_info where deviceid = #{deviceid} and app_code = #{appCode}")
	DeviceInfo selectByDeviceidAppCode(@Param("deviceid") String deviceid, @Param("appCode") String appCode);

    /**
     * 按应用统计设备数量
     */
    @Select("select count(1) from device_info where app_code = #{appCode}")
    Long countByAppCode(@Param("appCode") String appCode);

	/**
	 * 按主键游标分页查询设备（仅取id、version，用于版本统计）
	 */
	@Select("select id, version from device_info where app_code = #{appCode} and id > #{minId} order by id asc limit #{limit}")
	List<DeviceInfo> pageByAppCodeId(@Param("appCode") String appCode, @Param("minId") Integer minId, @Param("limit") Integer limit);
}
