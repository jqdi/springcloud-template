package com.company.framework.trace.threadpool;

import com.company.framework.trace.TraceManager;

public class TraceRunnable implements Runnable {
    private final Runnable target;
    private final TraceManager traceManager;
    private String traceId;

    public TraceRunnable(Runnable target, TraceManager traceManager, String traceId) {
        this.target = target;
        this.traceManager = traceManager;
        this.traceId = traceId;
    }

    @Override
    public void run() {
        try {
            traceManager.put(traceId);
            target.run();
        } finally {
            traceManager.remove();
        }
    }
}
