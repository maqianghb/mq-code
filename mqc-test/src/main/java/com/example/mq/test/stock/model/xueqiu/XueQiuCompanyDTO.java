package com.example.mq.test.stock.model.xueqiu;

import lombok.Data;

@Data
public class XueQiuCompanyDTO {

    private String code;

    private String name;

    /**
     * 行业名称
     */
    private String ind_name;

    /**
     * 省份
     */
    private String provincial_name;

}
