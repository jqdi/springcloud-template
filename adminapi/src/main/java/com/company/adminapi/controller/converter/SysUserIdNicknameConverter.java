package com.company.adminapi.controller.converter;


import com.company.adminapi.converter.ds.ConverterDataSource;
import com.company.framework.context.SpringContextUtil;
import com.company.system.api.feign.SysUserFeign;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SysUserIdNicknameConverter implements ConverterDataSource {

	@Override
	public Map<Object, String> getFieldConverterValue(Set<Object> fieldValueSet) {
		SysUserFeign sysUserFeign = SpringContextUtil.getBean(SysUserFeign.class);

		Set<Integer> sysUserIdSet = fieldValueSet.stream().map(v -> (Integer) v).collect(Collectors.toSet());
		Map<Integer, String> idNicknameMap = sysUserFeign.mapNicknameById(sysUserIdSet);

		return idNicknameMap.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), Map.Entry::getValue));
	}

}
