package com.company.user.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.lock.LockClient;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.util.PropertyUtils;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserInfoFeign;
import com.company.user.api.request.UserInfoReq;
import com.company.user.api.response.UserInfoResp;
import com.company.user.entity.UserInfo;
import com.company.user.entity.UserOauth;
import com.company.user.mapper.user.UserInfoMapper;
import com.company.user.mapper.user.UserOauthMapper;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/userinfo")
public class UserInfoController implements UserInfoFeign {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserOauthMapper userOauthMapper;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private LockClient lockClient;

    @Value("${newuser.default.nickname:}")
    private String defaultNickname;
    @Value("${newuser.default.avatar:}")
    private String defaultAvatar;

    /**
     * <pre>
     * 1.如果账号存在，直接返回已绑定的用户ID
     * 2.如果账号不存在，新增用户并绑定账号，返回新增用户ID
     * </pre>
     */
    @Override
    public UserInfoResp findOrCreateUser(@RequestBody @Valid UserInfoReq userInfoReq) {
        UserOauthEnum.IdentityType identityType = userInfoReq.getIdentityType();
        String identifier = userInfoReq.getIdentifier();

        UserOauth userOauthDB = userOauthMapper.selectByIdentityTypeIdentifier(identityType, identifier);
        if (userOauthDB != null) {
            UserInfoResp userInfoResp = new UserInfoResp().setId(userOauthDB.getUserId());
            return userInfoResp;
        }

        String key = String.format("lock:register:%s", identifier);
        Integer userId0 = lockClient.doInLock(key, () -> {
            UserOauth userOauth = userOauthMapper.selectByIdentityTypeIdentifier(identityType, identifier);
            if (userOauth != null) {
                return userOauth.getUserId();
            }

            String nickname = Optional.ofNullable(userInfoReq.getNickname()).orElse(defaultNickname);
            String avatar = Optional.ofNullable(userInfoReq.getNickname()).orElse(defaultAvatar);

            UserInfo userInfo = new UserInfo().setNickname(nickname).setAvatar(avatar);
            userInfoMapper.insert(userInfo);

            // userOauthMapper.bindOauth(userInfo.getId(), identityType, identifier, userInfoReq.getCertificate());
            userOauth = new UserOauth().setUserId(userInfo.getId()).setIdentityType(identityType.getCode())
                .setIdentifier(identifier).setCertificate(userInfoReq.getCertificate());
            userOauthMapper.insert(userOauth);
            if (StringUtils.isNotBlank(userInfoReq.getPassword())) {
                UserOauth userOauthPassword =
                    new UserOauth().setUserId(userInfo.getId()).setIdentityType(UserOauthEnum.IdentityType.PASSWORD.getCode())
                        .setIdentifier(userInfo.getId().toString()).setCertificate(userInfoReq.getPassword());
                userOauthMapper.insert(userOauthPassword);
            }

            // 发布注册事件
            Map<String, Object> params = Maps.newHashMap();
            params.put("userId", userInfo.getId());
            params.put("identityType", identityType.getCode());
            params.put("identifier", identifier);
            params.put("nickname", userInfo.getNickname());
            params.put("avatar", userInfo.getAvatar());
            params.put("httpContextHeader", HeaderContextUtil.httpContextHeader());
            messageSender.sendBroadcastMessage(params, BroadcastConstants.USER_REGISTER.EXCHANGE);

            return userInfo.getId();
        });

        UserInfoResp userInfoResp = new UserInfoResp().setId(userId0);

        return userInfoResp;
    }

    @Override
    public UserInfoResp getById(Integer id) {
        UserInfo userInfo = userInfoMapper.getById(id);
        return PropertyUtils.copyProperties(userInfo, UserInfoResp.class);
    }

    @Override
    public Map<Integer, String> mapNicknameById(@RequestBody Collection<Integer> idList) {
        List<UserInfo> userInfoList = userInfoMapper.selectBatchIds(idList);
        Map<Integer, String> idNicknameMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getNickname));
        return idNicknameMap;
    }
}
