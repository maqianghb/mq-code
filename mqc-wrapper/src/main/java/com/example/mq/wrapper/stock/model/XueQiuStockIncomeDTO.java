package com.example.mq.wrapper.stock.model;

import lombok.Data;

/**
 * 利润数据
 */
@Data
public class XueQiuStockIncomeDTO {

    private String code;

    private String name;

    private Integer report_year;

    /**
     * 1季报,中报,3季报,年报
     * Q1,Q2,Q3,Q4
     */
    private String report_type;

    /**
     * 营业总收入
     */
    private Double total_revenue;

    /**
     * 营业收入
     */
    private Double revenue;

    /**
     * 营业总成本
     */
    private Double operating_costs;

    /**
     * 营业成本
     */
    private Double operating_cost;

    /**
     * 营业税金及附加
     */
    private Double operating_taxes_and_surcharge;

    /**
     * 销售费用
     */
    private Double sales_fee;

    /**
     * 管理费用
     */
    private Double manage_fee;

    /**
     * 研发费用
     */
    private Double rad_cost;

    /**
     * 财务费用
     */
    private Double financing_expenses;

    /**
     * 利息费用
     */
    private Double finance_cost_interest_fee;

    /**
     * 利息收入
     */
    private Double finance_cost_interest_income;

    /**
     * 资产减值损失
     */
    private Double asset_impairment_loss;

    /**
     * 信用减值损失
     */
    private Double credit_impairment_loss;

    /**
     * 公允价值变动收益
     */
    private Double income_from_chg_in_fv;

    /**
     * 投资收益
     */
    private Double invest_income;

    /**
     * 对联营企业和合营企业的投资收益
     */
    private Double invest_incomes_from_rr;

    /**
     * 资产处置收益
     */
    private Double asset_disposal_income;

    /**
     * 其他收益
     */
    private Double other_income;

    /**
     * 营业利润
     */
    private Double op;

    /**
     * 营业外收入
     */
    private Double non_operating_income;

    /**
     * 营业外支出
     */
    private Double non_operating_payout;

    /**
     * 利润总额
     */
    private Double profit_total_amt;

    /**
     * 所得税费用
     */
    private Double income_tax_expenses;

    /**
     * 净利润
     */
    private Double net_profit;

    /**
     * 持续经营净利润
     */
    private Double continous_operating_np;

    /**
     * 归属于母公司股东的净利润
     */
    private Double net_profit_atsopc;

    /**
     * 少数股东损益
     */
    private Double minority_gal;

    /**
     * 扣除非经常性损益后的净利润
     */
    private Double net_profit_after_nrgal_atsolc;


    /**
     * 基本每股收益
     */
    private Double basic_eps;

    /**
     * 稀释每股收益
     */
    private Double dlt_earnings_per_share;

    /**
     * 其他综合收益
     */
    private Double othr_compre_income;

    /**
     * 归属母公司所有者的其他综合收益
     */
    private Double othr_compre_income_atoopc;

    /**
     * 综合收益总额
     */
    private Double total_compre_income;

    /**
     * 归属于母公司股东的综合收益总额
     */
    private Double total_compre_income_atsopc;

    /**
     * 归属于少数股东的综合收益总额
     */
    private Double total_compre_income_atms;

}
