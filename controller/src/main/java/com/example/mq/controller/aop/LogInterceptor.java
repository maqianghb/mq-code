package com.example.mq.controller.aop;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.api.common.Response;
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

    private static long TIME_OUT_LIMIT =200;

    @Pointcut("execution(public * com.example.mq.controller.web.*.*(..)) " +
			" && @annotation(com.example.mq.controller.annotation.MonitorLog)")
    public void ctrlPointCut(){}

    @Around("ctrlPointCut()")
    public Response arround(ProceedingJoinPoint joinPoint) throws Throwable{
		Response result = null;
		try {
			String className =joinPoint.getTarget().getClass().getSimpleName();
			String method = joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();

			//执行
			long startTime=System.currentTimeMillis();
			Object resultObj = joinPoint.proceed();
			if(resultObj instanceof Response){
				result =(Response) resultObj;
			}else {
				LOG.error("处理结果类型未知，无法返回数据，result:{}", JSONObject.toJSONString(resultObj));
				result = Response.createByFailMsg("无法获取正确的返回结果！");
			}

			//log
			long costTimeMills= System.currentTimeMillis() -startTime;
			if (costTimeMills >=TIME_OUT_LIMIT) {
				LOG.warn("request execute timeout, method:{}|costTime:{}|args:{}|result:{}",
						className+"."+method, costTimeMills, JSONObject.toJSONString(args), JSONObject.toJSONString(result));
			} else {
				LOG.info("request execute normal, method:{}|costTime:{}|args:{}|result:{}",
						className+"."+method, costTimeMills, JSONObject.toJSONString(args), JSONObject.toJSONString(result));
			}
		} catch (Throwable throwable) {
			throw throwable;
		}
		return result;
    }
}
