package com.company.user.mapper.user;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.user.api.enums.UserClientEnum;
import com.company.user.entity.UserClient;

public interface UserClientMapper extends BaseMapper<UserClient> {
    @Select("select * from user_client where user_id = #{userId} and identity_type = #{clientType.code}")
    UserClient selectByUserIdClientType(@Param("userId") Integer userId, @Param("clientType") UserClientEnum.ClientType clientType);
}
