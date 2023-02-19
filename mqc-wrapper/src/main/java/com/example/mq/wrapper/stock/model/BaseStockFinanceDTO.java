package com.example.mq.wrapper.stock.model;

import lombok.Data;

@Data
public class BaseStockFinanceDTO {

    private String code;

    private String name;

    private Integer report_year;

    private String report_type;
}
