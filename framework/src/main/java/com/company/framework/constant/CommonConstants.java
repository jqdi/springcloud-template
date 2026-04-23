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
		int DEVICE = 6;
		int SOURCE = 7;
		int REQUEST = 10;
		int TOKEN = 30;
		int HEADER_CONTEXT = 40;// 优先级必须最低，才能将所有的请求头放到上下文
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
