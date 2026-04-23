package com.company.adminapi.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.NumberUtils;
import com.company.adminapi.cache.UserInfoCache;
import com.company.framework.context.SpringContextUtil;
import com.company.user.api.response.UserInfoResp;

/**
 * C端用户（id转nickname）
 */
public class UserInfoConverter implements Converter<Integer> {

	@Override
	public Class<?> supportJavaTypeKey() {
		return Integer.class;
	}

	@Override
	public CellDataTypeEnum supportExcelTypeKey() {
		return CellDataTypeEnum.STRING;
	}

	@Override
	public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) {
		return cellData.getNumberValue().intValue();
	}

	@Override
	public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
		UserInfoCache userInfoCache = SpringContextUtil.getBean(UserInfoCache.class);
		UserInfoResp userInfoResp = userInfoCache.getById(context.getValue());
		if (userInfoResp == null || userInfoResp.getId() == null) {
			return NumberUtils.formatToCellData(context.getValue(), context.getContentProperty());
		}
		return new WriteCellData<>(userInfoResp.getNickname());
	}

}
