package com.company.framework.globalresponse;


import com.company.common.api.Result;
import com.company.common.api.ResultCode;
import com.company.common.exception.ResultException;
import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
//@RestControllerAdvice
@Deprecated // 替换为GracefulResponse框架处理
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    /**
     * 参数业务异常
     */
    @ExceptionHandler(ArgsBusinessException.class)
    public Result<?> business(ArgsBusinessException e, HttpServletRequest request, HttpServletResponse response,
                              HandlerMethod handler) {
        String message = e.getMessage();
        if (StringUtils.isBlank(message)) {
            message = ExceptionUtils.getStackTrace(e);
        }
        log.warn("业务异常:{}", message);
        sendErrorIfPage(request, response, handler);
        return Result.fail(e.getCode(), messageSource.getMessage(message, e.getArgs(), LocaleContextHolder.getLocale()));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> business(BusinessException e, HttpServletRequest request, HttpServletResponse response,
                              HandlerMethod handler) {
        String message = e.getMessage();
        if (StringUtils.isBlank(message)) {
            message = ExceptionUtils.getStackTrace(e);
        }
        log.warn("业务异常:{}", message);
        sendErrorIfPage(request, response, handler);
        return Result.fail(e.getCode(), messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
    }

    /**
     * 结果异常
     */
    @ExceptionHandler(ResultException.class)
    public Result<?> result(ResultException e, HttpServletRequest request, HttpServletResponse response,
                            HandlerMethod handler) {
        String message = e.getMessage();
        if (StringUtils.isBlank(message)) {
            message = ExceptionUtils.getStackTrace(e);
        }
        log.warn("业务异常:{}", message);
        sendErrorIfPage(request, response, handler);
        return Result.fail(e.getCode(), messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
    }

    /**
     * 拦截异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e, HttpServletRequest request, HttpServletResponse response,
                           HandlerMethod handler) {
        log.error("未知异常:", e);
        sendErrorIfPage(request, response, handler);
        ResultCode resultCode = ResultCode.SYSTEM_ERROR;
        return Result.fail(resultCode.getCode(), messageSource.getMessage(resultCode.getMessage(), null, LocaleContextHolder.getLocale()));
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                  HttpServletRequest request, HttpServletResponse response) {
        String message = messageSource.getMessage("不支持{0}请求", new Object[]{e.getMethod()}, LocaleContextHolder.getLocale());
//		log.warn(message, e);
        return Result.fail(message);
    }

    /**
     * 媒体类型不支持
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e,
                                                        HttpServletRequest request, HttpServletResponse response) {
        String message = messageSource.getMessage("仅支持{0}媒体类型", new Object[]{JsonUtil.toJsonString(e.getSupportedMediaTypes())}, LocaleContextHolder.getLocale());
        log.warn(message, e);
        return Result.fail(message);
    }

    /**
     * 参数缺失
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> missingServletRequestParameter(MissingServletRequestParameterException e,
                                                    HttpServletRequest request, HttpServletResponse response) {
        String message = messageSource.getMessage("参数{0}({1})缺失", new Object[]{e.getParameterName(), e.getParameterType()}, LocaleContextHolder.getLocale());
//		log.warn(message, e);
        return Result.fail(message);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request,
                                                HttpServletResponse response) {
        String message = messageSource.getMessage("参数{0}({1})不匹配{2}类型", new Object[]{e.getName(), e.getValue(),
                Optional.ofNullable(e.getRequiredType()).map(Class::getName).orElse(null)}, LocaleContextHolder.getLocale());
//		log.warn(message, e);
        return Result.fail(message);
    }

    /**
     * 文件上传大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> maxUploadSizeExceededException(MaxUploadSizeExceededException e,
                                                    HttpServletRequest request, HttpServletResponse response) {
        String maxFileSize = SpringContextUtil.getProperty("spring.servlet.multipart.max-file-size", "1M");
        String message = messageSource.getMessage("文件大小需小于{0}", new Object[]{maxFileSize}, LocaleContextHolder.getLocale());
        log.warn(message, e);
        return Result.fail(message);
    }

    /**
     * 拦截未处理的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtime(RuntimeException e, HttpServletRequest request, HttpServletResponse response,
                             HandlerMethod handler) {
        log.error("未处理运行时异常", e);
        sendErrorIfPage(request, response, handler);
        ResultCode resultCode = ResultCode.SYSTEM_ERROR;
        return Result.fail(resultCode.getCode(), messageSource.getMessage(resultCode.getMessage(), null, LocaleContextHolder.getLocale()));
    }

    // 各种运行时异常单独处理可以在这里添加,例如
    /**
     * 用来处理bean validation异常 主要是
     * resolveMethodArgumentNotValidException，handleBingException 这两个方法
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> resolveConstraintViolationException(ConstraintViolationException e, HttpServletRequest request,
                                                         HttpServletResponse response, HandlerMethod handler) {
        sendErrorIfPage(request, response, handler);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            String messageStr = constraintViolations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
            return Result.fail(messageStr);
        }
        return Result.fail(e.getMessage());
    }

    /**
     * @Desc 处理 @RequestBody 类型的 POJO 参数
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> resolveMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                            HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        sendErrorIfPage(request, response, handler);
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        if (!CollectionUtils.isEmpty(objectErrors)) {
            String messageStr = objectErrors.stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(","));
            return Result.fail(messageStr);
        }
        return Result.fail(e.getMessage());
    }

    /**
     * @Desc 处理表单提交的 POJO 参数
     **/
    @ExceptionHandler(BindException.class)
    public Result<?> handleBingException(BindException e, HttpServletRequest request, HttpServletResponse response,
                                         HandlerMethod handler) {
        sendErrorIfPage(request, response, handler);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        if (!CollectionUtils.isEmpty(allErrors)) {
            String messageStr = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
            return Result.fail(messageStr);
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 数据重复
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> duplicateKey(DuplicateKeyException e, HttpServletRequest request, HttpServletResponse response,
                                  HandlerMethod handler) {
        log.error("数据重复异常", e);
        sendErrorIfPage(request, response, handler);
        return Result.fail(messageSource.getMessage("数据重复", null, LocaleContextHolder.getLocale()));
    }

    private boolean isReturnJson(HandlerMethod handler) {
        if (handler.getBeanType().isAnnotationPresent(RestController.class)) {
            return true;
        }
        Method method = handler.getMethod();
        if (method.isAnnotationPresent(ResponseBody.class)) {
            return true;
        }
        return false;
    }

    private void sendErrorIfPage(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        String accept = request.getHeader("Accept");
        if (StringUtils.isNotBlank(accept) && accept.contains("application/json")) {
            return;
        }
        String contentType = response.getContentType();
        if (StringUtils.isNotBlank(contentType) && contentType.contains("application/json")) {
            return;
        }
        if (isReturnJson(handler)) {
            return;
        }
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("sendError error", e);
        }
    }
}
