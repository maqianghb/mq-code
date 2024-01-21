package com.example.mq.common.exception;

public enum ErrorCodeEnum implements ErrorCode{

    // 参数校验异常
    PARAM_ERROR("ERROR_PARAM_001", "参数校验异常"),

    // 系统异常
    SYSTEM_ERROR("ERROR_SYSTEM_001", "系统异常"),

    // 远程调用异常
    REMOTE_SERVICE_ERROR("ERROR_REMOTE_SERVICE_001", "远程调用异常"),

    // 默认异常
    DEFAULT_BIZ_ERROR("ERROR_BIZ_001", "默认异常"),

    // 顾客数据异常
    CUSTOMER_DATA_ERROR("ERROR_CUSTOMER_001", "顾客数据异常"),
    CUSTOMER_EMPTY_ERROR("ERROR_CUSTOMER_002", "顾客数据为空"),
    ;

    private String errorCode;

    private String errorMessage;

    ErrorCodeEnum(String errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage =errorMessage;
    }

    @Override
    public String getCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
