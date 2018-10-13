package com.example.mq.service.aop;



import java.lang.annotation.*;

/**
 * 自定义注解，通知ZK
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZkNotify {
    String value() default "/notify/default";
}
