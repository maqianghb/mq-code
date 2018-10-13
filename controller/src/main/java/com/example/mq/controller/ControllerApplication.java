package com.example.mq.controller;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

/**
 * @author xiehejun
 * @date 2018/5/7 下午4:06
 **/
@SpringBootApplication
@PropertySource(value = "classpath:application-dev.properties")
//@ImportResource({"classpath:dubbo/dubbo-server.xml", "classpath:dubbo/dubbo-client.xml"})
@ComponentScan(basePackages = { "com.example.mq"})
@MapperScan("com.example.mq.service.dao")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableScheduling
public class ControllerApplication {

    public static void main(String[] args) {
        long startTime =System.currentTimeMillis();
        SpringApplication.run(ControllerApplication.class, args);
        System.out.println("-------------------------------");
        System.out.println("---application start success---");
        System.out.println("-------------------------------");
        System.out.println("applicatiuon started in "+(System.currentTimeMillis()-startTime));
    }
    @PostConstruct
    void initZk(){
       //初始化zk服务
    }
}
