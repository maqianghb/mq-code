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
     * 日期
     */
    private String tradeDate;

    /**
     * 持股数量（万股）
     */
    private Double holdShares;

    /**
     * 总股数的持股占比（%）
     */
    private Double totalSharesRatio;

    /**
     * 近1000天持股数的百分位
     */
    private Double holdSharePercent;

    /**
     * 当天增减持数量
     */
    private Double curIncreaseShares;

    /**
     * 当天增持比例
     */
    private Double curIncreaseRatio;

    /**
     * 近30天的增减持数量
     */
    private Double increaseShares_30;

    /**
     * 近30天的增持比例
     */
    private Double increaseRatio_30;

    /**
     * 近60天的增减持数量
     */
    private Double increaseShares_60;

    /**
     * 近60天的增持比例
     */
    private Double increaseRatio_60;

    /**
     * 近90天的增减持数量
     */
    private Double increaseShares_90;

    /**
     * 近90天的增持比例
     */
    private Double increaseRatio_90;

}
