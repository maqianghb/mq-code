package com.example.mq.app.utils;

import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-26 17:45:12
 * @Description:
 */
@Slf4j
public class ExpressRunnerUtils {

    private static final ExpressRunner expressRunner =new ExpressRunner();

    /**
     * 表达式计算
     *
     * @param conditionExpress
     * @param params
     * @return
     */
    public static boolean checkCondition(String conditionExpress, JSONObject params){
        if(StringUtils.isBlank(conditionExpress) || params ==null){
            return false;
        }

        DefaultContext defaultContext =buildExpressContext(params);

        try {
            Object executeResult = expressRunner.execute(conditionExpress, defaultContext, null, true, false);
            if(executeResult !=null && executeResult instanceof Boolean){
                return (Boolean) executeResult;
            }
        } catch (Exception e) {
            log.warn("表达式引擎计算失败, conditionExpress:{}, context:{}", conditionExpress, JSONObject.toJSONString(defaultContext), e);
        }

        return false;
    }

    /**
     * 参数转换
     *
     * @param params
     * @return
     */
    private static DefaultContext buildExpressContext(JSONObject params){
        DefaultContext defaultContext =new DefaultContext();
        for(Map.Entry<String, Object> entry : params.entrySet()){
            defaultContext.put(entry.getKey(), entry.getValue());
        }

        return defaultContext;
    }

}
