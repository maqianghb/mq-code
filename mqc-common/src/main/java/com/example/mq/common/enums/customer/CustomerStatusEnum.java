package com.example.mq.common.enums.customer;

import java.util.Objects;

public enum CustomerStatusEnum {
    ENABLE(1, "启用"),
    DISABLE(0,"停用");

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    CustomerStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CustomerStatusEnum getByCode(int code){
        for(CustomerStatusEnum statusEnum: CustomerStatusEnum.values()){
            if(Objects.equals(statusEnum.getCode(), code)){
                return statusEnum;
            }
        }

        return null;
    }
}
