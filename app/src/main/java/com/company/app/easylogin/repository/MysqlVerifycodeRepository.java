package com.company.app.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.app.constants.Constants;
import com.company.tool.api.feign.VerifyCodeFeign;
import io.github.jqdi.easylogin.core.repository.VerifycodeRepository;

@Component
public class MysqlVerifycodeRepository implements VerifycodeRepository {

	@Autowired
	private VerifyCodeFeign verifyCodeFeign;

	@Override
	public boolean checkVerifycode(String identifier, String verifyCode) {
        Boolean verifyPass = verifyCodeFeign.verify(Constants.VerifyCodeType.LOGIN, identifier, verifyCode);
        return verifyPass;
	}

}
