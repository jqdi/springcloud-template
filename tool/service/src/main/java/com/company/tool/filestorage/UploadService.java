package com.company.tool.filestorage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.util.Utils;
import io.github.jqdi.filestorage.core.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * 文件上传
 */
@Slf4j
@Service
public class UploadService {

    @Autowired
    private FileStorage fileStorage;

    /**
     * 上传
     *
     * @param outUrl 外部访问链接,如：https://tfs.alipayobjects.com/images/partner/TB1ynYQXmCwMeJk6XeaXXa9rpXa
     * @return fileKey
     */
    public String upload(String outUrl) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpUtil.download(outUrl, out, true);
        byte[] bytes = out.toByteArray();

        return upload(bytes);
    }

    /**
     * 上传
     *
     * @param bytes 文件流的byte数组
     * @return fileKey
     */
    public String upload(byte[] bytes) {
        return upload(bytes, null, null);
    }

    /**
     * 上传
     *
     * @param bytes    文件流的byte数组
     * @param fileName 文件名
     * @return fileKey
     */
    public String upload(byte[] bytes, String basePath, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return upload(inputStream, basePath, fileName);
        } catch (IOException e) {
            log.error("IOException", e);
            ExceptionUtil.throwException("上传失败");
        }
        return null;
    }

    /**
     * 上传图
     *
     * @param inputStream 文件流
     * @param fileName    文件名
     * @return fileKey
     */
    public String upload(InputStream inputStream, String basePath, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            byte[] bytes = IoUtil.readBytes(inputStream);
            fileName = String.format("%s.%s", "uuid-replace", Utils.extraSuffix(bytes));
        }
        String fileKey = generateFileKey(basePath, fileName);
        log.info("fileName:{}, fileKey:{}", fileName, fileKey);

        fileStorage.upload(inputStream, fileKey);
        return fileKey;
    }

    /**
     * 客户端上传
     *
     * @param fileName 文件名
     * @return 预签名链接
     */
    public ClientUploadResult clientUpload(String basePath, String fileName) {
        String fileKey = generateFileKey(basePath, fileName);

        String presignedUrl = fileStorage.clientUpload(fileKey);
        log.info("fileName:{}, fileKey:{}, presignedUrl:{}", fileName, fileKey, presignedUrl);
        return new ClientUploadResult().setFileKey(fileKey).setPresignedUrl(presignedUrl);
    }

    /**
     * 生成文件Key（目录+文件名，例如:basePath/202201/01/00033d9fea0b484eac1509567e87e61a.jpg）
     *
     * @param basePath
     * @param fileName
     * @return
     */
    private static String generateFileKey(String basePath, String fileName) {
        String fileKey = generateFileKey(fileName);
        // 生成文件Key（目录+文件名，例如:basePath/202201/01/00033d9fea0b484eac1509567e87e61a.jpg）
        if (basePath == null) {
            basePath = "";
        }
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1, basePath.length());
        }
        if (StringUtils.isNotBlank(basePath) && !basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        return basePath + fileKey;
    }

    /**
     * 生成文件Key（例如:202201/01/00033d9fea0b484eac1509567e87e61a.jpg）
     *
     * @param fileName
     * @return 文件Key
     */
    private static String generateFileKey(String fileName) {
        // 生成文件Key（例如:202201/01/00033d9fea0b484eac1509567e87e61a.jpg）
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);

        String ext = null;
        String[] fileNameSplit = fileName.split("\\.");
        if (fileNameSplit.length > 1) {
            ext = fileNameSplit[fileNameSplit.length - 1];
        }
        return String.format("%d%02d/%02d/%s.%s", year, month, day, IdUtil.fastSimpleUUID(), ext);
    }

    /**
     * 获取预签名链接
     *
     * @param fileKey
     * @return
     */
    public String presignedUrl(String fileKey) {
        return fileStorage.presignedUrl(fileKey);
    }

    /**
     * 获取文件内容
     *
     * @param fileKey
     * @return
     */
    public String getContent(String fileKey) {
        InputStream download = fileStorage.download(fileKey);
        String content = IoUtil.read(download, Charset.forName("utf-8"));
        return content;
    }

    public static void main(String[] args) {
        String fileName = "uuid-replace.jpg";

        // 2023/07-19/0b0fbf3ecb6c41ba96b6e20171a2a78d.jpg
        System.out.println(generateFileKey(fileName));

        // image/2023/07-19/0b0fbf3ecb6c41ba96b6e20171a2a78d.jpg
        System.out.println(generateFileKey("image", fileName));
        System.out.println(generateFileKey("/image", fileName));
        System.out.println(generateFileKey("image/", fileName));
        System.out.println(generateFileKey("/image/", fileName));

        String filePath = "D:/111.jpg";
//		String filePath = "D:/guohui.jpg";
//		String filePath = "D:/图片.rar";
        byte[] bytes = FileUtil.readBytes(filePath);
        System.out.println(Utils.extraSuffix(bytes));

//		String outUrl = "https://tfs.alipayobjects.com/images/partner/TB1ynYQXmCwMeJk6XeaXXa9rpXa";
        String outUrl = "https://thirdwx.qlogo.cn/mmopen/vi_32/zRziaYdcx2ib9icsCUSsAmqdhia9bLoCwHRHr02icZAI8DA5n97AVpdwF3ziafzJ0ViaQXia4ibGSAMSUKR1swgufMtS1MA/132";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpUtil.download(outUrl, out, true);
        System.out.println(Utils.extraSuffix(out.toByteArray()));
    }

}
