package com.example.mq.wrapper.stock.manager.impl;

import com.example.mq.common.utils.DateUtil;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.manager.DongChaiDataManager;
import com.example.mq.wrapper.stock.manager.IndicatorElementManager;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.model.*;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author: maqiang
 * @CreateTime: 2025-09-20 00:39:08
 * @Description:
 */
public class IndicatorElementManagerImpl implements IndicatorElementManager {

    private LocalDataManager localDataManager = new LocalDataManagerImpl();
    private DongChaiDataManager dongChaiDataManager = new DongChaiDataManagerImpl();

    /**
     * 查询指标源数据
     *
     * @param code
     * @param year
     * @param typeEnum
     * @param kLineDate
     * @return
     */
    @Override
    public AnalyseIndicatorElement queryIndicatorElement(String code, Integer year, FinanceReportTypeEnum typeEnum, String kLineDate) {
        AnalyseIndicatorElement indicatorElement = new AnalyseIndicatorElement();
        indicatorElement.setCode(code);
        indicatorElement.setReportYear(year);
        indicatorElement.setReportType(typeEnum.getCode());
        indicatorElement.setKLineDate(kLineDate);

        CompanyDTO companyDTO = localDataManager.getLocalCompanyDTO(code);
        indicatorElement.setCompanyDTO(companyDTO);

        this.assembleFinanceElement(indicatorElement, code, year, typeEnum);

        this.assembleKLineElement(indicatorElement, code, kLineDate);

        this.assembleHolderIncreaseElement(indicatorElement, code, kLineDate);

        this.assembleFreeShareElement(indicatorElement, code, kLineDate);

        return indicatorElement;
    }

    /**
     * 财务分析源数据
     *
     * @param indicatorElement
     * @param code
     * @param year
     * @param typeEnum
     */
    private void assembleFinanceElement(AnalyseIndicatorElement indicatorElement, String code, Integer year, FinanceReportTypeEnum typeEnum) {
        if (indicatorElement == null || year == null || typeEnum == null) {
            return;
        }

        XueQiuStockBalanceDTO balanceDTO = localDataManager.getLocalBalanceDTO(code, year, typeEnum);
        if (balanceDTO != null) {
            indicatorElement.setCurBalanceDTO(balanceDTO);
        }

        XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = localDataManager.getLocalBalanceDTO(code, year - 1, typeEnum);
        if (lastSamePeriodBalanceDTO != null) {
            indicatorElement.setLastSamePeriodBalanceDTO(lastSamePeriodBalanceDTO);
        }

        XueQiuStockBalanceDTO lastYearBalanceDTO = localDataManager.getLocalBalanceDTO(code, year - 1, FinanceReportTypeEnum.ALL_YEAR);
        if (lastYearBalanceDTO != null) {
            indicatorElement.setLastYearBalanceDTO(lastYearBalanceDTO);
        }

        XueQiuStockIncomeDTO incomeDTO = localDataManager.getLocalIncomeDTO(code, year, typeEnum);
        if (incomeDTO != null) {
            indicatorElement.setCurIncomeDTO(incomeDTO);
        }

        XueQiuStockIncomeDTO lastYearIncomeDTO = localDataManager.getLocalIncomeDTO(code, year - 1, FinanceReportTypeEnum.ALL_YEAR);
        if (lastYearIncomeDTO != null) {
            indicatorElement.setLastYearIncomeDTO(lastYearIncomeDTO);
        }

        XueQiuStockCashFlowDTO cashFlowDTO = localDataManager.getLocalCashFlowDTO(code, year, typeEnum);
        if (cashFlowDTO != null) {
            indicatorElement.setCurCashFlowDTO(cashFlowDTO);
        }

        XueQiuStockCashFlowDTO lastYearCashFlowDTO = localDataManager.getLocalCashFlowDTO(code, year - 1, FinanceReportTypeEnum.ALL_YEAR);
        if (lastYearCashFlowDTO != null) {
            indicatorElement.setLastYearCashFlowDTO(lastYearCashFlowDTO);
        }

        XueQiuStockIndicatorDTO indicatorDTO = localDataManager.getLocalXqIndicatorDTO(code, year, typeEnum);
        if (indicatorDTO != null) {
            indicatorElement.setCurIndicatorDTO(indicatorDTO);
        }

        XueQiuStockIndicatorDTO lastPeriodIndicatorDTO = localDataManager.getLocalXqIndicatorDTO(code, year - 1, typeEnum);
        if (lastPeriodIndicatorDTO != null) {
            indicatorElement.setLastPeriodIndicatorDTO(lastPeriodIndicatorDTO);
        }

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList = Lists.newArrayList();
        String curQuarterType = StringUtils.EMPTY;
        Integer lastQuarterYear = 0;
        String lastQuarterType = StringUtils.EMPTY;
        if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_1.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_1.getCode();
            lastQuarterYear = year - 1;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_4.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 2, FinanceReportTypeEnum.SINGLE_Q_4));
        } else if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.HALF_YEAR.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_2.getCode();
            lastQuarterYear = year;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_1.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_1));
        } else if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_3.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_3.getCode();
            lastQuarterYear = year;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_2.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2));
        } else if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.ALL_YEAR.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_4.getCode();
            lastQuarterYear = year;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_3.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList = localDataManager.getLocalQuarterIncomeDTO(code, immutablePairList);
        if (CollectionUtils.isNotEmpty(quarterIncomeDTOList)) {
            indicatorElement.setQuarterIncomeDTOList(quarterIncomeDTOList);

            for (QuarterIncomeDTO quarterIncomeDTO : quarterIncomeDTOList) {
                if (Objects.equals(quarterIncomeDTO.getReport_year(), year)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), curQuarterType)) {
                    indicatorElement.setCurQuarterIncomeDTO(quarterIncomeDTO);
                }

                if (Objects.equals(quarterIncomeDTO.getReport_year(), year - 1)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), curQuarterType)) {
                    indicatorElement.setLastYearQuarterIncomeDTO(quarterIncomeDTO);
                }

                if (Objects.equals(quarterIncomeDTO.getReport_year(), lastQuarterYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), lastQuarterType)) {
                    indicatorElement.setLastPeriodQuarterIncomeDTO(quarterIncomeDTO);
                }

                if (Objects.equals(quarterIncomeDTO.getReport_year(), lastQuarterYear -1)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), lastQuarterType)) {
                    indicatorElement.setLastYearAndLastQuarterIncomeDTO(quarterIncomeDTO);
                }
            }
        }

    }

    /**
     * k线源数据
     *
     * @param indicatorElement
     * @param code
     * @param kLineDate
     */
    private void assembleKLineElement(AnalyseIndicatorElement indicatorElement, String code, String kLineDate) {
        if (indicatorElement == null || StringUtils.isBlank(code) || StringUtils.isBlank(kLineDate)) {
            return;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime kLineDateTime = LocalDate.parse(kLineDate, df).atStartOfDay();

        List<XueQiuStockKLineDTO> kLineDTOList = localDataManager.getLocalKLineList(code, kLineDateTime, KLineTypeEnum.DAY, 1000);
        if (CollectionUtils.isNotEmpty(kLineDTOList)) {
            indicatorElement.setKLineDTOList(kLineDTOList);
        }

        // 一周前的k线数据
        LocalDateTime oneWeekBeforeDateTime = kLineDateTime.plusWeeks(-1);
        List<XueQiuStockKLineDTO> oneWeekBeforeKLineDTOList = localDataManager.getLocalKLineList(code, oneWeekBeforeDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(oneWeekBeforeKLineDTOList)) {
            Long kLineTimestamp = oneWeekBeforeKLineDTOList.get(0).getTimestamp();
            long oneWeekBeforeTimestamp = oneWeekBeforeDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(oneWeekBeforeTimestamp -kLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setOneWeekBeforeKLineDTO(oneWeekBeforeKLineDTOList.get(0));
            }
        }

        // 一月前的k线数据
        LocalDateTime oneMonthBeforeDateTime = kLineDateTime.plusMonths(-1);
        List<XueQiuStockKLineDTO> oneMonthBeforeKLineDTOList = localDataManager.getLocalKLineList(code, oneMonthBeforeDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(oneMonthBeforeKLineDTOList)) {
            Long kLineTimestamp = oneMonthBeforeKLineDTOList.get(0).getTimestamp();
            long oneMonthBeforeTimestamp = oneMonthBeforeDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(oneMonthBeforeTimestamp -kLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setOneMonthBeforeKLineDTO(oneMonthBeforeKLineDTOList.get(0));
            }
        }

        // 三月前的k线数据
        LocalDateTime threeMonthBeforeDateTime = kLineDateTime.plusMonths(-3);
        List<XueQiuStockKLineDTO> threeMonthBeforeKLineDTOList = localDataManager.getLocalKLineList(code, threeMonthBeforeDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(threeMonthBeforeKLineDTOList)) {
            Long kLineTimestamp = threeMonthBeforeKLineDTOList.get(0).getTimestamp();
            long threeMonthBeforeTimestamp = threeMonthBeforeDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(threeMonthBeforeTimestamp -kLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setThreeMonthBeforeKLineDTO(threeMonthBeforeKLineDTOList.get(0));
            }
        }

        // 一月后的k线数据
        LocalDateTime oneMonthDateTime = kLineDateTime.plusMonths(1);
        List<XueQiuStockKLineDTO> oneMonthKLineDTOList = localDataManager.getLocalKLineList(code, oneMonthDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(oneMonthKLineDTOList)) {
            Long oneMonthKLineTimestamp = oneMonthKLineDTOList.get(0).getTimestamp();
            long oneMonthTimestamp = oneMonthDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(oneMonthTimestamp -oneMonthKLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setOneMonthLaterKLineDTO(oneMonthKLineDTOList.get(0));
            }
        }

        // 三月后的k线数据
        LocalDateTime threeMonthDateTime = kLineDateTime.plusMonths(3);
        List<XueQiuStockKLineDTO> threeMonthKLineDTOList = localDataManager.getLocalKLineList(code, threeMonthDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(threeMonthKLineDTOList)) {
            Long threeMonthKLineTimestamp = threeMonthKLineDTOList.get(0).getTimestamp();
            long threeMonthTimestamp = threeMonthDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(threeMonthTimestamp -threeMonthKLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setThreeMonthLaterKLineDTO(threeMonthKLineDTOList.get(0));
            }
        }

        // 半年后的k线数据
        LocalDateTime halfYearDateTime = kLineDateTime.plusMonths(6);
        List<XueQiuStockKLineDTO> halfYearKLineDTOList = localDataManager.getLocalKLineList(code, halfYearDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(halfYearKLineDTOList)) {
            Long halfYearKLineTimestamp = halfYearKLineDTOList.get(0).getTimestamp();
            long halfYearTimestamp = halfYearDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (Math.abs(halfYearTimestamp - halfYearKLineTimestamp) <= 7 *24 *3600 *1000) {
                indicatorElement.setHalfYearLaterKLineDTO(halfYearKLineDTOList.get(0));
            }
        }

        // 一年后的k线数据
        LocalDateTime oneYearDateTime = kLineDateTime.plusYears(1);
        List<XueQiuStockKLineDTO> oneYearKLineDTOList = localDataManager.getLocalKLineList(code, oneYearDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(oneYearKLineDTOList)) {
            Long oneYearKLineTimestamp = oneYearKLineDTOList.get(0).getTimestamp();
            long oneYearTimestamp = oneYearDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (Math.abs(oneYearTimestamp - oneYearKLineTimestamp) <= 7 *24 *3600 *1000) {
                indicatorElement.setOneYearLaterKLineDTO(oneYearKLineDTOList.get(0));
            }
        }

    }

    /**
     * 6月内公告的增减持数据
     *
     * @param indicatorElement
     * @param code
     */
    private void assembleHolderIncreaseElement(AnalyseIndicatorElement indicatorElement, String code, String kLineDate) {
        if (indicatorElement == null || StringUtils.isBlank(code)) {
            return;
        }

        LocalDateTime startDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(-180).atStartOfDay();
        LocalDateTime endDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(91).atStartOfDay();


        DongChaiHolderIncreaseDTO holderIncreaseDTO = dongChaiDataManager.getMaxHolderIncreaseDTO(code, startDateTime, endDateTime);
        if (holderIncreaseDTO != null && (holderIncreaseDTO.getChange_num() >50 || holderIncreaseDTO.getAfter_change_rate() >0.05)) {
            indicatorElement.setHolderIncreaseDTO(holderIncreaseDTO);
        }
    }

    /**
     * 解禁数据（前3个月，后6个月）
     *
     * @param indicatorElement
     * @param code
     */
    private void assembleFreeShareElement(AnalyseIndicatorElement indicatorElement, String code, String kLineDate) {
        if (indicatorElement == null || StringUtils.isBlank(code) || StringUtils.isBlank(kLineDate)) {
            return;
        }

        LocalDateTime startDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(-90).atStartOfDay();
        LocalDateTime endDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(180).atStartOfDay();

        DongChaiFreeShareDTO freeShareDTO = dongChaiDataManager.getMaxFreeShareDTO(code, startDateTime, endDateTime);
        if (freeShareDTO != null && (freeShareDTO.getFree_share_num() >100 || freeShareDTO.getTotal_ratio() >0.1)) {
            indicatorElement.setFreeShareDTO(freeShareDTO);
        }
    }

}
