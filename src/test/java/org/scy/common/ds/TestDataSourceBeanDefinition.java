package org.scy.common.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

/**
 * 测试数据库连接
 * Created by shicy on 2017/6/26.
 */
public class TestDataSourceBeanDefinition implements BeanDefinitionRegistryPostProcessor {

    private Logger logger = LoggerFactory.getLogger(TestDataSourceBeanDefinition.class);

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("do postProcessBeanFactory");
        BeanDefinition definition = beanFactory.getBeanDefinition("dataSource");
        MutablePropertyValues properties = definition.getPropertyValues();
        properties.addPropertyValue("driverClassName", "com.mysql.jdbc.Driver");
        properties.addPropertyValue("url", "jdbc:mysql://127.0.0.1:3306/test?useSSL=false&characterEncoding=utf8");
        properties.addPropertyValue("username", "root");
        properties.addPropertyValue("password", "123456");
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        logger.info("do postProcessBeanDefinitionRegistry");
        try {
            registerBean(registry, "dataSource", Class.forName("org.apache.tomcat.jdbc.pool.DataSource"));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void registerBean(BeanDefinitionRegistry registry, String name, Class<?> beanClass) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);

        ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());

        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, name);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

}
