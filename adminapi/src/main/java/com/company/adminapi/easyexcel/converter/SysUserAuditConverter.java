package com.company.adminapi.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.company.adminapi.cache.SysUserCache;
import com.company.adminapi.util.AuditUserUtil;
import com.company.framework.context.SpringContextUtil;
import com.company.system.api.response.SysUserResp;

import org.apache.commons.lang3.StringUtils;

/**
 * 审计字段创建人/更新人（device:userId转nickname）
 */
public class SysUserAuditConverter implements Converter<String> {

	@Override
	public Class<?> supportJavaTypeKey() {
		return String.class;
	}

	@Override
	public CellDataTypeEnum supportExcelTypeKey() {
		return CellDataTypeEnum.STRING;
	}

	@Override
	public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) {
		return cellData.getStringValue();
	}

	@Override
	public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
		String auditBy = context.getValue();
		if (StringUtils.isBlank(auditBy)) {
			return new WriteCellData<>(StringUtils.EMPTY);
		}
		Integer sysUserId = AuditUserUtil.parseUserId(auditBy);
		if (sysUserId == null) {
			return new WriteCellData<>(auditBy);
		}
		SysUserCache sysUserCache = SpringContextUtil.getBean(SysUserCache.class);
		SysUserResp sysUserResp = sysUserCache.getById(sysUserId);
		if (sysUserResp == null || sysUserResp.getNickname() == null) {
			return new WriteCellData<>(auditBy);
		}
		return new WriteCellData<>(sysUserResp.getNickname());
	}

}
