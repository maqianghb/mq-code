package com.example.mq.data.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz){
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz){
		return applicationContext.getBean(name, clazz);
	}

	public static List<String> getBeanNames(){
		String[] beanNames =applicationContext.getBeanDefinitionNames();
		if(null ==beanNames && beanNames.length ==0){
			return null;
		}
		return Arrays.asList(beanNames);
	}

	public static Object getProperty(String key){
		if(StringUtils.isEmpty(key)){
			return null;
		}
		return applicationContext.getEnvironment().getProperty(key);
	}
}
