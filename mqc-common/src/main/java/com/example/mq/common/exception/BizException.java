package com.example.mq.common.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class BizException extends MqcException{

    public static final String REPLACE_PARAM_STRING ="{}";

    public BizException(){
        super();
    }

    public BizException(String message){
        super(ErrorCodeEnum.DEFAULT_BIZ_ERROR, message);
    }

    public BizException(String msgFormat, Object... params){
        this(formatMessage(msgFormat, params));
    }

    public BizException(ErrorCode errorCode){
        super(errorCode);
    }

    public BizException(ErrorCode errorCode, String message){
        super(errorCode, message);
    }


    public BizException(ErrorCode errorCode, String msfFormat, Object... params){
        super(errorCode, formatMessage(msfFormat, params));
    }


    private static String formatMessage(String msgFormat, Object... params){
        if(params ==null || params.length ==0){
            return msgFormat;
        }

        msgFormat = msgFormat != null ? msgFormat : StringUtils.EMPTY;
        for(Object param : Arrays.asList(params)){
            if(msgFormat.contains(REPLACE_PARAM_STRING)){
                msgFormat =StringUtils.replaceOnce(msgFormat, REPLACE_PARAM_STRING, String.valueOf(param));
            }else {
                msgFormat =msgFormat + String.valueOf(param);
            }
        }

        return msgFormat;
    }

}
