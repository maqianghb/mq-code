package com.example.mq.service.annotation;

import com.example.mq.base.data.HotDsKeyHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class HotDataSourceAspect {

    @Pointcut("@annotation(HotDataSourceSelector)")
    public void hotDataPointCut() {
    }

    @Around(value = "hotDataPointCut()")
    public Object hotDataExecute(ProceedingJoinPoint joinPoint) {
        String dataSourceType = null;
        try {
            //获取方法上的注解
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method realMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(),methodSignature.getParameterTypes());
            HotDataSourceSelector sourceSelector = realMethod.getAnnotation(HotDataSourceSelector.class);
            //获取dataSource类型
            dataSourceType = sourceSelector.dataSource();
        } catch (NoSuchMethodException e) {
            log.error(" err, errMsg:{}", e.getMessage(), e);
        }

        try {
            //切换数据源，需要注意异步线程，以及执行过程中数据源切换可能会产生问题
            HotDsKeyHolder.switchDB(dataSourceType);

            //执行
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("throwable, msg:{}", throwable.getMessage(), throwable);
        }finally {
            HotDsKeyHolder.clearDBKey();
        }

        return null;
    }
}
