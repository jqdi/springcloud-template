package com.company.adminapi.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 审计字段创建人/更新人解析工具类
 *
 * <pre>
 * 审计字段的值由登录设备与用户ID拼接而成，格式为 device:userId
 * </pre>
 */
public class AuditUserUtil {

	/**
	 * 审计字段中设备与用户ID的分隔符
	 */
	private static final String SEPARATOR = ":";

	private AuditUserUtil() {
	}

	/**
	 * 从审计字段值中解析出用户ID
	 *
	 * @param auditBy 审计字段值（device:userId）
	 * @return 用户ID，无法解析时返回null
	 */
	public static Integer parseUserId(Object auditBy) {
		if (auditBy == null) {
			return null;
		}
		if (auditBy instanceof Integer) {
			return (Integer) auditBy;
		}
		if (auditBy instanceof Number) {
			return ((Number) auditBy).intValue();
		}
		String value = String.valueOf(auditBy);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		int index = value.lastIndexOf(SEPARATOR);
		if (index >= 0) {
			value = value.substring(index + SEPARATOR.length());
		}
		value = StringUtils.trimToEmpty(value);
		if (!StringUtils.isNumeric(value)) {
			return null;
		}
		return Integer.valueOf(value);
	}

}
