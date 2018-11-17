package com.example.mq.data.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @program: mq-code
 * @description: spring操作工具
 * @author: maqiang
 * @create: 2018/11/5
 *
 */

public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtil.applicationContext =applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name){
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz){
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz){
		return applicationContext.getBean(name, clazz);
	}
}
