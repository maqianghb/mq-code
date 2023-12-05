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
     * 查询并保存最新的沪港通持股数据
     *
     * @param fileDate
     * @param stockCodeList
     * @param updateLocalData 是否先更新本地数据
     */
    void queryAndSaveNorthHoldShares(String fileDate, List<String> stockCodeList, Boolean updateLocalData);

}
