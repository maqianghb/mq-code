package com.example.mq.service.aop;


import com.example.mq.service.annotation.ZkNotify;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ZkNotifyAspect {
    Logger logger = LoggerFactory.getLogger(ZkNotifyAspect.class);

//    @Autowired
//    private ZookeeperService zookeeperService;

	@Pointcut("@annotation(com.example.mq.service.annotation.ZkNotify)")
	public void zkNotifyPointCut(){}

	@Around(value = "zkNotifyPointCut()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {

            result = joinPoint.proceed();

            logger.info("zk notify start");
            //获取方法上的ZkNotify注解
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method realMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(),methodSignature.getParameterTypes());
            ZkNotify zkNotify = realMethod.getAnnotation(ZkNotify.class);

            //发送zk通知
//            zookeeperService.notify(zkNotify.value(), String.valueOf(System.currentTimeMillis()));
        } catch (Throwable throwable) {
            throw throwable;
        }
        return result;
    }
}
