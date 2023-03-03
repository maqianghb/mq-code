package com.example.mq.wrapper.stock.model;

import lombok.Data;

@Data
public class XueQiuStockIndicatorDTO {

    private String code;

    private String name;

    private Integer report_year;

    /**
     * 1季报,中报,3季报,年报
     * Q1,Q2,Q3,Q4
     */
    private String report_type;

    /**
     * 净资产收益率
     */
    private Double avg_roe;

    /**
     * 每股净资产
     */
    private Double np_per_share;

    /**
     * 每股经营现金流
     */
    private Double operate_cash_flow_ps;

    /**
     * 每股收益
     */
    private Double basic_eps;

    /**
     * 每股资本公积金
     */
    private Double capital_reserve;

    /**
     * 净利润同比增长
     */
    private Double undistri_profit_ps;

    /**
     * 总资产报酬率
     */
    private Double net_interest_of_total_assets;

    /**
     * 销售净利率
     */
    private Double net_selling_rate;

    /**
     * 产权比率
     */
    private Double gross_selling_rate;

    /**
     * 营业收入
     */
    private Double total_revenue;

    /**
     * 营业收入同比增长
     */
    private Double operating_income_yoy;

    /**
     * 净利润
     */
    private Double net_profit_atsopc;

    /**
     * 净利润同比增长
     */
    private Double net_profit_atsopc_yoy;

    /**
     * 扣非净利润
     */
    private Double net_profit_after_nrgal_atsolc;

    /**
     * 扣非净利润同比增长
     */
    private Double np_atsopc_nrgal_yoy;

    /**
     * 净资产收益率-摊薄
     */
    private Double ore_dlt;

    /**
     * 人力投入回报率
     */
    private Double rop;

    /**
     * 资产负债率
     */
    private Double asset_liab_ratio;

    /**
     * 流动比率
     */
    private Double current_ratio;

    /**
     * 速动比率
     */
    private Double quick_ratio;

    /**
     * 权益乘数
     */
    private Double equity_multiplier;

    /**
     * 产权比率
     */
    private Double equity_ratio;

    /**
     * 股东权益比率
     */
    private Double holder_equity;

    /**
     * 现金流量比率
     */
    private Double ncf_from_oa_to_total_liab;

    /**
     * 存货周转天数
     */
    private Double inventory_turnover_days;

    /**
     * 应收账款周转天数
     */
    private Double receivable_turnover_days;

    /**
     * 应付账款周转天数
     */
    private Double accounts_payable_turnover_days;

    /**
     * 现金循环周期
     */
    private Double cash_cycle;

    /**
     * 营业周期
     */
    private Double operating_cycle;

    /**
     * 总资产周转率
     */
    private Double total_capital_turnover;

    /**
     * 存货周转率
     */
    private Double inventory_turnover;

    /**
     * 应收账款周转率
     */
    private Double account_receivable_turnover;

    /**
     * 应付账款周转率
     */
    private Double accounts_payable_turnover;

    /**
     * 流动资产周转率
     */
    private Double current_asset_turnover_rate;

    /**
     * 固定资产周转率
     */
    private Double fixed_asset_turnover_ratio;

}
