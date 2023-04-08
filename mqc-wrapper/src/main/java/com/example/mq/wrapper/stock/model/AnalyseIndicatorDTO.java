package com.example.mq.wrapper.stock.model;

import lombok.Data;

@Data
public class AnalyseIndicatorDTO {

    private String code;

    private String name;

    private String kLineDate;

    private Double one_month_price_change;

    private Double three_month_price_change;

    private Double half_year_price_change;

    private Double one_year_price_change;

    /**
     * 资产负债率
     */
    private Double asset_liab_ratio;

    /**
     * 市盈率TTM
     */
    private Double pe;

    /**
     * pe分位值(近1000个交易日)
     */
    private Double pe_p_1000;

    /**
     * 市净率
     */
    private Double pb;

    /**
     * pe分位值(近1000个交易日)
     */
    private Double pb_p_1000;

    /**
     * 股东权益合计
     */
    private Double total_holders_equity;

    /**
     * 净资产收益率TTM
     */
    private Double avg_roe_ttm;

    /**
     * 营业收入
     */
    private Double revenue;

    /**
     * 营业成本
     */
    private Double operating_cost;

    /**
     * 销售毛利率
     */
    private Double gross_margin_rate;

    /**
     * 销售净利率
     */
    private Double net_selling_rate;

    /**
     * 当季销售毛利率
     */
    private Double cur_q_gross_margin_rate;

    /**
     * 当季销售净利率
     */
    private Double cur_q_net_selling_rate;

    /**
     * 当季销售毛利率同比
     */
    private Double cur_q_gross_margin_rate_change;

    /**
     * 当季销售净利率同比
     */
    private Double cur_q_net_selling_rate_change;

    /**
     * 营业收入同比增长
     */
    private Double operating_income_yoy;

    /**
     * 净利润同比增长
     */
    private Double net_profit_atsopc_yoy;

    /**
     * 当季营业收入同比增长
     */
    private Double cur_q_operating_income_yoy;

    /**
     * 当季净利润同比增长
     */
    private Double cur_q_net_profit_atsopc_yoy;

    /**
     * 固定资产同比增长
     */
    private Double fixed_asset_sum_inc;

    /**
     * 在建工程同比增长
     */
    private Double construction_in_process_sum_inc;

    /**
     * 商誉+无形资产/净资产
     */
    private Double gw_ia_assert_rate;

    /**
     * 现金等价物/短期负债
     */
    private Double cash_sl_rate;

    /**
     * 总市值
     */
    private Double market_capital;

    /**
     * 经营活动现金流入小计
     */
    private Double sub_total_of_ci_from_oa;

    /**
     * 经营活动现金流入小计/营业收入
     */
    private Double ci_oi_rate;

    /**
     * 经营活动产生的现金流量净额
     */
    private Double ncf_from_oa;

    /**
     * 净利润
     */
    private Double net_profit_atsopc;

    /**
     * 经营活动产生的现金流量净额/净利润
     */
    private Double ncf_pri_rate;

    /**
     * 应付票据及应付账款
     */
    private Double bp_and_ap;

    /**
     * 应收票据及应收账款
     */
    private Double ar_and_br;

    /**
     * 应付票据及应付账款/应收票据及应收账款
     */
    private Double ap_ar_rate;

    /**
     * 应收账款周转天数
     */
    private Double receivable_turnover_days;

    /**
     * 存货周转天数
     */
    private Double inventory_turnover_days;

}
