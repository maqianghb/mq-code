package com.example.mq.controller;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

import com.example.mq.common.utils.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@PropertySource(value = "classpath:application.properties")
@ImportResource({"classpath:application-context-base.xml"})
@ComponentScan(basePackages = { "com.example.mq"})
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableScheduling
public class MqCodeApplication {

    public static void main(String[] args) {
        long startMills =System.currentTimeMillis();
        ApplicationContext context =SpringApplication.run(MqCodeApplication.class, args);

//		printBeanNames();
		System.out.println("server.servlet.context-path: " + SpringContextUtil.getProperty("server.servlet.context-path"));
		System.out.println("应用启动完成，耗时: " + (System.currentTimeMillis()-startMills));
    }

    @PostConstruct
    void inits(){
		//初始化内容

	}

    private static void printBeanNames(){
		List<String> beanNames = SpringContextUtil.getBeanNames();
		if(!CollectionUtils.isEmpty(beanNames)){
			for(String beanName :beanNames){
				System.out.println("beanName:" + beanName);
			}
		}
	}

}
