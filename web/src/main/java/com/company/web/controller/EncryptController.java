package com.company.web.controller;

import cn.licoy.encryptbody.annotation.encrypt.EncryptBody;
import cn.licoy.encryptbody.enums.EncryptBodyMethod;

import com.company.framework.util.JsonUtil;
import com.company.framework.util.PropertyUtils;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.response.Order4Resp;
import com.company.user.api.response.UserResp;
import com.company.web.req.DecryptEntityReq;
import com.company.web.req.DecryptFieldReq;
import com.company.web.resp.EncryptEntityResp;
import com.company.web.resp.EncryptFieldResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/encrypt")
public class EncryptController {

	@Autowired
	private OrderFeign orderFeign;

	@PostMapping(value = "/post-body-row")
	public com.company.web.resp.UserResp postbodyrow(@RequestBody Map<String, Object> param) {
//		if(true){
//			ExceptionUtil.throwException("asdasd");
//		}

		Order4Resp result = orderFeign.selectByOrderCode("666");
		System.out.println("byId:"+JsonUtil.toJsonString(result));
		com.company.web.resp.UserResp resp = PropertyUtils.copyProperties(result, com.company.web.resp.UserResp.class);
		return resp;
	}

	@PostMapping(value = "/post-body-row-result")
	public Map<String, Object> postbodyrowresult(@RequestBody Map<String, Object> param) {
		return param;
	}

	@PostMapping(value = "/post-body-row2")
	public String postbodyrownoresult2(@RequestBody Map<String, Object> param) {
		return "SUCCESS";
	}

	@PostMapping(value = "/post-body-row-result2")
	@EncryptBody(value = EncryptBodyMethod.AES)
	public Map<String, Object> postbodyrowresult2(@RequestBody Map<String, Object> param) {
		return param;
	}

	@PostMapping(value = "/post-body-row3")
	@EncryptBody(value = EncryptBodyMethod.AES)
	public String postbodyrownoresult3(@RequestBody Map<String, Object> param) {
		return "SUCCESS";
	}

	@PostMapping(value = "/encrypt-entity")
	public EncryptEntityResp encryptEntity(@RequestBody Map<String, Object> param) {
		EncryptEntityResp resp = new EncryptEntityResp();
		resp.setId(1);
		resp.setName("张三");
		return resp;
	}

	@PostMapping(value = "/encrypt-field")
	public EncryptFieldResp encryptField(@RequestBody Map<String, Object> param) {
		EncryptFieldResp resp = new EncryptFieldResp();
		resp.setId(1);
		resp.setName("张三");
		resp.setAesencryptbody("aesencryptbody");
		resp.setDesencryptbody("desencryptbody");
		resp.setMd5encryptbody("md5encryptbody");
//		resp.setRsaencryptbody("rsaencryptbody");
//		resp.setShaencryptbody("shaencryptbody");
		return resp;
	}

	@PostMapping(value = "/decrypt-entity")
	public EncryptEntityResp decryptEntity(@RequestBody DecryptEntityReq req) {
		EncryptEntityResp resp = new EncryptEntityResp();
		resp.setId(1);
		resp.setName("张三");
		return resp;
	}

	@PostMapping(value = "/decrypt-field")
	public EncryptFieldResp decryptField(@RequestBody DecryptFieldReq req) {
		EncryptFieldResp resp = new EncryptFieldResp();
		resp.setId(1);
		resp.setName("张三");
		resp.setAesencryptbody("aesencryptbody");
		resp.setDesencryptbody("desencryptbody");
		resp.setMd5encryptbody("md5encryptbody");
//		resp.setRsaencryptbody("rsaencryptbody");
//		resp.setShaencryptbody("shaencryptbody");
		return resp;
	}
}
