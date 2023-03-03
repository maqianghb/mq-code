package com.example.mq.wrapper.stock.enums;

import org.apache.commons.lang3.StringUtils;

public enum KLineTypeEnum {

    DAY("day", "日线"),
    WEEK("week", "周线"),
    MONTH("month", "月线");

    private String code;
    private String desc;

    KLineTypeEnum(String code, String desc){
        this.code =code;
        this.desc =desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static KLineTypeEnum getByCode(String code){
        if(StringUtils.isEmpty(code)){
            return null;
        }
        for(KLineTypeEnum typeEnum: KLineTypeEnum.values()){
            if(StringUtils.equals(typeEnum.getCode(), code)){
                return typeEnum;
            }
        }
        return null;
    }
}
