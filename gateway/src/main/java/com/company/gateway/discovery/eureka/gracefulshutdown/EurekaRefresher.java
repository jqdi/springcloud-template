package com.company.gateway.discovery.eureka.gracefulshutdown;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.stereotype.Component;

import com.company.gateway.context.SpringContextUtil;
import com.company.gateway.gracefulshutdown.ServerListRefresher;
import com.netflix.discovery.DiscoveryClient;

import lombok.extern.slf4j.Slf4j;

/**
 * eureka服务列表刷新
 *
 * @author JQ棣
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "true", matchIfMissing = true)
public class EurekaRefresher implements ServerListRefresher {

    @Override
    public void refresh(String type, String application, String ip, int port) {
        this.refreshRegistry(application);
        this.refreshServerList(application);
    }

    /**
     * 刷新注册列表
     */
    private void refreshRegistry(String application) {
		try {
            DiscoveryClient client = SpringContextUtil.getBean(DiscoveryClient.class);
            log.info("{},application before:{}", application, client.getApplication(application));

			Method method = DiscoveryClient.class.getDeclaredMethod("refreshRegistry");
			method.setAccessible(true);
			method.invoke(client);
			method.setAccessible(false);
			log.info("{},application after:{}", application, client.getApplication(application));
			log.info("refresh registry success!!!");
		} catch (Exception e) {
			log.error("refresh registry fail!!!", e);
		}
    }

    /**
     * 刷新服务列表
     */
    private void refreshServerList(String application) {
        try {
            /**
             * <pre>
             * 服务列表缓存查找方法：
             * 1.经过断点调试，发现服务下线后通过DiscoveryClient.getInstances是可以获取到剔除下线节点的服务列表的
             * 2.在DiscoveryClient.getInstances中断点，根据栈信息，关注LoadBalance关键字的类，找到RoundRobinLoadBalancer.choose入口
             * 3.通过feign调用不断调试，每次请求都到达RoundRobinLoadBalancer.choose，但不是每次请求都到达DiscoveryClient.getInstances，说明RoundRobinLoadBalancer.choose与DiscoveryClient.getInstances之间肯定存在一个缓存，即服务列表缓存
             *
             * 栈如下：
             * RoundRobinLoadBalancer.choose
             * RetryAwareServiceInstanceListSupplier.get
             * CachingServiceInstanceListSupplier.CacheFlux.lookup 缓存在这里Cache
             * CompositeDiscoveryClient.getInstances
             * DiscoveryClient.getInstances
             * </pre>
             */
            LoadBalancerCacheManager cacheManager = SpringContextUtil.getBean(LoadBalancerCacheManager.class);
            Cache cache = cacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME);

            log.info("{},cache before:{}", application, cache.get(application, List.class));
            cache.evict(application);
            log.info("{},cache after:{}", application, cache.get(application, List.class));
            log.info("refresh server list success!!!");
        } catch (Exception e) {
            log.error("refresh server list fail!!!", e);
        }
    }
}
