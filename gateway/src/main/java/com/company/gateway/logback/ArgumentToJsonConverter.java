package com.company.gateway.logback;

import com.company.gateway.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ArgumentToJsonConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (isArgumentArrayAllSimpleType(argumentArray)) {
            // 如果所有参数都是简单类型，直接使用父类处理消息
            return super.convert(event);
        }
        Object[] newArgumentArray = convertArgumentArray(argumentArray);
        return MessageFormatter.arrayFormat(event.getMessage(), newArgumentArray).getMessage();
    }

    /**
     * 判断参数数组是否全部是简单类型
     */
    private boolean isArgumentArrayAllSimpleType(Object[] argumentArray) {
        if (argumentArray == null || argumentArray.length == 0) {
            return true;
        }
        boolean allArgumentIsSimpleType = true;
        for (Object arg : argumentArray) {
            if (!isSimpleType(arg)) {
                allArgumentIsSimpleType = false;
                break;
            }
        }
        return allArgumentIsSimpleType;
    }

    private Object[] convertArgumentArray(Object[] argumentArray) {
        Object[] newArgumentArray = new Object[argumentArray.length];
        for (int i = 0; i < argumentArray.length; i++) {
            newArgumentArray[i] = serializeArg(argumentArray[i]);
        }
        return newArgumentArray;
    }

    /**
     * 参数序列化
     */
    private Object serializeArg(Object arg) {
        if (arg == null || isSimpleType(arg)) {
            return arg;
        }
        String argJsonString = JsonUtil.toJsonString(arg);
        if (StringUtils.isBlank(argJsonString)) {
            // log.warn("argJsonString is blank:{}", arg); 这里不能打log，会导致死循环
            System.err.println("argJsonString is blank:" + arg);
            return arg;
        }
        return argJsonString;
    }

    /**
     * 判断参数是否简单类型
     */
    private boolean isSimpleType(Object arg) {
        if (arg == null) {
            return true;
        }
        Class<?> clazz = arg.getClass();
        if (clazz.isPrimitive()) {
            return true;
        }
        return arg instanceof CharSequence || arg instanceof Number || arg instanceof Boolean || arg instanceof Character
            || arg instanceof Enum;
    }
}