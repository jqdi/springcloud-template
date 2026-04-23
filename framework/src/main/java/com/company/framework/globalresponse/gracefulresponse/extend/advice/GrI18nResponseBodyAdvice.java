package com.company.framework.globalresponse.gracefulresponse.extend.advice;

import com.company.framework.globalresponse.gracefulresponse.extend.advice.context.GracefulResponseExceptionArgsContext;
import com.company.framework.message.IMessage;
import com.feiniaojin.gracefulresponse.advice.AbstractResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdviceProcessor;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * copy from com.feiniaojin.gracefulresponse.advice.GrI18nResponseBodyAdvice
 * 国际化处理
 * 原版的GrI18nResponseBodyAdvice不满足需求，重写部分逻辑：使用msg作为国际化的code，适配现有框架逻辑
 * @author feiniaojin
 */
@ControllerAdvice
@Order(2000)
//@ConditionalOnMissingBean(com.feiniaojin.gracefulresponse.advice.GrI18nResponseBodyAdvice.class)
public class GrI18nResponseBodyAdvice extends AbstractResponseBodyAdvice implements ResponseBodyAdvicePredicate, ResponseBodyAdviceProcessor {

//    private static final String[] EMPTY_ARRAY = new String[0];

//    @Resource
//    private GracefulResponseProperties properties;

//    @Resource
//    private MessageSource grMessageSource;
    @Resource
    private IMessage imessage;

    @Override
    public Object process(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Response) {
            Response res = (Response) body;
//            Locale locale = LocaleContextHolder.getLocale();
            ResponseStatus bodyStatus = res.getStatus();
//            String code = bodyStatus.getCode();
            String msg = bodyStatus.getMsg();
            // 这里处理ArgsExceptionAdvice.fromGracefulResponseExceptionInstance记录的args，需要处理好参数替换，再响应给前端
            Object[] args = GracefulResponseExceptionArgsContext.getAndRemoveArgs();
            String renderMsg = imessage.getMessage(msg, args);
            //有国际化配置的才会替换，否则使用默认配置的
            if (StringUtils.hasText(renderMsg)) {
                bodyStatus.setMsg(renderMsg);
            }
        }
        return body;
    }

    @Override
    public boolean shouldApplyTo(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
//        return properties.getI18n();
        return true;
    }

    @PostConstruct
    public void init() {
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(this);
        this.setPredicates(copyOnWriteArrayList);
        this.setResponseBodyAdviceProcessor(this);
    }
}
