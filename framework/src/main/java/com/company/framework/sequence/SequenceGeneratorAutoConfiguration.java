package com.company.framework.sequence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.framework.sequence.snowflake.HutoolSnowflake;

@Configuration(proxyBeanMethods = false)
public class SequenceGeneratorAutoConfiguration {

    @Bean
    public SequenceGenerator sequenceGenerator() {
        // SequenceGenerator sequenceGenerator = new HutoolSnowflake(port, datacenterId);
        SequenceGenerator sequenceGenerator = new HutoolSnowflake();
        return sequenceGenerator;
    }
}
