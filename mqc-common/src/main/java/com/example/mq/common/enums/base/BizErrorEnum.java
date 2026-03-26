package com.example.mq.common.enums.base;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 16:15:05
 * @Description:
 */
public enum BizErrorEnum {

    // ******************************** 通用错误码 ********************************
    COMMON_BIZ_ERROR("000000", "通用业务错误"),



    // ******************************** 基础错误码 ********************************
    SYSTEM_BUSY("010000", "系统繁忙"),
    PARAM_INVALID("010001", "请求参数不合法"),
    REMOTE_REQUEST_ERROR("010002", "远程调用失败"),
    DB_OPERATE_ERROR("010003", "DB操作失败"),



    // ******************************** 客户域错误码 ********************************
    CUSTOMER_EMPTY_ERROR("020001", "客户数据为空"),
    CUSTOMER_DATA_INVALID("020002", "客户数据异常"),



    // ******************************** 商户域错误码 ********************************
    SELLER_EMPTY_ERROR("030001", "商户数据为空"),
    SELLER_DATA_INVALID("030002", "商户数据异常"),

    ;

    private String code;
    private String message;

    BizErrorEnum(String code, String message){
        this.code =code;
        this.message =message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString(){
        return code + ":" + message;
    }

}
