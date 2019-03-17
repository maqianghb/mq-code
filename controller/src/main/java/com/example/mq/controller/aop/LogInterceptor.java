package com.example.mq.controller.aop;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.common.Response;
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
    public Response arround(ProceedingJoinPoint joinPoint) throws Throwable{
		Response result = null;
		try {
			String method = joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			long startTime =System.currentTimeMillis();

			//执行
			Object resultObj = joinPoint.proceed();
			if(resultObj instanceof Response){
				result =(Response) resultObj;
			}else {
				LOG.error("处理结果类型未知，无法返回数据，result:{}", JSONObject.toJSONString(resultObj));
				result = Response.createByFailMsg("无法获取正确的返回结果！");
			}

			//log
			Map<String, Object> logDetail =new HashMap<>();
			logDetail.put("method", joinPoint.getTarget().getClass().getName()+"."+method);
			logDetail.put("costTime",(System.currentTimeMillis()-startTime));
			logDetail.put("args", JSONObject.toJSONString(args));
			logDetail.put("result", JSONObject.toJSONString(result));
			LOG.info("controller log detail:{}", JSONObject.toJSONString(logDetail));
		} catch (Throwable throwable) {
			throw throwable;
		}
		return result;
    }
}
