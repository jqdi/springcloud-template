package com.company.framework.constant;

public interface CommonConstants {
	String BASE_PACKAGE = "com.company";

	/**
	 * 过滤器优先级
	 */
	public interface FilterOrdered {
		// 值越小，优先级越高
		int TRACE = -10;
		int SUMMARY_API = -5;
		int HTTP_CONTEXT = 5;
		int DEVICE = 60;
		int SOURCE = 70;
		int REQUEST = 80;
		int TOKEN = 90;
		int HEADER_CONTEXT = 100;// 优先级必须最低，才能将所有的请求头放到上下文
	}

	/**
	 * 拦截器优先级
	 */
	public interface InterceptorOrdered {
		// 值越小，优先级越高

		// token
//		int ACCESS_CONTROL = 1;

		// admin 引用了edge
		int PERMISSION = 5;
	}
}
