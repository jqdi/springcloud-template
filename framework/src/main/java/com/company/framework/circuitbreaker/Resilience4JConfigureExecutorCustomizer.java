/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.company.framework.circuitbreaker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import com.company.framework.threadpool.TaskDecoratorThreadPoolExecutor;
import com.company.framework.threadpool.ThreadPoolAutoConfiguration;
import com.company.framework.threadpool.ThreadPoolProperties;

/**
 * resilience4j的线程池替换为自定义线程池
 */
@Component
@ConditionalOnProperty(name = { "spring.cloud.circuitbreaker.resilience4j.enabled",
        "spring.cloud.circuitbreaker.resilience4j.blocking.enabled" }, matchIfMissing = true)
public class Resilience4JConfigureExecutorCustomizer implements Customizer<Resilience4JCircuitBreakerFactory> {
    private final ThreadPoolProperties properties;
    private final TaskDecorator taskDecorator;

    public Resilience4JConfigureExecutorCustomizer(ThreadPoolProperties properties, TaskDecorator taskDecorator) {
        this.properties = properties;
        this.taskDecorator = taskDecorator;
    }

    @Override
    public void customize(Resilience4JCircuitBreakerFactory factory) {
        int corePoolSize = properties.getCorePoolSize();
        int maximumPoolSize = properties.getMaxPoolSize();
        long keepAliveTime = properties.getKeepAliveSeconds();
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(properties.getQueueCapacity());
        ExecutorService executor = new TaskDecoratorThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
            TimeUnit.SECONDS, workQueue, new ThreadPoolAutoConfiguration.CustomDefaultThreadFactory(),
            new ThreadPoolAutoConfiguration.CustomCallerRunsPolicy(), taskDecorator);

        factory.configureExecutorService(executor);// 自定义线程池传递日志ID
    }
}
