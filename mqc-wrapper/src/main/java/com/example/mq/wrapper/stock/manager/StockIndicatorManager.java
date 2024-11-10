package com.example.mq.wrapper.stock.manager;

import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;

import java.util.List;

public interface StockIndicatorManager {

    /**
     * 全量分析数据
     *
     * @param kLineDate k线日期
     * @param stockCodeList
     * @param reportYear
     * @param reportTypeEnum
     */
    void calculateAndSaveAllAnalysisDTO(String kLineDate, List<String> stockCodeList, Integer reportYear, FinanceReportTypeEnum reportTypeEnum);

    /**
     * 查询并保存指定日期的沪港通持股数据
     *
     * @param stockCodeList
     * @param queryDate 格式：yyyyMMdd
     */
    void queryAndSaveNorthHoldShares(List<String> stockCodeList, String queryDate);

    /**
     * by行业查询并保存沪港通数据
     *
     * @param queryDate 格式：yyyyMMdd
     */
    void queryAndSaveIndustryHoldShares(String queryDate);

}
