package com.example.mq.common.utils;

import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 16:09:59
 * @Description:
 */
public class AssertUtils {

    // **************************** object空校验 ****************************
    private static void assertNotNull(Object object, BusinessException businessException){
        if(object ==null){
            throw businessException;
        }
    }

    public static void assertNotNull(Object object, BizErrorEnum bizErrorEnum){
        BusinessException businessException =new BusinessException(bizErrorEnum);
        assertNotNull(object, businessException);
    }

    public static void assertNotNull(Object object, String errMsgFormat, Object... params){
        BusinessException businessException =new BusinessException(BizErrorEnum.COMMON_BIZ_ERROR, errMsgFormat, params);
        assertNotNull(object, businessException);
    }

    public static void assertNotNull(Object object, BizErrorEnum bizErrorEnum, String errMsgFormat, Object... params){
        BusinessException businessException =new BusinessException(bizErrorEnum, errMsgFormat, params);
        assertNotNull(object, businessException);
    }


    // **************************** condition校验 ****************************
    private static void assertTrue(Boolean condition, BusinessException businessException){
        if(true == condition){
            throw businessException;
        }
    }

    public static void assertTrue(Boolean condition, BizErrorEnum bizErrorEnum){
        BusinessException businessException =new BusinessException(bizErrorEnum);
        assertTrue(condition, businessException);
    }

    public static void assertTrue(Boolean condition, String errMsgFormat, Object... params){
        BusinessException businessException =new BusinessException(BizErrorEnum.COMMON_BIZ_ERROR, errMsgFormat, params);
        assertTrue(condition, businessException);
    }

    public static void assertTrue(Boolean condition, BizErrorEnum bizErrorEnum, String errMsgFormat, Object... params){
        BusinessException businessException =new BusinessException(bizErrorEnum, errMsgFormat, params);
        assertTrue(condition, businessException);
    }


    // **************************** string空校验 ****************************
    private static void assertNotBlank(String str, BusinessException businessException){
        if(StringUtils.isBlank(str)){
            throw businessException;
        }
    }

    public static void assertNotBlank(String str, BizErrorEnum bizErrorEnum){
        BusinessException businessException =new BusinessException(bizErrorEnum);
        assertNotBlank(str, businessException);
    }

    public static void assertNotBlank(String str, String errMsgFormat, Object... params){
        BusinessException businessException =new BusinessException(BizErrorEnum.COMMON_BIZ_ERROR, errMsgFormat, params);
        assertNotBlank(str, businessException);
    }

    public static void assertNotBlank(String str, BizErrorEnum bizErrorEnum, String errMsgFormat, Object... params){
        BusinessException businessException =new BusinessException(bizErrorEnum, errMsgFormat, params);
        assertNotBlank(str, businessException);
    }

}
