package com.example.mq.test.stock.model;

import com.example.mq.test.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.test.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.test.stock.model.dongchai.DongChaiPledgeDataDTO;
import com.example.mq.test.stock.model.xueqiu.*;
import lombok.Data;

import java.util.List;

/**
 * 指标计算源数据
 */
@Data
public class AnalyseIndicatorElement {

    private String code;

    private Integer reportYear;

    private String reportType;

    private String kLineDate;

    private XueQiuCompanyDTO companyDTO;

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

    /**
     * 1周前的k线数据
     */
    private XueQiuStockKLineDTO oneWeekBeforeKLineDTO;

    /**
     * 1月前的k线数据
     */
    private XueQiuStockKLineDTO oneMonthBeforeKLineDTO;

    /**
     * 3月前的k线数据
     */
    private XueQiuStockKLineDTO threeMonthBeforeKLineDTO;

    /**
     * 1月后的k线数据
     */
    private XueQiuStockKLineDTO oneMonthLaterKLineDTO;

    /**
     * 3月后的k线数据
     */
    private XueQiuStockKLineDTO threeMonthLaterKLineDTO;

    /**
     * 半年后的k线数据
     */
    private XueQiuStockKLineDTO halfYearLaterKLineDTO;

    /**
     * 1年后的k线数据
     */
    private XueQiuStockKLineDTO oneYearLaterKLineDTO;

    /**
     * 最新的增减持信息
     */
    private DongChaiHolderIncreaseDTO holderIncreaseDTO;

    /**
     * 解禁信息
     */
    private DongChaiFreeShareDTO freeShareDTO;

    /**
     * 最新的质押信息
     */
    private DongChaiPledgeDataDTO latestPledgeDataDTO;

}
