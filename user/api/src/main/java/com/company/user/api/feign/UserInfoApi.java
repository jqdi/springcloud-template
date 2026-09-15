package com.company.user.api.feign;


import com.company.user.api.request.UserInfoReq;
import com.company.user.api.response.UserInfoResp;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

public interface UserInfoApi {

	@RequestMapping("/findOrCreateUser")
	UserInfoResp findOrCreateUser(@RequestBody UserInfoReq userInfoReq);

	@RequestMapping("/getById")
	UserInfoResp getById(@RequestParam("id") Integer id);

	@RequestMapping("/mapNicknameById")
	Map<Integer, String> mapNicknameById(@RequestBody Collection<Integer> idList);
}