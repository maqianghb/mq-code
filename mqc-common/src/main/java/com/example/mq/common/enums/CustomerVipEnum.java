package com.example.mq.common.enums;

import java.util.Objects;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:17
 */
public enum CustomerVipEnum {

    SILVER((short)1, "白银"),
    GOLD((short)2, "黄金"),
    DIAMOND((short)3, "钻石");

    private Short code;
    private String msg;

    CustomerVipEnum(Short code, String msg){
       this.code =code;
       this.msg =msg;
    }

    public Short getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static CustomerVipEnum getByCode(Short code){
        if(Objects.isNull(code)){
            return null;
        }
        for(CustomerVipEnum levelEnum: CustomerVipEnum.values()){
            if(levelEnum.code.equals(code)){
                return levelEnum;
            }
        }
        return null;
    }
}
