package com.company.framework.trace.threadpool;

import org.springframework.core.task.TaskDecorator;

import com.company.framework.trace.TraceManager;

public class TraceTaskDecorator implements TaskDecorator {
    private TaskDecorator taskDecorator;

    protected TraceManager traceManager;

    public TraceTaskDecorator(TraceManager traceManager) {
        this.traceManager = traceManager;
    }

    public TraceTaskDecorator(TaskDecorator taskDecorator, TraceManager traceManager) {
        this.taskDecorator = taskDecorator;
        this.traceManager = traceManager;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        if (taskDecorator != null) {
            return new TraceRunnable(taskDecorator.decorate(runnable), traceManager, traceManager.get());
        }
        return new TraceRunnable(runnable, traceManager, traceManager.get());
    }
}
