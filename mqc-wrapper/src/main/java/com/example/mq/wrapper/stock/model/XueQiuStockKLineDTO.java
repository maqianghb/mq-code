package com.example.mq.wrapper.stock.model;

import lombok.Data;

@Data
public class XueQiuStockKLineDTO {

    private String code;

    private String name;

    /**
     * day, week, month
     */
    private String type;

    /**
     * 时间
     */
    private Long timestamp;

    /**
     * 成交量
     */
    private Long volume;

    /**
     * 开盘价
     */
    private Double open;

    /**
     * 最高价
     */
    private Double high;

    /**
     * 最低价
     */
    private Double low;

    /**
     * 收盘价
     */
    private Double close;

    /**
     * 股价涨幅
     */
    private Double chg;

    /**
     * 涨幅百分比
     */
    private Double percent;

    /**
     * 换手率
     */
    private Double turnoverrate;

    /**
     * 成交额（元）
     */
    private Long amount;

    /**
     * 市盈率（TTM）
     */
    private Double pe;

    /**
     * 市净率
     */
    private Double pb;

    /**
     * 市销率
     */
    private Double ps;

    private Double pcf;

    /**
     * 总市值
     */
    private Double market_capital;



}
