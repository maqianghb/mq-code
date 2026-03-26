package com.example.mq.common.exception;

import com.example.mq.common.enums.base.BizErrorEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 16:11:24
 * @Description: 业务异常
 */
public class BusinessException extends RuntimeException{

    private static final String PLACE_HOLDER = "{}";

    private String code;

    private String message;

    public BusinessException(Throwable throwable){
        super(throwable);
    }

    public BusinessException(String message) {
        super(message);
        this.code = BizErrorEnum.COMMON_BIZ_ERROR.getCode();
        this.message = message;
    }

    public BusinessException(BizErrorEnum bizErrorEnum) {
        super(bizErrorEnum.getMessage());
        this.code = bizErrorEnum.getCode();
        this.message = bizErrorEnum.getMessage();
    }

    public BusinessException(BizErrorEnum bizErrorEnum, String message) {
        super(bizErrorEnum.getMessage());
        this.code = bizErrorEnum.getCode();
        this.message = bizErrorEnum.getMessage() + "," + message;
    }

    public BusinessException(BizErrorEnum bizErrorEnum, String errMsgFormat, Object... params) {
        this(bizErrorEnum, buildErrorMsg(errMsgFormat, params));
    }


    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private static String buildErrorMsg(String errMsgFormat, Object... params){
        for(int i=0; i< params.length; i++){
            if(!errMsgFormat.contains(PLACE_HOLDER)){
                return errMsgFormat;
            }

            errMsgFormat = StringUtils.replaceOnce(errMsgFormat, PLACE_HOLDER, String.valueOf(params[i]));
        }

        return errMsgFormat;
    }

}
