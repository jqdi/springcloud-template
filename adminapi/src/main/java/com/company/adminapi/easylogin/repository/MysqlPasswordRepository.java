package com.company.adminapi.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.adminapi.util.PassWordUtil;
import com.company.system.api.feign.SysUserPasswordFeign;
import io.github.jqdi.easylogin.core.repository.PasswordRepository;

@Component
public class MysqlPasswordRepository implements PasswordRepository {
    @Autowired
    private SysUserPasswordFeign sysUserPasswordFeign;

    @Override
    public boolean checkPassword(String userId, String password) {
        String passwordDB = sysUserPasswordFeign.getPasswordBySysUserId(Integer.valueOf(userId)).values().iterator().next();
        String md5Password = PassWordUtil.md5(password);
        return md5Password.equals(passwordDB);
    }
}
