package com.example.mq.adapter.filter;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 14:34:36
 * @Description: 日志切面
 */
@Component
@WebFilter(filterName = "requestLogFilter", urlPatterns = "/*")
@Order(-1)
@Slf4j
public class RequestLogFilter implements Filter {

    private ThreadLocal<Map<String, Object>> threadLocal =new ThreadLocal<>();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletRequest;
        response.setCharacterEncoding("UTF-8");

        // 请求日志打印
        this.printRequest(request);

        filterChain.doFilter(request, response);

        // 响应日志打印
        this.printResponse(request, response);

        // 执行耗时日志
        this.printCostMills();

        this.threadLocal.remove();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private void printRequest(HttpServletRequest request){
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("requestUrl", request.getRequestURL());
            map.put("requestTime", System.currentTimeMillis());
            map.put("requestUuid", UUID.randomUUID().toString().replace("-",""));
            this.threadLocal.set(map);

            Map<String, Object> logMap = new HashMap<>(map);
            logMap.put("requestMethod", request.getMethod());
            logMap.put("contentType", request.getContentType());
            logMap.put("requestParams", this.getRequestParams(request));

            log.info("request start : {}", logMap);
        } catch (Exception e) {
            log.error("print request fail. ", e);
        }
    }

    private String getRequestParams(HttpServletRequest request){
        if(StringUtils.equalsIgnoreCase("GET", request.getMethod())){
            return request.getQueryString();
        }

        if(StringUtils.equalsIgnoreCase("POST", request.getMethod())){
            Map<String, String[]> parameterMap = request.getParameterMap();
            if(MapUtils.isNotEmpty(parameterMap)){
                StringBuilder paramsBuilder =new StringBuilder();
                for(Map.Entry<String, String[]> entry : parameterMap.entrySet()){
                    paramsBuilder.append(entry.getKey()).append("=").append((entry.getValue())[0]).append(";");
                }
                return paramsBuilder.toString();
            }
        }

        return StringUtils.EMPTY;
    }

    private void printResponse(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String, Object> requestMap = this.threadLocal.get();
            long costMills = System.currentTimeMillis() - (Long) requestMap.get("requestTime");


            Map<String, Object> logMap = new HashMap<>();
            logMap.put("requestUrl", requestMap.get("requestUrl"));
            logMap.put("requestUuid", requestMap.get("requestUuid"));
            logMap.put("costMills", costMills);
            logMap.put("responseData", "");

            log.info("request end : {}", logMap);
        } catch (Exception e) {
            log.error("print response fail. ", e);
        }
    }

    private void printCostMills(){
        Map<String, Object> requestMap = this.threadLocal.get();
        long costMills = System.currentTimeMillis() - (Long) requestMap.get("requestTime");
        if(costMills <=3000){
            return;
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("requestUrl", requestMap.get("requestUrl"));
        logMap.put("requestUuid", requestMap.get("requestUuid"));
        logMap.put("costMills", costMills);
        log.info("请求执行耗时较长，请评估是否需要优化，请求信息:{}", logMap);
    }

}
