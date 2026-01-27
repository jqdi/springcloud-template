package com.company.app.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 解决knife4j与actuator代码兼容性问题导致NPE
 */
@Configuration(proxyBeanMethods = false)
public class Knife4jActuatorCompatibilityConfig {
	/**
	 * 解决springboot升到2.6.x之后，knife4j报错
	 * 原文链接：https://gitee.com/xiaoym/knife4j/issues/I4JT89
	 * 
	 * @param wes the web endpoints supplier
	 * @param ses the servlet endpoints supplier
	 * @param ces the controller endpoints supplier
	 * @param emt the endpoint media types
	 * @param cep the cors properties
	 * @param wep the web endpoints properties
	 * @param env the environment
	 * @return the web mvc endpoint handler mapping
	 */
	@Bean
	public WebMvcEndpointHandlerMapping webMvcEndpointHandlerMapping(WebEndpointsSupplier wes,
			ServletEndpointsSupplier ses, ControllerEndpointsSupplier ces, EndpointMediaTypes emt,
			CorsEndpointProperties cep, WebEndpointProperties wep, Environment env) {
		List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
		Collection<ExposableWebEndpoint> webEndpoints = wes.getEndpoints();
		allEndpoints.addAll(webEndpoints);
		allEndpoints.addAll(ses.getEndpoints());
		allEndpoints.addAll(ces.getEndpoints());
		String basePath = wep.getBasePath();
		EndpointMapping endpointMapping = new EndpointMapping(basePath);
		boolean shouldRegisterLinksMapping = shouldRegisterLinksMapping(wep, env, basePath);
		return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, emt, cep.toCorsConfiguration(),
				new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
	}

	/**
	 * shouldRegisterLinksMapping
	 *
	 * @param wep
	 * @param env
	 * @param basePath
	 * @return
	 */
	private boolean shouldRegisterLinksMapping(WebEndpointProperties wep, Environment env, String basePath) {
		return wep.getDiscovery().isEnabled()
				&& (StringUtils.hasText(basePath) || ManagementPortType.get(env).equals(ManagementPortType.DIFFERENT));
	}
}
