package com.company.user.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.constant.Constants;
import com.company.user.entity.UserOauth;
import com.company.user.entity.UserSource;
import com.company.user.mapper.user.UserOauthMapper;
import com.company.user.mapper.user.UserSourceMapper;

@Component
public class UserSourceService extends ServiceImpl<UserSourceMapper, UserSource> implements IService<UserSource> {

	@Autowired
	private UserOauthMapper userOauthMapper;

	/**
	 * 根据用户ID查询最新用户来源
	 * 
	 * @param userId
	 * @return
	 */
	public UserSource selectLastByUserId(Integer userId) {
		// 通过小程序openid查询
		UserOauth userOauth = userOauthMapper.selectByUserIdIdentityType(userId,
				UserOauthEnum.IdentityType.WX_OPENID_MINIAPP);
		if (userOauth != null) {
			String deviceid = userOauth.getIdentifier();
			UserSource lastUserSource = baseMapper.selectLastByDeviceid(deviceid);
			if (lastUserSource != null) {
				return lastUserSource;
			}
		}

		// 通过APP设备ID查询
		// TODO
		String deviceid = String.valueOf(userId);// 根据userId查询
		if (deviceid != null) {
			UserSource lastUserSource = baseMapper.selectLastByDeviceid(deviceid);
			if (lastUserSource != null) {
				return lastUserSource;
			}
		}

		return null;
	}

	/**
	 * 根据被邀请人用户id查找邀请人用户id
	 * 
	 * @param userId
	 *            用户ID
	 * @param sourcePrefix
	 *            来源前缀
	 * @return
	 */
	public Integer selectInvitorByUserId(Integer inviteeUserId, String sourcePrefix) {
		UserSource lastUserSource = selectLastByUserId(inviteeUserId);
		if (lastUserSource == null) {// 未找到邀请关系
			return null;
		}

		// 获取邀请人信息
		String sourceInvitor = lastUserSource.getSource();
		String uniqueInvitor = sourceInvitor.replaceFirst(sourcePrefix, "");

		// source可能是小程序openid
		UserOauth userOauthInvitor = userOauthMapper
				.selectByIdentityTypeIdentifier(UserOauthEnum.IdentityType.WX_OPENID_MINIAPP, uniqueInvitor);
		if (userOauthInvitor != null) {
			return userOauthInvitor.getUserId();
		}

		// source可能是APP设备ID
		String deviceidInvitor = uniqueInvitor;
		// TODO
		Integer userIdInvitor = Integer.valueOf(deviceidInvitor); // 根据设备ID查询userId
		if (userIdInvitor != null) {
			return userIdInvitor;
		}

		return null;
	}

    @Cacheable(value = Constants.CacheName.USER_SOURCE, key = "#deviceid")
    public UserSource selectLastByDeviceid(String deviceid) {
        return baseMapper.selectLastByDeviceid(deviceid);
    }

    public int saveOrIgnore(String deviceid, String source, LocalDateTime time) {
        return baseMapper.saveOrIgnore(deviceid, source, time);
    }
}
