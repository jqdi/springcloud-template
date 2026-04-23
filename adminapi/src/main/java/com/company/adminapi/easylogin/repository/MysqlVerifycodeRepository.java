package com.company.adminapi.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.adminapi.constants.Constants;
import com.company.tool.api.feign.VerifyCodeFeign;
import io.github.jqdi.easylogin.core.repository.VerifycodeRepository;

@Component
public class MysqlVerifycodeRepository implements VerifycodeRepository {

    @Autowired
    private VerifyCodeFeign verifyCodeFeign;

    @Override
    public boolean checkVerifycode(String identifier, String verifyCode) {
        String[] split = verifyCode.split(":");
        String uuid = split[0];
        String code = split[1];
        Boolean verifyPass = verifyCodeFeign.verify(Constants.VerifyCodeType.ADMIN_LOGIN, uuid, code);
        return verifyPass;
    }

}
