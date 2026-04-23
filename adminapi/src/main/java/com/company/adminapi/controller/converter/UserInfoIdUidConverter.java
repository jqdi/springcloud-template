package com.company.adminapi.controller.converter;


import com.company.adminapi.converter.ds.ConverterDataSource;
import com.company.framework.context.SpringContextUtil;
import com.company.user.api.feign.UserInfoFeign;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserInfoIdUidConverter implements ConverterDataSource {

	@Override
	public Map<Object, String> getFieldConverterValue(Set<Object> fieldValueSet) {
		UserInfoFeign userInfoFeign = SpringContextUtil.getBean(UserInfoFeign.class);

		Set<Integer> userIdSet = fieldValueSet.stream().map(v -> (Integer) v).collect(Collectors.toSet());
		Map<Integer, String> idNicknameMap = userInfoFeign.mapNicknameById(userIdSet);

		return idNicknameMap.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), Map.Entry::getValue));
	}

}
