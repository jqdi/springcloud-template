package com.company.framework.globalresponse.gracefulresponse.feign.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.company.common.exception.ResultException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.AbstractControllerAdvice;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.*;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理ResultException的异常
 *
 * @author JQ棣
 */
@Slf4j
@Order(190)
@ControllerAdvice
public class ResultExceptionAdvice extends AbstractControllerAdvice
    implements ControllerAdvicePredicate, ControllerAdviceProcessor, ControllerAdviceHttpProcessor {

    private ResponseFactory responseFactory;
    private GracefulResponseProperties properties;
    private ResponseStatusFactory responseStatusFactory;

    public ResultExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess, @Lazy RejectStrategy rejectStrategy,
                                 ResponseFactory responseFactory, GracefulResponseProperties properties, ResponseStatusFactory responseStatusFactory) {

        this.responseFactory = responseFactory;
        this.properties = properties;
        this.responseStatusFactory = responseStatusFactory;

        setRejectStrategy(rejectStrategy);
        setControllerAdviceProcessor(this);
        setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        setControllerAdviceHttpProcessor(this);
    }

    @Override
    public Response process(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {
        if (exception instanceof ResultException) {
            ResultException resultException = (ResultException) exception;
            ResponseStatus statusLine = fromGracefulResponseExceptionInstance(resultException);
            return responseFactory.newInstance(statusLine);
        }
        throw new UnsupportedOperationException();
    }

    private ResponseStatus fromGracefulResponseExceptionInstance(ResultException exception) {
        String code = exception.getCode();
        if (code == null) {
            code = properties.getDefaultErrorCode();
        }
        String message = exception.getMessage();
        if (StringUtils.isBlank(message)) {
            message = ExceptionUtils.getStackTrace(exception);
        }
        log.warn("业务异常:{}", message);
        return responseStatusFactory.newInstance(code, message);
    }

    @Override
    @ExceptionHandler(value = ResultException.class)
    public Object exceptionHandler(Exception exception) {
        return super.exceptionHandler(exception);
    }

    @Override
    public boolean shouldApplyTo(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {
        return exception instanceof ResultException;
    }
}


