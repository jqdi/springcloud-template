package com.company.tool.filestorage;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PresignedUploadResult {
    /**
     * fileKey
     */
    String fileKey;

    /**
     * 预签名链接
     */
    String presignedUrl;
}