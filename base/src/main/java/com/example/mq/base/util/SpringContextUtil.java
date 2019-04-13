package com.example.mq.base.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: springContext操作类
 * @author: maqiang
 * @create: 2018/11/5
 *
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext =applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name){
		return getApplicationContext().getBean(name);
	}

	public static <T> T getBean(Class<T> clazz){
		return getApplicationContext().getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz){
		return getApplicationContext().getBean(name, clazz);
	}

	public static List<String> getBeanNames(){
		String[] beanNames =getApplicationContext().getBeanDefinitionNames();
		if(null ==beanNames && beanNames.length ==0){
			return null;
		}
		return Arrays.asList(beanNames);
	}

	public static Object getProperty(String key){
		if(StringUtils.isEmpty(key)){
			return null;
		}
		return getApplicationContext().getEnvironment().getProperty(key);
	}
}
