package com.example.mq.controller.common;

import com.alibaba.fastjson.JSON;
import com.example.mq.api.common.Response;
import com.example.mq.service.bean.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private Environment environment;

    @ExceptionHandler(Exception.class)
    public Response handlerException(HttpServletRequest request, Exception ex) {
        int respCode = -1;
        String respMsg = StringUtils.isEmpty(ex.getMessage()) ? "系统发生未知错误，请重试.":ex.getMessage();
        try {
            logger.error("request failed, url:{}|params:{}, exception:", JSON.toJSONString(request.getRequestURL()),
                    JSON.toJSONString(request.getParameterMap()), ex);

            if(ex instanceof MyException){
                //自定义异常
                MyException myException = (MyException)ex;
                respCode = myException.getCode();
                respMsg = myException.getDesc();
            }
           return Response.createByFailMsg(respCode, respMsg);
        } catch (Exception e) {
            logger.error("未知错误:"+ e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
