package com.example.mq.wrapper.stock.manager;

import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.model.*;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiIndustryHoldShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDateTime;
import java.util.List;

public interface LocalDataManager {

    /**
     * 查询并保存公司信息
     */
    void queryAndSaveCompanyDTO();

    /**
     * 查询并更新全网K线数据
     *
     */
    void queryAndUpdateKLineList();

    /**
     * 更新资产负债数据
     */
    void queryAndUpdateBalanceData();

    /**
     * 更新利润表数据
     */
    void queryAndUpdateIncomeData();

    /**
     * 更新现金流数据
     */
    void queryAndUpdateCashFlowData();

    /**
     * 更新雪球指标数据
     */
    void queryAndUpdateIndicatorData();

    /**
     * 查询并保存业绩预告数据
     *
     * @param reportDate（格式：2023-12-31）
     */
    void queryAndSaveFinanceNotice(String reportDate);

    /**
     * 查询并保存行业的业绩预告数据
     *
     * @param reportDate
     */
    void queryAndSaveIndFinanceNotice(String reportDate);

    /**
     * 查询并保存股东增减持数据
     *
     * @return
     */
    void queryAndSaveHolderIncreaseList();

    /**
     * 查询并更新沪港通持股数据
     */
    void queryAndUpdateNorthHoldShareList();

    /**
     * 查询全部编码
     *
     * @return
     */
    List<String> getLocalStockCodeList();

    /**
     * 获取黑名单的编码列表
     *
     * @return
     */
    List<String> getBlackStockCodeList();

    /**
     * 获取白名单的编码列表
     *
     * @return
     */
    List<String> getWhiteStockCodeList();

    /**
     * 公司信息查询
     *
     * @param code
     * @return
     */
    CompanyDTO getLocalCompanyDTO(String code);

    /**
     * K线指标查询
     *
     * @param code
     * @param endKLineDateTime k线日期
     * @param typeEnum
     * @param count    截止到k线日期的查询数量(包含k线日期)
     * @return
     */
    List<XueQiuStockKLineDTO> getLocalKLineList(String code, LocalDateTime endKLineDateTime, KLineTypeEnum typeEnum, Integer count);

    /**
     * 负债表指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    XueQiuStockBalanceDTO getLocalBalanceDTO(String code, Integer year, FinanceReportTypeEnum typeEnum);

    /**
     * 利润表指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    XueQiuStockIncomeDTO getLocalIncomeDTO(String code, Integer year, FinanceReportTypeEnum typeEnum);

    /**
     * 单季利润表指标查询
     *
     * @param code
     * @param yearAndReportTypeList
     * @return
     */
    List<QuarterIncomeDTO> getLocalQuarterIncomeDTO(String code, List<ImmutablePair<Integer, FinanceReportTypeEnum>> yearAndReportTypeList);

    /**
     * 现金流指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    XueQiuStockCashFlowDTO getLocalCashFlowDTO(String code, Integer year, FinanceReportTypeEnum typeEnum);

    /**
     * 雪球指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    XueQiuStockIndicatorDTO getLocalXqIndicatorDTO(String code, Integer year, FinanceReportTypeEnum typeEnum);

    /**
     * 查询指定日期的沪港通数据
     *
     * @param stockCodeList
     * @param queryDate 格式：yyyyMMdd
     * @return
     */
    List<DongChaiNorthHoldShareDTO> queryNorthHoldShares(List<String> stockCodeList, String queryDate);

    /**
     * 查询本地文件中的沪港通持股数据
     *
     * @param code 编码
     * @param endTradeTime 交易日期
     * @param count 截止到交易日期的查询数量（包含交易日期）
     * @return
     */
    List<DongChaiNorthHoldShareDTO> queryLocalNorthHoldShareDTOs(String code, LocalDateTime endTradeTime, Integer count);

    /**
     * 查询指定日期的行业持股数据
     *
     * @param queryDate 格式：yyyyMMdd
     * @return
     */
    List<DongChaiIndustryHoldShareDTO> queryIndustryHoldShareDTO(String queryDate);

    /**
     * by行业持股数据
     *
     * @param indName
     * @param queryDate 格式：yyyyMMdd
     * @return
     */
    DongChaiIndustryHoldShareDTO queryIndustryHoldShareDTO(String indName, String queryDate);

}
