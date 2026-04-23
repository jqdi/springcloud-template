package com.company.framework.context.threadpool;

import com.company.framework.context.HeaderContextUtil;

import java.util.HashMap;
import java.util.Map;

public class HeaderContextRunnable implements Runnable {
    private Runnable target;
    private Map<String, String> headerMap;

    public HeaderContextRunnable(Runnable target) {
        this.target = target;

//        headerMap = HeaderContextUtil.headerMap();// 直接引用不知道会不会有问题
        headerMap = new HashMap<>();// 深克隆一份数据给子线程
        HeaderContextUtil.headerMap().entrySet().forEach(entry -> headerMap.put(entry.getKey(), entry.getValue()));
    }

    @Override
    public void run() {
        try {
            HeaderContextUtil.setHeaderMap(headerMap);
            target.run();
        } finally {
            HeaderContextUtil.remove();
        }
    }
}
