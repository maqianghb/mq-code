package com.example.mq.wrapper.stock.model;

import lombok.Data;

import java.util.List;

@Data
public class AnalyseIndicatorElement {

    private String code;

    private Integer reportYear;

    private String reportType;

    private XueQiuStockBalanceDTO curBalanceDTO;

    private XueQiuStockBalanceDTO lastSamePeriodBalanceDTO;

    private XueQiuStockBalanceDTO lastYearBalanceDTO;

    private XueQiuStockIncomeDTO curIncomeDTO;

    private XueQiuStockIncomeDTO lastYearIncomeDTO;

    private XueQiuStockCashFlowDTO curCashFlowDTO;

    private XueQiuStockCashFlowDTO lastYearCashFlowDTO;

    private List<XueQiuStockKLineDTO> kLineDTOList;

    private XueQiuStockIndicatorDTO curIndicatorDTO;

    private List<QuarterIncomeDTO> quarterIncomeDTOList;

    private QuarterIncomeDTO curQuarterIncomeDTO;

    private QuarterIncomeDTO lastSamePeriodQuarterIncomeDTO;

}
