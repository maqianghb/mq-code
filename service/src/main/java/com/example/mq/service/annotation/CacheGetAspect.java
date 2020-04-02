package com.example.mq.service.annotation;

import com.alibaba.fastjson.JSON;
import com.example.mq.base.codis.CodisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CacheGetAspect {

    @Autowired
    private CodisService codisService;

    @Around("@annotation(cacheGet)")
    private Object doCacheGet(ProceedingJoinPoint joinPoint, CacheGet cacheGet){
        if(!isOpenCache()){
            //缓存关闭
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                log.error(" throwable, msg:{}", throwable.getMessage(), throwable);
            }
            return null;
        }

        //get from redis
        String redisKey =null;
        Object redisValue =null;
        try {
            Long customerNo = (Long) getRedisParams(cacheGet.key(), joinPoint.getArgs());
            redisKey =cacheGet.prefix() +customerNo;
            redisValue = getFromRedis(joinPoint, redisKey, customerNo);
        } catch (Exception e) {
            log.warn("cache query error,param is {}", joinPoint.getArgs(), e);
        }
        if (redisValue != null) {
            return redisValue;
        }

        //execute
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(" throwable, msg:{}", throwable.getMessage(), throwable);
        }

        //reset expire time
        if (result != null && StringUtils.isNotEmpty(redisKey)) {
            codisService.setValue(redisKey, JSON.toJSONString(result), cacheGet.expireSeconds());
        }
        return result;
    }

    private boolean isOpenCache(){
        return true;
    }

    private Object getRedisParams(String redisKey, Object[] params){
        return null;
    }

    private Object getFromRedis(ProceedingJoinPoint joinPoint, String redisKey, Long cuatomerNo){
        return null;
    }

}
