package com.example.mq.wrapper.stock.model.dongchai;

import lombok.Data;

/**
 * 北上持股数据
 */
@Data
public class DongChaiNorthHoldShareDTO {

    private String code;

    private String name;

    /**
     * 行业
     */
    private String indName;

    /**
     * 日期, 格式：yyyy-MM-dd HH:mm:ss
     */
    private String tradeDate;

    /**
     * 沪港通持股数量(万股)
     */
    private Double holdShares;

    /**
     * 沪港通持有市值（亿元）
     */
    private Double holdMarketCap;

    /**
     * 沪港通持股占总股数的百分比(%)
     */
    private Double totalSharesRatio;

    /**
     * 近1000天持股数的百分位
     */
    private Double holdSharePercent;

    /**
     * 近7天的增减持数量(万股)
     */
    private Double increaseShares_7;

    /**
     * 近7天的增持比例(%)
     */
    private Double increaseRatio_7;

    /**
     * 近30天的增减持数量(万股)
     */
    private Double increaseShares_30;

    /**
     * 近30天的增持比例(%)
     */
    private Double increaseRatio_30;

    /**
     * 近90天的增减持数量(万股)
     */
    private Double increaseShares_90;

    /**
     * 近90天的增持比例(%)
     */
    private Double increaseRatio_90;

    /**
     * 近360天的增减持数量(万股)
     */
    private Double increaseShares_360;

    /**
     * 近360天的增持比例(%)
     */
    private Double increaseRatio_360;

    /**
     * 当天增减持数量(万股)
     */
    private Double curIncreaseShares;

    /**
     * 当天增持比例(%)
     */
    private Double curIncreaseRatio;

}
