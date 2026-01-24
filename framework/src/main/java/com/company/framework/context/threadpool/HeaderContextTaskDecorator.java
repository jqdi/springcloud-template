package com.company.framework.context.threadpool;

import org.springframework.core.task.TaskDecorator;

public class HeaderContextTaskDecorator implements TaskDecorator {

    private TaskDecorator taskDecorator;

    public HeaderContextTaskDecorator() {}

    public HeaderContextTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        if (taskDecorator != null) {
            return new HeaderContextRunnable(taskDecorator.decorate(runnable));
        }
        return new HeaderContextRunnable(runnable);
    }
}
