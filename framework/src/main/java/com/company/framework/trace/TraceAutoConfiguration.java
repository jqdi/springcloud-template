package com.company.framework.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

import com.company.framework.constant.HeaderConstants;
import com.company.framework.context.threadpool.HeaderContextTaskDecorator;
import com.company.framework.trace.threadpool.TraceTaskDecorator;
import com.company.framework.trace.provider.RandomProvider;

@Configuration
public class TraceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceIdProvider traceIdProvider() {
//        return new UUIDProvider();
        return new RandomProvider();
//        return new SkywalkingProvider(new RandomProvider());
    }

    @Bean
    public TraceManager traceManager(TraceIdProvider traceIdProvider) {
        return new TraceManager(HeaderConstants.TRACE_ID, traceIdProvider);
    }

    /**
     * 任务装饰器，解决线程池中任务的日志id+上下文传递
     * @see com.company.framework.threadpool.ThreadPoolAutoConfiguration
     *
     * @param traceManager
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskDecorator taskDecorator(TraceManager traceManager) {
        TraceTaskDecorator traceTaskDecorator = new TraceTaskDecorator(traceManager);// 传递日志id
//        return traceTaskDecorator;
        return new HeaderContextTaskDecorator(traceTaskDecorator);// 传递日志id+上下文
    }
}
