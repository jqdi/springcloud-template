package com.company.framework.constant;

public interface HeaderConstants {
	// 公共请求头（与用户无关）
	String HEADER_PLATFORM = "x-platform";// 平台：app(APP)、mini(微信小程序)、h5(H5页面)、alimini(支付宝小程序)、alipaymini(支付宝小程序内H5)
	String HEADER_OPERATOR = "x-operator";// 操作系统：ios(iOS)、mac(iOS)、android(Android)、win(Windows,如果获取不到可以不要)、devtools(小程序开发工具)
	String HEADER_VERSION = "x-version";// 版本号：4.1.0
	String HEADER_DEVICEID = "x-deviceid";// 设备ID：82b6fe22b2063733af477a8df7358238
	String HEADER_CHANNEL = "x-channel";// 渠道：wx(微信小程序)、ali(支付宝小程序)、dev(开发包)、sit(测试包)、uat(验收包)、ios(苹果应用商店)、xiaomi(小米应用商店)、huawei(华为应用商店)等
	String HEADER_REQUESTIP = "x-requestip";// 请求IP（最外层的请求）
	String HEADER_SOURCE = "x-source";// 用户来源：pyqgg(朋友圈广告)、dygg(抖音广告)、dt_15333223333(张三地推)、fx_oQvXm5qqQIiEPewZTtOKugyuEGWA(用户分享)、fx_rebate_oQvXm5qqQIiEPewZTtOKugyuEGWA(用户返现分享)

	// 用户请求头（注：为了防止直接在header设置用户ID，绕过认证，要取最后1个值）
	String HEADER_CURRENT_USER_ID = "x-current-user-id";// 当前登录用户id
	String HEADER_CURRENT_DEVICE = "x-current-device";// 当前登录设备

	// 国际化语言切换
	String ACCEPT_LANGUAGE = "Accept-Language";// 当前语言

	/**
	 * 日志追踪ID
	 */
	String TRACE_ID = "trace-id";
}
