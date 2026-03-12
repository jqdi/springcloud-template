package com.company.framework.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.company.framework.constant.HeaderConstants;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * 请求头放到上下文
 */
public class HeaderContextUtil {
    // 有问题：父子线程不能传递
    // private static final ThreadLocal<String> CURRENT_TL = new ThreadLocal<>();

    // 有问题：子线程初始化后不能修改值
    // private static final ThreadLocal<String> CURRENT_TL = new InheritableThreadLocal<>();

    // 启动参数加上：-javaagent:path/to/transmittable-thread-local-2.14.5.jar，否则问题同InheritableThreadLocal
    private static final ThreadLocal<Map<String, String>> HEADER_CONTEXT_TL = new TransmittableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static void setHeaderMap(Map<String, String> headerMap) {
        HEADER_CONTEXT_TL.set(headerMap);
    }

    public static Map<String, String> headerMap() {
        return HEADER_CONTEXT_TL.get();
    }

    public static void remove() {
        HEADER_CONTEXT_TL.remove();
    }

    public static String head(String name) {
        return Optional.ofNullable(headerMap()).map(m -> m.get(name)).orElse(null);
    }

    public static String currentUserId() {
        return head(HeaderConstants.HEADER_CURRENT_USER_ID);
    }

    public static Integer currentUserIdInt() {
        return Optional.ofNullable(currentUserId()).map(Integer::valueOf).orElse(null);
    }

    public static String currentUserType() {
        return head(HeaderConstants.HEADER_CURRENT_USER_TYPE);
    }

    public static String platform() {
        return head(HeaderConstants.HEADER_PLATFORM);
    }

    public static String operator() {
        return head(HeaderConstants.HEADER_OPERATOR);
    }

    public static String version() {
        return head(HeaderConstants.HEADER_VERSION);
    }

    public static String deviceid() {
        return head(HeaderConstants.HEADER_DEVICEID);
    }

    public static String channel() {
        return head(HeaderConstants.HEADER_CHANNEL);
    }

    public static String requestip() {
        return head(HeaderConstants.HEADER_REQUESTIP);
    }

    public static String source() {
        return head(HeaderConstants.HEADER_SOURCE);
    }

    public static Map<String, String> httpContextHeader() {
        return headerMap();
    }

    public static Map<String, List<String>> httpContextHeaders() {
        Set<Map.Entry<String, String>> entrySet = httpContextHeader().entrySet();

        Map<String, List<String>> headers = Maps.newHashMap();
        for (Map.Entry<String, String> entry : entrySet) {
            headers.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        return headers;
    }
}
