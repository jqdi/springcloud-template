package com.company.admin.springsecurity;

import com.company.common.api.ResultCode;
import com.company.framework.util.JsonUtil;
import com.google.common.collect.Maps;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 系统用户登录失败处理器
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String exceptionMsg = exception.getMessage();
        String code = ResultCode.FAIL.getCode();
        String msg = ResultCode.FAIL.getMessage();

        if (exception instanceof UsernameNotFoundException) {
            if (exception.getMessage().equals(ResultCode.FAIL.getMessage())) {
                code = ResultCode.FAIL.getCode();
                msg = ResultCode.FAIL.getMessage();
            }
            else {
                code = ResultCode.FAIL.getCode();
                msg = "员工不存在";
            }
        }
        else if (exception instanceof BadCredentialsException) {
            code = ResultCode.FAIL.getCode();
            msg = "密码错误";
        }
        else if (exception instanceof InternalAuthenticationServiceException) {
            code = ResultCode.FAIL.getCode();
            msg = "员工被禁用";
        }
        else if (exceptionMsg.contains("principal exceeded")) {
            code = ResultCode.FAIL.getCode();
            msg = "该账号超出最大登录人数限制";
        }

        Map<String, Object> errorMap = Maps.newHashMap();
        errorMap.put("status", false);
        errorMap.put("code", code);
        errorMap.put("msg", msg);
        String errorMsg = JsonUtil.toJsonString(errorMap);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(errorMsg);
    }
}
