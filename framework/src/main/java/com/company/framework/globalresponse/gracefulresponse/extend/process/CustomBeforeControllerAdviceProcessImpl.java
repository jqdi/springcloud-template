package com.company.framework.globalresponse.gracefulresponse.extend.process;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.company.common.exception.ResultException;
import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import com.feiniaojin.gracefulresponse.api.ExceptionMapper;

/**
 * copy from DefaultBeforeControllerAdviceProcessImpl
 * 自定义处理前回调，区分异常打印日志
 *
 * @author qinyujie
 */
@Component
public class CustomBeforeControllerAdviceProcessImpl implements BeforeControllerAdviceProcess {

    private final Logger logger = LoggerFactory.getLogger(CustomBeforeControllerAdviceProcessImpl.class);

    @Resource
    private GracefulResponseProperties properties;
    @Resource
    private ExceptionAliasRegister exceptionAliasRegister;

    @Override
    public void call(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        // 取代码中注册的异常别名
        Class<? extends Exception> clazz = ex.getClass();
        ExceptionAliasFor exceptionAliasFor = exceptionAliasRegister.getExceptionAliasFor(clazz);
        ExceptionMapper exceptionMapper = clazz.getAnnotation(ExceptionMapper.class);
        if (!(ex instanceof GracefulResponseException || ex instanceof ResultException || ex instanceof BindException
            || ex instanceof ValidationException || exceptionAliasFor != null || exceptionMapper != null)) {
            // 如果不是预期内的异常，则直接打印错误堆栈，方便排查问题
            logger.error("捕获到未知异常,message=[{}]", ex.getMessage(), ex);
            return;
        }
        if (properties.isPrintExceptionInGlobalAdvice()) {
            String code = properties.getDefaultErrorCode();
            String message = ex.getMessage();
            if (ex instanceof GracefulResponseException) {
                GracefulResponseException gracefulResponseException = (GracefulResponseException)ex;
                code = gracefulResponseException.getCode();
                message = gracefulResponseException.getMsg();
            } else if (ex instanceof ResultException) {
                ResultException resultException = (ResultException)ex;
                code = resultException.getCode();
                message = resultException.getMessage();
            } else if (exceptionAliasFor != null) {
                code = exceptionAliasFor.code();
                message = exceptionAliasFor.msg();
            } else if (exceptionMapper != null && !exceptionMapper.msgReplaceable()) {
                code = exceptionMapper.code();
                message = exceptionMapper.msg();
            }
//            logger.error("Graceful Response:捕获到异常,message=[{}]", ex.getMessage(), ex);
            logger.warn("Graceful Response:捕获到异常,code=[{}],message=[{}]", code, message);// 调整为warn级别，不打印堆栈，降低日志关注度
        }
    }
}
