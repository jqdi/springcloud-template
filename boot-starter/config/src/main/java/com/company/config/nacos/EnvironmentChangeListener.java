package com.company.config.nacos;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

public class EnvironmentChangeListener {

    private final SpringValueAutoRefreshProcessor springValueAutoRefreshProcessor;

    public EnvironmentChangeListener(SpringValueAutoRefreshProcessor springValueAutoRefreshProcessor) {
        this.springValueAutoRefreshProcessor = springValueAutoRefreshProcessor;
    }

    @EventListener
    public void environmentChange(EnvironmentChangeEvent event) {
        springValueAutoRefreshProcessor.changedKeys(event.getKeys());
    }
}
