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
     * 查询并保存全网财务数据
     *
     */
    void queryAndUpdateFinanceList();

    /**
     * 查询并保存业绩预告数据
     *
     * @param reportDate
     */
    void queryAndSaveFinanceNotice(String reportDate);

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
     * 查询最近日期的沪港通数据
     *
     * @param stockCodeList
     * @param updateLocalData 是否更新本地文件
     * @return
     */
    List<DongChaiNorthHoldShareDTO> queryLatestNorthHoldShares(List<String> stockCodeList, Boolean updateLocalData);

    /**
     * 查询最新的行业持股数据
     *
     * @return
     */
    List<DongChaiIndustryHoldShareDTO> queryLatestIndustryHoldShareDTO();

}
