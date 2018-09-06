package com.aizhixin.cloud.studentpractice.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    // 获取上下文
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 设置上下文
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (SpringContextUtil.applicationContext == null) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }

    // 通过名字获取上下文中的bean
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    // 通过类型获取上下文中的bean
    public static Object getBean(Class<?> requiredType) {
        return applicationContext.getBean(requiredType);
    }
}
