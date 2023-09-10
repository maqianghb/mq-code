package com.example.mq.wrapper.stock.model;

import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class AnalyseIndicatorElement {

    private String code;

    private Integer reportYear;

    private String reportType;

    private String kLineDate;

    private CompanyDTO companyDTO;

    private XueQiuStockBalanceDTO curBalanceDTO;

    private XueQiuStockBalanceDTO lastSamePeriodBalanceDTO;

    private XueQiuStockBalanceDTO lastYearBalanceDTO;

    private XueQiuStockIncomeDTO curIncomeDTO;

    private XueQiuStockIncomeDTO lastYearIncomeDTO;

    private XueQiuStockCashFlowDTO curCashFlowDTO;

    private XueQiuStockCashFlowDTO lastYearCashFlowDTO;

    private XueQiuStockIndicatorDTO curIndicatorDTO;

    /**
     * 过去5个季度的单季利润数据
     */
    private List<QuarterIncomeDTO> quarterIncomeDTOList;

    /**
     * 当前季度利润数据
     */
    private QuarterIncomeDTO curQuarterIncomeDTO;

    /**
     * 去年同期单季利润数据
     */
    private QuarterIncomeDTO lastYearQuarterIncomeDTO;

    /**
     * 上一季度单季利润数据
     */
    private QuarterIncomeDTO lastPeriodQuarterIncomeDTO;

    /**
     * 近1000交易日的KLine数据
     */
    private List<XueQiuStockKLineDTO> kLineDTOList;

    private XueQiuStockKLineDTO oneMonthKLineDTO;

    private XueQiuStockKLineDTO threeMonthKLineDTO;

    private XueQiuStockKLineDTO halfYearKLineDTO;

    private XueQiuStockKLineDTO oneYearKLineDTO;

    /**
     * 最新的增减持信息
     */
    private DongChaiHolderIncreaseDTO holderIncreaseDTO;

    /**
     * 解禁信息
     */
    private DongChaiFreeShareDTO freeShareDTO;

}
