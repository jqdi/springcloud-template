package com.company.adminapi.controller.converter;


import com.company.adminapi.converter.ds.ConverterDataSource;
import com.company.adminapi.feign.client.SysUserFeign;
import com.company.adminapi.util.AuditUserUtil;
import com.company.framework.context.SpringContextUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 系统用户（id转nickname），兼容审计字段的 device:userId 格式
 */
public class SysUserIdNicknameConverter implements ConverterDataSource {

	@Override
	public Map<Object, String> getFieldConverterValue(Set<Object> fieldValueSet) {
		Map<Object, Integer> fieldValueIdMap = new HashMap<>();
		Set<Integer> sysUserIdSet = new HashSet<>();
		for (Object fieldValue : fieldValueSet) {
			Integer sysUserId = AuditUserUtil.parseUserId(fieldValue);
			if (sysUserId == null) {
				continue;
			}
			fieldValueIdMap.put(fieldValue, sysUserId);
			sysUserIdSet.add(sysUserId);
		}
		if (sysUserIdSet.isEmpty()) {
			return Collections.emptyMap();
		}

		SysUserFeign sysUserFeign = SpringContextUtil.getBean(SysUserFeign.class);
		Map<Integer, String> idNicknameMap = sysUserFeign.mapNicknameById(sysUserIdSet);
		if (idNicknameMap == null) {
			return Collections.emptyMap();
		}

		Map<Object, String> fieldConverterMap = new HashMap<>();
		for (Map.Entry<Object, Integer> entry : fieldValueIdMap.entrySet()) {
			String nickname = idNicknameMap.get(entry.getValue());
			if (nickname == null) {
				continue;
			}
			fieldConverterMap.put(entry.getKey(), nickname);
		}
		return fieldConverterMap;
	}

}
