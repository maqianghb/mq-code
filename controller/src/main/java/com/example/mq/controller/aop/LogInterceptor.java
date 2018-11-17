package com.example.mq.controller.aop;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 * @program: webApp
 * @description: ${description}
 * @author: Mr.Ma
 * @create: 2018-08-06 22:58:32
 **/
@Aspect
@Component
public class LogInterceptor {
    private static final Logger LOG =LoggerFactory.getLogger(LogInterceptor.class);

    @Pointcut("execution(public * com.example.mq.controller.api..*(..))")
    public void ctrlPointCut(){}

    @Around("ctrlPointCut()")
    public Object arround(ProceedingJoinPoint joinPoint) throws Throwable{
		Object result = null;
		try {
			String method = joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			long startTime =System.currentTimeMillis();

			result = joinPoint.proceed();

			Map<String, Object> logInfo =new HashMap<>();
			logInfo.put("method", joinPoint.getTarget().getClass().getName()+"."+method);
			logInfo.put("costTime",(System.currentTimeMillis()-startTime));
			logInfo.put("args", JSONObject.toJSONString(args));
			logInfo.put("result", result);
			LOG.info(JSONObject.toJSONString(logInfo));
		} catch (Throwable throwable) {
			throw throwable;
		}
		return result;
    }
}
