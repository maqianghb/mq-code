package com.example.mq.common.exception;

public abstract class MqcException extends RuntimeException{

    private String code;

    private String message;

    public MqcException(){
        super();
    }

    public MqcException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code =errorCode.getCode();
        this.message =errorCode.getMessage();
    }

    public MqcException(ErrorCode errorCode, Throwable throwable){
        super(errorCode.getMessage(), throwable);
        this.code =errorCode.getCode();
        this.message =errorCode.getMessage();
    }

    public MqcException(ErrorCode errorCode, String message){
        super(message);
        this.code =errorCode.getCode();
        this.message =message;
    }

    public MqcException(ErrorCode errorCode, String message, Throwable throwable){
        super(message, throwable);
        this.code =errorCode.getCode();
        this.message =message;
    }

}
