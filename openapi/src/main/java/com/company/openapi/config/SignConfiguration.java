package com.company.openapi.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SignAccountProperties.class)
@ConditionalOnProperty(prefix = "sign", name = "check", havingValue = "true", matchIfMissing = true)
public class SignConfiguration {

	private SignAccountProperties properties;

	@Autowired
	public SignConfiguration(SignAccountProperties properties) {
		this.properties = properties;
	}

	public String getAppsecret(String appid) {
		List<SignAccountProperties.Account> accountList = properties.getAccounts();
		for (SignAccountProperties.Account account : accountList) {
			if (account.getAppid().equals(appid)) {
				return account.getAppsecret();
			}
		}
		throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
	}

	public Integer getReqValidSeconds() {
		return properties.getReqValidSeconds();
	}
	
	public boolean nonceValid() {
		return Optional.of(properties.getNonceValid()).orElse(true);
	}
}
