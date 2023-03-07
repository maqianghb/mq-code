package com.example.mq.wrapper.stock.model;

import lombok.Data;

import java.util.List;

@Data
public class AnalyseIndicatorElement {

    private String code;

    private Integer reportYear;

    private String reportType;

    private XueQiuStockBalanceDTO curBalanceDTO;

    private XueQiuStockIncomeDTO curIncomeDTO;

    private XueQiuStockCashFlowDTO curCashFlowDTO;

    private XueQiuStockIndicatorDTO curIndicatorDTO;

    private List<XueQiuStockKLineDTO> kLineDTOList;

    private XueQiuStockBalanceDTO lastYearBalanceDTO;

    private XueQiuStockIncomeDTO lastYearIncomeDTO;

    private XueQiuStockCashFlowDTO lastYearCashFlowDTO;

    private List<QuarterIncomeDTO> quarterIncomeDTOList;

    private QuarterIncomeDTO curQuarterIncomeDTO;

    private QuarterIncomeDTO lastYearQuarterIncomeDTO;

}
