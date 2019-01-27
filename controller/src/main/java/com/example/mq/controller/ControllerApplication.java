package com.example.mq.controller;

import java.util.Arrays;
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

import com.example.mq.data.util.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;


@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@PropertySource(value = "classpath:application.properties")
@ImportResource({"classpath:applicationContext-base.xml"})
@ComponentScan(basePackages = { "com.example.mq"})
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableScheduling
public class ControllerApplication {

    public static void main(String[] args) {
        long startTime =System.currentTimeMillis();
        ApplicationContext context =SpringApplication.run(ControllerApplication.class, args);
		System.out.println("server.servlet.context-path:"+ context.getEnvironment().getProperty("server.servlet.context-path"));
		System.out.println("server.servlet.context-path:"+ SpringContextUtil.getProperty("server.servlet.context-path"));
		printBeanNames(context);

		System.out.println("------applicatiuon started in "+(System.currentTimeMillis()-startTime));
    }

    @PostConstruct
    void initZk(){
       //初始化zk服务
    }

    private static void printBeanNames(ApplicationContext context){
		if(null == context){
			return;
		}
		List<String> beanNames = Arrays.asList(context.getBeanDefinitionNames());
		if(!CollectionUtils.isEmpty(beanNames)){
			for(String beanName :beanNames){
				System.out.println("beanName:" + beanName);
			}
		}
	}

}
