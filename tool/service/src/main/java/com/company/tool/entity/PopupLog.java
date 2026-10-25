package com.company.tool.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("popup_log")
public class PopupLog extends AuditableModel<PopupLog> {
	private Integer id;

	/**
	 * 业务类型(popup:popup,user_popup:user_popup)
	 */
	private String businessType;
	
	/**
	 * 业务ID
	 */
	private Integer businessId;
	
	/**
	 * user_info.id
	 */
	private Integer userId;

	/**
	 * 设备号(小程序是openid，APP是设备ID)
	 */
	private String deviceid;

	private String remark;
}
