package com.company.framework.logback;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import com.company.framework.context.HeaderContextUtil;
import com.company.framework.util.JsonUtil;
import com.fasterxml.classmate.types.ResolvedInterfaceType;
import com.fasterxml.classmate.types.ResolvedObjectType;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ArgumentToJsonConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String contextMessage = "";
        if (Level.ERROR.equals(event.getLevel())) {
            // 仅处理ERROR级别，增加上下文信息的输出，方便根据用户信息快速定位问题
            List<String> contextMessageList = new ArrayList<>();
            String userId = HeaderContextUtil.currentUserId();
            if (StringUtils.isNotBlank(userId)) {
                contextMessageList.add("userId:" + userId);
            }
            String device = HeaderContextUtil.currentDevice();
            if (StringUtils.isNotBlank(device)) {
                contextMessageList.add("device:" + device);
            }
            if (!contextMessageList.isEmpty()) {
                contextMessage = "[" + String.join(",", contextMessageList) + "]";
            }
        }

        Object[] argumentArray = event.getArgumentArray();
        if (isArgumentArrayAllSimpleType(argumentArray)) {
            // 如果所有参数都是简单类型，直接使用父类处理消息
            return contextMessage + super.convert(event);
        }
        Object[] newArgumentArray = convertArgumentArray(argumentArray);
        return contextMessage + MessageFormatter.arrayFormat(event.getMessage(), newArgumentArray).getMessage();
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
        // instanceof判断性能更好，所以放在前面判断
        if (arg instanceof CharSequence || arg instanceof Number || arg instanceof Boolean || arg instanceof Character
            || arg instanceof Enum) {
            return true;
        }
        // class判断性能次之，所以放在后面判断
        Class<?> clazz = arg.getClass();
        if (clazz.getName().startsWith("springfox")) {
            return true;
        }
        if (clazz.isPrimitive() || clazz.isEnum()) {
            return true;
        }
        // 枚举序列化，上面的判断枚举会false
        if (arg instanceof ResolvedObjectType) {
            ResolvedObjectType resolvedObjectType = (ResolvedObjectType)arg;
            if (resolvedObjectType.isInstanceOf(CharSequence.class) || resolvedObjectType.isInstanceOf(Number.class)
                || resolvedObjectType.isInstanceOf(Boolean.class) || resolvedObjectType.isInstanceOf(Character.class)
                || resolvedObjectType.isInstanceOf(Enum.class) || resolvedObjectType.isInstanceOf(Object.class)) {
                return true;
            }
            Class<?> erasedType = resolvedObjectType.getErasedType();
            if (erasedType.isPrimitive() || erasedType.isEnum()) {
                return true;
            }
        }
        if (arg instanceof ResolvedInterfaceType) {
            ResolvedInterfaceType resolvedInterfaceType = (ResolvedInterfaceType)arg;
            if (resolvedInterfaceType.isInstanceOf(CharSequence.class) || resolvedInterfaceType.isInstanceOf(Number.class)
                || resolvedInterfaceType.isInstanceOf(Boolean.class) || resolvedInterfaceType.isInstanceOf(Character.class)
                || resolvedInterfaceType.isInstanceOf(Enum.class) || resolvedInterfaceType.isInstanceOf(Object.class)) {
                return true;
            }
            Class<?> erasedType = resolvedInterfaceType.getErasedType();
            if (erasedType.isPrimitive() || erasedType.isEnum()) {
                return true;
            }
        }
        return false;
    }
}
