package com.example.mq.wrapper.stock.model;

import lombok.Data;

/**
 * 资产负债数据
 */
@Data
public class XueQiuStockBalanceDTO {

    private String code;

    private String name;

    private Integer report_year;

    /**
     * 1季报,中报,3季报,年报
     * Q1,Q2,Q3,Q4
     */
    private String report_type;

    /**
     * 货币资金
     */
    private Double currency_funds;

    /**
     * 交易性金融资产
     */
    private Double tradable_fnncl_assets;

    /**
     * 应收票据及应收账款
     */
    private Double ar_and_br;

    /**
     * 应收票据
     */
    private Double bills_receivable;

    /**
     * 应收账款
     */
    private Double account_receivable;

    /**
     * 预付款项
     */
    private Double pre_payment;

    /**
     * 存货
     */
    private Double inventory;

    /**
     * 合同资产
     */
    private Double contractual_assets;

    /**
     * 一年内到期的非流动资产
     */
    private Double nca_due_within_one_year;

    /**
     * 其他流动资产
     */
    private Double othr_current_assets;

    /**
     * 流动资产合计
     */
    private Double total_current_assets;

    /**
     * 长期应收款
     */
    private Double lt_receivable;

    /**
     * 长期股权投资
     */
    private Double lt_equity_invest;

    /**
     * 其他非流动金融资产
     */
    private Double other_illiquid_fnncl_assets;

    /**
     * 固定资产合计
     */
    private Double fixed_asset_sum;

    /**
     * 在建工程合计
     */
    private Double construction_in_process_sum;

    /**
     * 无形资产
     */
    private Double intangible_assets;

    /**
     * 商誉
     */
    private Double goodwill;

    /**
     * 长期待摊费用
     */
    private Double lt_deferred_expense;

    /**
     * 递延所得税资产
     */
    private Double dt_assets;

    /**
     * 其他非流动资产
     */
    private Double othr_noncurrent_assets;

    /**
     * 非流动资产合计
     */
    private Double total_noncurrent_assets;

    /**
     * 资产合计
     */
    private Double total_assets;

    /**
     * 短期借款
     */
    private Double st_loan;

    /**
     * 交易性金融负债
     */
    private Double tradable_fnncl_liab;

    /**
     * 应付票据及应付账款
     */
    private Double bp_and_ap;

    /**
     * 应付票据
     */
    private Double bill_payable;

    /**
     * 应付账款
     */
    private Double accounts_payable;

    /**
     * 合同负债
     */
    private Double contract_liabilities;

    /**
     * 应付职工薪酬
     */
    private Double payroll_payable;

    /**
     * 应交税费
     */
    private Double tax_payable;

    /**
     * 一年内到期的非流动负债
     */
    private Double noncurrent_liab_due_in1y;

    /**
     * 其他流动负债
     */
    private Double othr_current_liab;

    /**
     * 流动负债合计
     */
    private Double total_current_liab;

    /**
     * 长期借款
     */
    private Double lt_loan;

    /**
     * 长期应付款合计
     */
    private Double lt_payable_sum;

    /**
     * 预计负债
     */
    private Double estimated_liab;

    /**
     * 递延所得税负债
     */
    private Double dt_liab;

    /**
     * 递延收益-非流动负债
     */
    private Double noncurrent_liab_di;

    /**
     * 其他非流动负债
     */
    private Double othr_non_current_liab;

    /**
     * 非流动负债合计
     */
    private Double total_noncurrent_liab;

    /**
     * 负债合计
     */
    private Double total_liab;

    /**
     * 实收资本(或股本)
     */
    private Double shares;

    /**
     * 资本公积
     */
    private Double capital_reserve;

    /**
     * 库存股
     */
    private Double treasury_stock;

    /**
     * 其他综合收益
     */
    private Double othr_compre_income;

    /**
     * 盈余公积
     */
    private Double earned_surplus;

    /**
     * 未分配利润
     */
    private Double undstrbtd_profit;

    /**
     * 归属于母公司股东权益合计
     */
    private Double total_quity_atsopc;

    /**
     * 少数股东权益
     */
    private Double minority_equity;

    /**
     * 股东权益合计
     */
    private Double total_holders_equity;

    /**
     * 负债和股东权益总计
     */
    private Double total_liab_and_holders_equity;

    /**
     * 资产负债率
     */
    private Double asset_liab_ratio;

}
