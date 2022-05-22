package com.example.mq.service.annotation;

import java.lang.annotation.*;

/**
 * 默认查codis数据
 *
 * @author Qiang.Ma7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HotDataSourceSelector {

    String dataSource() default "";

    String biz() default "";
}
