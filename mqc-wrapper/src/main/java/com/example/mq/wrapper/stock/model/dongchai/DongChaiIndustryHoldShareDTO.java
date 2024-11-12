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
     * 日期(yyyy-MM-dd HH:mm:ss)
     */
    private String tradeDate;

    /**
     * 行业总市值
     */
    private Double indTotalMarketCap;

    /**
     * 行业总市值占比
     */
    private Double indTotalMarketRatio;

    /**
     * 沪港通持有市值（亿元）
     */
    private Double holdMarketCap;

    /**
     * 沪港通持有市值占全部北上资金的比例
     */
    private Double industryRatio;

    /**
     * 沪港通超配比例
     */
    private Double overHoldRatio;

    /**
     * 7天内沪港通持股占比变化
     */
    private Double ind_ratio_chg_7;

    /**
     * 30天内沪港通持股占比变化
     */
    private Double ind_ratio_chg_30;

    /**
     * 90天内沪港通持股占比变化
     */
    private Double ind_ratio_chg_90;

    /**
     * 180天内沪港通持股占比变化
     */
    private Double ind_ratio_chg_180;

    /**
     * 360天沪港通持股占比变化
     */
    private Double ind_ratio_chg_360;

}
