package com.company.gateway.discovery.nacos.gracefulshutdown;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.company.gateway.context.SpringContextUtil;
import com.company.gateway.gracefulshutdown.ServerListRefresher;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * nacos服务列表刷新
 *
 * @author JQ棣
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.enabled", havingValue = "true", matchIfMissing = true)
public class NacosRefresher implements ServerListRefresher {

    @Autowired
    private NacosDiscoveryProperties nacosProperties;

    @Override
    public void refresh(String type, String application, String ip, int port) {
        this.refreshRegistry(type, application, ip, port);
        this.refreshServerList(application);
    }

    /**
     * 刷新注册列表
     */
    private void refreshRegistry(String type, String application, String ip, int port) {
        // nacos没有提供能直接刷新注册列表的方法，这里使用比较服务列表数量的方式解决
        NamingService namingService = nacosProperties.namingServiceInstance();

        String groupName = nacosProperties.getGroup();

        String clusterName = nacosProperties.getClusterName();
        List<String> clusters = Lists.newArrayList();
        if (StringUtils.isNotBlank(clusterName)) {
            clusters = Arrays.asList(StringUtils.split(clusterName, ","));
        }
        boolean subscribe = false;
        try {
            if ("startup".equals(type)) {// 启动
                for (int i = 0; i < 5; i++) {// 最多尝试5次，可调大，不建议太大
                    List<Instance> allInstances = namingService.getAllInstances(application, groupName, clusters, subscribe);
                    log.info("{},instances:{}", application, allInstances);
                    boolean instanceExist = false;
                    for (Instance instance : allInstances) {
                        if (ip.equals(instance.getIp()) && port == instance.getPort()) {
                            instanceExist = true;
                            break;
                        }
                    }
                    if (instanceExist) {// 实例存在则退出
                        break;
                    }
                    Thread.sleep(1000);// 睡眠1秒后再查，避免缓存未更新
                }
            } else if ("offline".equals(type)) {// 下线
                for (int i = 0; i < 5; i++) {// 最多尝试5次，可调大，不建议太大
                    List<Instance> allInstances = namingService.getAllInstances(application, groupName, clusters, subscribe);
                    log.info("{},instances:{}", application, allInstances);
                    boolean instanceExist = false;
                    for (Instance instance : allInstances) {
                        if (ip.equals(instance.getIp()) && port == instance.getPort()) {
                            instanceExist = true;
                            break;
                        }
                    }
                    if (!instanceExist) {// 实例不存在则退出
                        break;
                    }
                    Thread.sleep(1000);// 睡眠1秒后再查，避免缓存未更新
                }
            }
            log.info("refresh registry success!!!");
        } catch (Exception e) {
            log.error("refresh registry fail!!!", e);
        }
    }

    /**
     * 刷新注册列表（方案2，但启动刷新会重试满）
     */
    private void refreshRegistry2(String type, String application, String ip, int port) {
        // nacos没有提供能直接刷新注册列表的方法，这里使用比较服务列表数量的方式解决
        NamingService namingService = nacosProperties.namingServiceInstance();

        String groupName = nacosProperties.getGroup();

        String clusterName = nacosProperties.getClusterName();
        List<String> clusters = Lists.newArrayList();
        if (StringUtils.isNotBlank(clusterName)) {
            clusters = Arrays.asList(StringUtils.split(clusterName, ","));
        }
        try {
            // 使用subscribe=true查询，可能从缓存查询
            List<Instance> allInstances1 = namingService.getAllInstances(application, groupName, clusters, true);
            log.info("{},application before:{}", application, allInstances1);

            for (int i = 0; i < 5; i++) {// 最多尝试5次，可调大，不建议太大
                // 使用subscribe=false查询，不会从缓存查询
                List<Instance> allInstances2 = namingService.getAllInstances(application, groupName, clusters, false);
                log.info("{},application after:{}", application, allInstances2);
                if (allInstances2.size() != allInstances1.size()) {
                    // 两次查询服务列表数量不等，说明服务列表有更新
                    break;
                }
                Thread.sleep(1000);// 睡眠1秒后再查，避免缓存未更新
            }
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
