package com.example.mq.common.exception;

public class SysException extends MqcException{

    private Integer level;

    public SysException() {
    }

    public SysException(String message, Integer level){
        super(ErrorCodeEnum.DEFAULT_BIZ_ERROR, message);
        this.level =level;
    }

    public SysException(ErrorCode errorCode, Integer level){
        super(errorCode);
        this.level =level;
    }

}
