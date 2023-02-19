package com.example.mq.wrapper.stock.model;

import lombok.Data;

/**
 * 现金流数据
 */
@Data
public class XueQiuStockCashFlowDTO extends BaseStockFinanceDTO {

    /**
     * 销售商品、提供劳务收到的现金
     */
    private Double cash_received_of_sales_service;

    /**
     * 收到的税费返还
     */
    private Double refund_of_tax_and_levies;

    /**
     * 收到其他与经营活动有关的现金
     */
    private Double cash_received_of_othr_oa;

    /**
     * 经营活动现金流入小计
     */
    private Double sub_total_of_ci_from_oa;

    /**
     * 购买商品、接受劳务支付的现金
     */
    private Double goods_buy_and_service_cash_pay;

    /**
     * 支付给职工以及为职工支付的现金
     */
    private Double cash_paid_to_employee_etc;

    /**
     * 支付的各项税费
     */
    private Double payments_of_all_taxes;

    /**
     * 支付其他与经营活动有关的现金
     */
    private Double othrcash_paid_relating_to_oa;

    /**
     * 经营活动现金流出小计
     */
    private Double sub_total_of_cos_from_oa;

    /**
     * 经营活动产生的现金流量净额
     */
    private Double ncf_from_oa;

    /**
     * 收回投资收到的现金
     */
    private Double cash_received_of_dspsl_invest;

    /**
     * 取得投资收益收到的现金
     */
    private Double invest_income_cash_received;

    /**
     * 处置固定资产、无形资产和其他长期资产收回的现金净额
     */
    private Double net_cash_of_disposal_assets;

    /**
     * 投资活动现金流入小计
     */
    private Double sub_total_of_ci_from_ia;

    /**
     * 购建固定资产、无形资产和其他长期资产支付的现金
     */
    private Double cash_paid_for_assets;

    /**
     * 投资支付的现金
     */
    private Double invest_paid_cash;

    /**
     * 取得子公司及其他营业单位支付的现金净额
     */
    private Double net_cash_amt_from_branch;

    /**
     * 支付其他与投资活动有关的现金
     */
    private Double othrcash_paid_relating_to_ia;

    /**
     * 投资活动现金流出小计
     */
    private Double sub_total_of_cos_from_ia;

    /**
     * 投资活动产生的现金流量净额
     */
    private Double ncf_from_ia;

    /**
     * 吸收投资收到的现金
     */
    private Double cash_received_from_investor;

    /**
     * 子公司吸收少数股东投资收到的现金
     */
    private Double cash_received_of_absorb_invest;

    /**
     * 取得借款收到的现金
     */
    private Double cash_received_of_borrowing;

    /**
     * 筹资活动现金流入小计
     */
    private Double sub_total_of_ci_from_fa;

    /**
     * 偿还债务支付的现金
     */
    private Double cash_pay_for_debt;

    /**
     * 分配股利、利润或偿付利息支付的现金
     */
    private Double cash_paid_of_distribution;

    /**
     * 子公司支付给少数股东的股利
     */
    private Double branch_paid_to_minority_holder;

    /**
     * 支付其他与筹资活动有关的现金
     */
    private Double othrcash_paid_relating_to_fa;

    /**
     * 筹资活动现金流出小计
     */
    private Double sub_total_of_cos_from_fa;

    /**
     * 筹资活动产生的现金流量净额
     */
    private Double ncf_from_fa;

    /**
     * 汇率变动对现金及现金等价物的影响
     */
    private Double effect_of_exchange_chg_on_cce;

    /**
     * 现金及现金等价物净增加额
     */
    private Double net_increase_in_cce;

    /**
     * 期初现金及现金等价物余额
     */
    private Double initial_balance_of_cce;

    /**
     * 期末现金及现金等价物余额
     */
    private Double final_balance_of_cce;

}
