package com.company.framework.autoconfigure;

import org.frameworkset.elasticsearch.boot.BBossESProperties;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.company.framework.autoconfigure.BBossESAutoConfiguration.BBossESCondition;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(BBossESStarter.class)
@EnableConfigurationProperties({ BBossESProperties.class })
@Conditional(BBossESCondition.class)
public class BBossESAutoConfiguration {

	@Bean(initMethod = "start")
	@ConditionalOnMissingBean
	public BBossESStarter bbossESStarter() {
		return new BBossESStarter();
	}

	static class BBossESCondition extends AllNestedConditions {

		BBossESCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnProperty(prefix = "template.enable", name = "elasticsearch", havingValue = "true")
		static class EnableProperty {
		}

		@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "bboss.elasticsearch.rest.hostNames")
		static class HostProperty {
		}

	}
}
