package com.company.framework.globalresponse.gracefulresponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.company.framework.globalresponse.gracefulresponse.extend.advice.CustomValidationExceptionAdvice;
import com.feiniaojin.gracefulresponse.AutoConfig;
import com.feiniaojin.gracefulresponse.advice.DefaultValidationExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;

/**
 * 替代@EnableGracefulResponse完成GracefulResponse初始化
 */
@Configuration
//@EnableGracefulResponse
public class GracefulResponseAutoConfig extends AutoConfig {
    /**
     * 替换掉默认的自定义参数校验异常处理器（原DefaultValidationExceptionAdvice有bug）
     * 
     * @param beforeControllerAdviceProcess
     * @param rejectStrategy
     * @return
     */
    @Bean
    public DefaultValidationExceptionAdvice defaultValidationExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                                             @Lazy RejectStrategy rejectStrategy) {
        DefaultValidationExceptionAdvice advice = new CustomValidationExceptionAdvice();
        advice.setRejectStrategy(rejectStrategy);
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        // 设置默认参数校验异常http处理器
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }
}
