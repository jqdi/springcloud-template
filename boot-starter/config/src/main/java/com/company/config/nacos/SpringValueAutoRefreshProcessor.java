package com.company.config.nacos;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 实现springcloud应用@Value配置的自动刷新(跟nacos无关，但是apollo无需使用该类，所以只给nacos使用)
 * <p>
 * 参考网页：https://blog.csdn.net/echizao1839/article/details/122319170
 */
@Slf4j
@SuppressWarnings("ALL")
public class SpringValueAutoRefreshProcessor extends AutowiredAnnotationBeanPostProcessor implements BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    private final Class<? extends Annotation> refreshAnnotationType = Value.class;

    private final Set<String> beanNamesNeedRefresh = new HashSet<>();

    public SpringValueAutoRefreshProcessor() {
        super.setAutowiredAnnotationType(this.refreshAnnotationType);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        //do nothing
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.processNeedRefresh(beanName, bean.getClass());
        return bean;
    }

    @Override
    public void setOrder(int order) {
        super.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
    }

    public void changedKeys(Set<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        log.info("changed keys: {}", keys);
        for (String beanName : beanNamesNeedRefresh) {
            super.processInjection(beanFactory.getBean(beanName));
        }
        log.info("changed keys refresh finish");
    }

    private void processNeedRefresh(final String beanName, final Class<?> clazz) {
        if (beanNamesNeedRefresh.contains(beanName)) {
            return;
        }
        Class<?> targetClass = clazz;
        do {
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (beanNamesNeedRefresh.contains(beanName) || Modifier.isStatic(field.getModifiers())) {
                    return;
                }
                AnnotationAttributes attributes = findAutowiredAnnotation(field);
                if (Objects.nonNull(attributes)) {
                    beanNamesNeedRefresh.add(beanName);
                }
            });
            if (!beanNamesNeedRefresh.isEmpty()) {
                break;
            }
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                if (beanNamesNeedRefresh.contains(beanName) || method.getParameterCount() == 0 || Modifier.isStatic(method.getModifiers())) {
                    return;
                }
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                AnnotationAttributes attributes = findAutowiredAnnotation(bridgedMethod);
                if (Objects.nonNull(attributes) && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    beanNamesNeedRefresh.add(beanName);
                }
            });
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }

    private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
        if (ao.getAnnotations().length > 0) {
            return AnnotatedElementUtils.getMergedAnnotationAttributes(ao, refreshAnnotationType);
        }
        return null;
    }
}