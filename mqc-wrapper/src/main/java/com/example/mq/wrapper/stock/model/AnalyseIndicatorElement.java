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

    /**
     * 当前的财务数据
     */
    private XueQiuStockBalanceDTO curBalanceDTO;

    /**
     * 去年同期的财务数据
     */
    private XueQiuStockBalanceDTO lastSamePeriodBalanceDTO;

    /**
     * 去年的财务数据
     */
    private XueQiuStockBalanceDTO lastYearBalanceDTO;

    /**
     * 当前的净利润数据
     */
    private XueQiuStockIncomeDTO curIncomeDTO;

    /**
     * 去年的净利润数据
     */
    private XueQiuStockIncomeDTO lastYearIncomeDTO;

    /**
     * 当前的现金流数据
     */
    private XueQiuStockCashFlowDTO curCashFlowDTO;

    /**
     * 去年的现金流数据
     */
    private XueQiuStockCashFlowDTO lastYearCashFlowDTO;

    /**
     * 当前的指标数据
     */
    private XueQiuStockIndicatorDTO curIndicatorDTO;

    /**
     * 去年同期的指标数据
     */
    private XueQiuStockIndicatorDTO lastPeriodIndicatorDTO;

    /**
     * 最近6个季度的单季利润数据
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
     * 去年同期往前一季的利润数据
     */
    private QuarterIncomeDTO lastYearAndLastQuarterIncomeDTO;

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
