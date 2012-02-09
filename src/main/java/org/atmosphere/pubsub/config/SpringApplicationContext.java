package org.atmosphere.pubsub.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements BeanFactoryAware {

    private static BeanFactory springBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        springBeanFactory = beanFactory;
    }

    public static <T> T getBean(Class<? extends T> klass) {
        return springBeanFactory.getBean(klass);
    }
}
