//package com.company.admin.aspect;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.lang.reflect.Parameter;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.List;
//
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestBody;
//
//
//import com.company.framework.context.HttpContextUtil;
//import com.company.framework.util.JsonUtil;
//
///**
// * @author JQ棣
// * @date 2018/9/28
// */
//@Component
//@Aspect
//@Order(1)
//public class ValidationAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(ValidationAspect.class);
//
//    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
//            " || @annotation(org.springframework.web.bind.annotation.PostMapping)" +
//            " || @annotation(org.springframework.web.bind.annotation.GetMapping)")
//    public Object around(JoinPoint joinPoint) throws Throwable {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String[] parameterNames = methodSignature.getParameterNames();
//        Parameter[] parameters = methodSignature.getMethod().getParameters();
//        for (int i = 0; i < parameters.length; ++i) {
//            Parameter parameter = parameters[i];
//            Object arg = joinPoint.getArgs()[i];
//
//            boolean handleEmptyBody = parameter.isAnnotationPresent(RequestBody.class) && arg == null;
//            if (handleEmptyBody) {
//                if (parameter.getType().isAssignableFrom(List.class)) {
//                    Type[] types = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments();
//                    joinPoint.getArgs()[i] = JsonUtil.toList("[]", types[0].getClass());
//                }
//                else {
//                    joinPoint.getArgs()[i] = parameter.getType().newInstance();
//                }
//            }
//
//            try {
//                boolean handleNullArg = parameter.isAnnotationPresent(NotNull.class) && arg == null;
//                if (handleNullArg) {
//                    throw new IllegalArgumentException(parameterNames[i] + "不能为空");
//                }
//
//                boolean handleBlankArg = parameter.isAnnotationPresent(NotBlank.class) && (arg == null
//                        || arg instanceof String && StringUtils.isBlank((String) arg));
//                if (handleBlankArg) {
//                    throw new IllegalArgumentException(parameterNames[i] + "不能为空");
//                }
//
//                boolean handleEmptyArg = parameter.isAnnotationPresent(NotEmpty.class) && (arg == null
//                        || arg instanceof Object[] && ((Object[]) arg).length == 0);
//                if (handleEmptyArg) {
//                    throw new IllegalArgumentException(parameterNames[i] + "不能为空");
//                }
//            } catch (IllegalArgumentException e) {
//                HttpServletResponse response = HttpContextUtil.response();
//                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//                try (PrintWriter printWriter = response.getWriter()) {
//                    printWriter.write(JsonUtil.toJsonString(Result.fail(e.getMessage())));
//                } catch (IOException ioe) {
//                    logger.error(ioe.getMessage());
//                }
//                throw e;
//            }
//        }
//        return ((ProceedingJoinPoint) joinPoint).proceed(joinPoint.getArgs());
//    }
//}
