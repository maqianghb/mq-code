package com.example.mq.wrapper.stock.model.dongchai;

import lombok.Data;

/**
 * 北上持股数据，by行业汇总数据
 */
@Data
public class DongChaiIndustryHoldShareDTO {

    /**
     * 行业名称
     */
    private String indName;

    /**
     * 日期
     */
    private String tradeDate;

    /**
     * 行业市值（亿元）
     */
    private Double holdMarketCap;

    /**
     * 行业市值占全部北上资金的比例
     */
    private Double industryRatio;

    /**
     * 30天内的行业占比变化
     */
    private Double ind_ratio_chg_30;

    /**
     * 90天内的行业占比变化
     */
    private Double ind_ratio_chg_90;

    /**
     * 360天内的行业占比变化
     */
    private Double ind_ratio_chg_360;

}
