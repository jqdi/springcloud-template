package com.company.tool.api.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PresignedUploadResp {
	/**
	 * fileKey
	 */
	String fileKey;

	/**
	 * 预签名链接
	 */
	String presignedUrl;
}