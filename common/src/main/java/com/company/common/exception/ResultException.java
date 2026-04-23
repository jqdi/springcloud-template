package com.company.common.exception;

import lombok.Getter;

/**
 * 结果异常（建议交由全局异常处理器 GlobalExceptionHandler 处理抛出该异常，否则直接抛出RuntimeException或者对应含义的异常就好）
 *
 * @author JQ棣
 */
public class ResultException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Getter
	private String code;

	public ResultException(String code, String message) {
		super(message);
		this.code = code;
	}
}
