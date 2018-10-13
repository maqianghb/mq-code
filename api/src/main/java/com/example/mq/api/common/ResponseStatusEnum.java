package com.example.mq.api.common;

import org.springframework.util.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:02
 */
public enum ResponseStatusEnum {
    SUCCESS(200, "成功"),
    FAIL(-1,"失败"),
    NO_AUTH(-2, "无授权");

    private Integer code;
    private String msg;

    ResponseStatusEnum(Integer code, String msg){
        this.code =code;
        this.msg =msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ResponseStatusEnum getByCode(Integer code){
        if(StringUtils.isEmpty(code)){
            return null;
        }
        for(ResponseStatusEnum statusEnum:ResponseStatusEnum.values()){
            if(statusEnum.code.equals(code)){
                return statusEnum;
            }
        }
        return null;
    }
}
