package com.example.mq.wrapper.stock.model;

import lombok.Data;

@Data
public class CompanyDTO {

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
