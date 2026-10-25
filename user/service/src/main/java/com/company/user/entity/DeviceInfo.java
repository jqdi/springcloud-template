package com.company.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("device_info")
public class DeviceInfo extends AuditableModel<DeviceInfo> {
	private Integer id;

	/**
	 * 设备ID
	 */
	private String deviceid;

	/**
	 * 应用编码(写死到客户端)
	 */
	private String appCode;

	/**
	 * 平台：app(APP)、mini(微信小程序)、h5(H5页面)、alimini(支付宝小程序)、alipaymini(支付宝小程序内H5)
	 */
	private String platform;

	/**
	 * 操作系统：ios(iOS)、mac(iOS)、android(Android)、win(Windows,如果获取不到可以不要)、devtools(小程序开发工具)
	 */
	private String operator;

	/**
	 * 渠道：wx(微信小程序)、ali(支付宝小程序)、dev(开发包)、sit(测试包)、uat(验收包)、ios(苹果应用商店)、xiaomi(小米应用商店)、huawei(华为应用商店)等
	 */
	private String channel;

	/**
	 * 版本号
	 */
	private String version;

	/**
	 * 请求IP
	 */
	private String requestip;

	/**
	 * 请求UserAgent
	 */
	private String requestUserAgent;

	/**
	 * 时间
	 */
	private LocalDateTime time;

	private String remark;
}
