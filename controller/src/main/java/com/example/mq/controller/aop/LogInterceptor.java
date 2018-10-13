package com.example.mq.controller.aop;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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

    @Pointcut("execution(public * com.example.mq.controller.api.*.*(..))")
    public void ctrlPointCut(){}

//    @Before("ctrlPointCut()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request =attributes.getRequest();
        String methodName = joinPoint.getSignature().getName();
        Object[] objects = joinPoint.getArgs();

        Map<String, Object> logInfo = new HashMap<>();
        logInfo.put("reqUrl", request.getRequestURI());
        logInfo.put("reqAdds", request.getRemoteAddr());
        logInfo.put("method", joinPoint.getTarget().getClass().getName()+"."+methodName);
        logInfo.put("args", JSONObject.toJSONString(objects));
        LOG.info(JSONObject.toJSONString(logInfo));
    }

    @Around("ctrlPointCut()")
    public Object arround(ProceedingJoinPoint joinPoint) throws Throwable{
        Object result =null;
        try {
            String methodName = joinPoint.getSignature().getName();
            Object[] objects = joinPoint.getArgs();
            long startTime =System.currentTimeMillis();

            result =joinPoint.proceed();

            Map<String, Object> logInfo = new HashMap<>();
            logInfo.put("method", joinPoint.getTarget().getClass().getName()+"."+methodName);
            logInfo.put("args", JSONObject.toJSONString(objects));
            logInfo.put("result", result);
            logInfo.put("executeTime", System.currentTimeMillis()-startTime);
            LOG.info(JSONObject.toJSONString(logInfo));
        } catch (Throwable throwable) {
            LOG.error("exception:", throwable);
            throw throwable;
        }
        return result;
    }
}
