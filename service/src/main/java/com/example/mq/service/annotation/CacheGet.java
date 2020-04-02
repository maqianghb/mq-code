package com.example.mq.service.annotation;


import java.lang.annotation.*;

/**
 * @author Qiang.Ma7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheGet {

    /**
     * 缓存key
     * @return
     */
    String key() default "";

    /**
     * 缓存key的前缀
     * @return
     */
    String prefix() default "";

    /**
     * redis过期时间，单位：秒
     * @return
     */
    int expireSeconds() default 0;
}
