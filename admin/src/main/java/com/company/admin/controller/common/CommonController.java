package com.company.admin.controller.common;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.jqdi.filestorage.core.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Controller
public class CommonController {

	@Autowired
	private FileStorage fileStorage;

    @RequestMapping("/admin/editor/token")
	@ResponseBody
	public Map<String, String> ossTokenEncrypt(String token) {
		Map<String, String> result = Maps.newHashMap();
//		result.put("accessKeyId", "1");
//		result.put("accessKeySecret", "1");
		return result;
	}


    /**
     * 文件上传
     *
     * @param folders
     * @param files
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/admin/common/api/file/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<String> fileUpload(String[] folders, MultipartFile[] files) throws Exception {
    	List<String> resultList = Lists.newArrayList();
		for (int i = 0; i < files.length; i++) {
    		MultipartFile file = files[i];
    		String name = file.getName();
    		String originalFilename = file.getOriginalFilename();
    		String contentType = file.getContentType();
    		long size = file.getSize();
    		log.info("name:{},originalFilename:{},contentType:{},size:{}", name, originalFilename, contentType, size);
    		
    		try (InputStream inputStream = file.getInputStream()) {
    			String fileName = generateFileName(file.getOriginalFilename());
				String fullFileName = fullFileName(folders[i], fileName);

                fileStorage.upload(inputStream, fullFileName);

    			resultList.add(fullFileName);
    		} catch (IOException e) {
    			log.error("IOException", e);
    		}
		}
        return resultList;
    }
    

	private static String fullFileName(String basePath, String fileName) {
		if (basePath == null) {
			basePath = "";
		}
		if (basePath.startsWith("/")) {
			basePath = basePath.substring(1, basePath.length());
		}
		if (StringUtils.isNotBlank(basePath) && !basePath.endsWith("/")) {
			basePath = basePath + "/";
		}
		return basePath + fileName;
	}
	
	private static String generateFileName(String fileName) {
		// 生成文件名（目录+文件名，例如:basePath/202201/01/00033d9fea0b484eac1509567e87e61a.jpg）
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DAY_OF_MONTH);

		String ext = null;
		String[] fileNameSplit = fileName.split("\\.");
		if (fileNameSplit.length > 1) {
			ext = fileNameSplit[fileNameSplit.length - 1];
		}
		return String.format("%d%02d/%02d/%s.%s", year, month, day, UUID.randomUUID().toString().replace("-", ""), ext);
	}
}
