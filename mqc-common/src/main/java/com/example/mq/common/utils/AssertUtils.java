package com.example.mq.common.utils;

import com.example.mq.common.exception.BizException;
import com.example.mq.common.exception.ErrorCodeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 断言工具类
 */
public class AssertUtils {

    public static void assertNotNull(Object object, ErrorCodeEnum errorCodeEnum){
        if(object == null){
            throw new BizException(errorCodeEnum);
        }
    }

    public static void assertNotNull(Object object, ErrorCodeEnum errorCodeEnum, String errorMsg){
        if(object ==null){
            throw new BizException(errorCodeEnum, errorMsg);
        }
    }

    public static void assertNotBlank(String str, ErrorCodeEnum errorCodeEnum){
        if(StringUtils.isBlank(str)){
            throw new BizException(errorCodeEnum);
        }
    }

    public static void assertNotBlank(String str, ErrorCodeEnum errorCodeEnum, String errorMsg){
        if(StringUtils.isBlank(str)){
            throw new BizException(errorCodeEnum, errorMsg);
        }
    }

    public static void assertListNotEmpty(List list, ErrorCodeEnum errorCodeEnum){
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(errorCodeEnum);
        }
    }

    public static void assertListNotEmpty(List list, ErrorCodeEnum errorCodeEnum, String errorMsg){
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(errorCodeEnum, errorMsg);
        }
    }

    public static void assertSetNotEmpty(Set set, ErrorCodeEnum errorCodeEnum){
        if(CollectionUtils.isEmpty(set)){
            throw new BizException(errorCodeEnum);
        }
    }

    public static void assertSetNotEmpty(Set set, ErrorCodeEnum errorCodeEnum, String errorMsg){
        if(CollectionUtils.isEmpty(set)){
            throw new BizException(errorCodeEnum, errorMsg);
        }
    }

    public static void assertMapNotEmpty(Map map, ErrorCodeEnum errorCodeEnum){
        if(MapUtils.isEmpty(map)){
            throw new BizException(errorCodeEnum);
        }
    }

    public static void assertMapNotEmpty(Map map, ErrorCodeEnum errorCodeEnum, String errorMsg){
        if(MapUtils.isEmpty(map)){
            throw new BizException(errorCodeEnum, errorMsg);
        }
    }

    public static void assertTrue(boolean condition, ErrorCodeEnum errorCodeEnum){
        if(!condition){
            throw new BizException(errorCodeEnum);
        }
    }

    public static void assertTrue(boolean condition, ErrorCodeEnum errorCodeEnum, String errorMsg){
        if(!condition){
            throw new BizException(errorCodeEnum, errorMsg);
        }
    }

}
