package com.company.gateway.trace.provider;

import com.company.gateway.trace.TraceIdProvider;

import java.util.Random;

public class RandomProvider implements TraceIdProvider {
    /**
     * 1.Random是线程安全的<br/>
     * 2.高并发情况下，单实例的性能不如每个线程持有一个实例<br/>
     * 3.经粗略测试，并发数少于200情况下性能是单实例优，按需来说项目目前并发量在200内<br/>
     */
    private static Random random = new Random();

    @Override
    public String generateTraceId() {
        return "" + System.currentTimeMillis() + (1000 + random.nextInt(9000));
    }
}
