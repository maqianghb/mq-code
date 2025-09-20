package com.example.mq.wrapper.stock.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.DateUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.manager.IndicatorElementManager;
import com.example.mq.wrapper.stock.utils.FileOperateUtils;
import com.example.mq.wrapper.stock.utils.IndicatorFilterUtils;
import com.example.mq.wrapper.stock.utils.PercentDataUtils;
import com.example.mq.wrapper.stock.utils.StockCalculateUtils;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.manager.StockIndicatorManager;
import com.example.mq.wrapper.stock.model.*;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiIndustryHoldShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class StockIndicatorManagerImpl implements StockIndicatorManager {

    private IndicatorElementManager indicatorElementManager =new IndicatorElementManagerImpl();

    private LocalDataManager localDataManager = new LocalDataManagerImpl();

    private static final String INDICATOR_HEADER = "编码,名称,行业,省市,K线差值百分位,总市值,匹配次数,均线次数" +
            ",资产负债率,PE_TTM,pe百分位,市净率,pb百分位,ROE_TTM,去现后的ROE" +
            ",当季营收同比,当季净利同比,上一季营收同比,上一季净利同比,当季毛利率,当季净利率,当季毛利率同比,当季净利率同比,当季毛利率环比,当季净利率环比" +
            ",营收同比,净利润同比,毛利率,净利率" +
            ",固定资产同比,在建工程同比,商誉+无形/净资产,固定资产/净资产,在建工程/净资产" +
            ",现金等价物/短期负债,经营现金流入/营收,经营现金净额/净利润" +
            ",应付票据及账款,应收票据及账款,应付/应收,应收周转天数,存货周转天数,应收周转天数同比,存货周转天数同比" +
            ",增减持预告时间,增减持类型,增减持数量(万股),变动比例(%),解禁时间,解禁数量(万股),占总市值的比例(%)" +
            ",近6季度的毛利率和净利率,近6季度的营收和利润(亿)" +
            ",K线日期,K线数量,收盘价,ma1000值,股东权益合计,营业收入,营业成本,经营现金流入,经营现金净额,净利润" +
            ",近1周的股价波动,近1月的股价波动,近3月的股价波动,1月后股价波动,3月后股价波动,半年后股价波动,1年后股价波动";

    private static final String HOLD_SHARE_HEADER = "编码,名称,行业,日期,沪港通持股数(万),沪港通持有市值(亿),沪港通持股占比(%),近1000天持股数的百分位" +
            ",近7天的增减持数(万),近7天的增持比例(%),近30天的增减持数(万),近30天的增持比例(%),近90天的增减持数(万),近90天的增持比例(%)" +
            ",近180天增减持数(万),近180天增持比例(%),近360天的增减持数(万),近360天的增持比例(%)";
    private static final String IND_HOLD_SHARE_HEADER = "行业,日期,行业总市值,行业总市值占比,沪港通持有市值,占北上总市值的比例(%)" +
            ",沪港通超配比例(%),7天沪港通持股占比变化(%),30天沪港通持股占比变化(%),90天沪港通持股占比变化(%),180天沪港通持股占比变化(%),360天沪港通持股占比变化(%)";

    @Override
    public void calculateAndSaveAllAnalysisDTO(String kLineDate, List<String> stockCodeList, Integer reportYear, FinanceReportTypeEnum reportTypeEnum) {
        // 获取全部股票的指标数据
        List<AnalyseIndicatorDTO> allIndicatorDTOList = stockCodeList.parallelStream()
                .map(stockCode -> {
                    try {
                        // 指标源数据
                        AnalyseIndicatorElement indicatorElement = indicatorElementManager.queryIndicatorElement(stockCode, reportYear, reportTypeEnum, kLineDate);

                        // 计算指标值
                        AnalyseIndicatorDTO analyseIndicatorDTO = StockCalculateUtils.calculateAnalyseIndicatorDTO(indicatorElement);

                        // 指标值格式化
                        StockCalculateUtils.formatAnalyseIndicatorDTO(analyseIndicatorDTO);

                        return analyseIndicatorDTO;
                    } catch (Exception e) {
                        System.out.println("errCode: " + stockCode);
                        e.printStackTrace();
                    }

                    return null;
                })
                .filter(analyseIndicatorDTO -> analyseIndicatorDTO != null)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(allIndicatorDTOList)){
            return ;
        }

        // 生成结果
        List<String> strIndicatorList = allIndicatorDTOList.stream()
                .map(indicatorDTO -> {
                    try {
                        Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
                        StringBuilder indicatorBuilder = new StringBuilder();
                        for (Field field : fields) {
                            Object value = jsonObject.get(field.getName());
                            indicatorBuilder.append(",").append(value);
                        }

                        return indicatorBuilder.toString().substring(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return StringUtils.EMPTY;
                })
                .filter(strIndicator -> StringUtils.isNotBlank(strIndicator))
                .collect(Collectors.toList());

        // 记录全部股票的结果
        String analysisListName = String.format(StockConstant.INDICATOR_LIST_ANALYSIS, kLineDate);
        FileOperateUtils.saveLocalFile(analysisListName, INDICATOR_HEADER, strIndicatorList, false);

        // 按指标筛选数据
        this.filterAndSaveIndicatorDTO(kLineDate, allIndicatorDTOList);

        // 全行业百分位数据
        PercentDataUtils.getAndSaveIndicatorDTOPercent(kLineDate, allIndicatorDTOList);

        // 各行业50百分位指标
        PercentDataUtils.getAndSavePercent50ByIndustry(kLineDate, allIndicatorDTOList);

    }



    /**
     * 筛选并记录结果
     *
     * @param kLineDate
     * @param allIndicatorDTOList
     */
    private void filterAndSaveIndicatorDTO(String kLineDate, List<AnalyseIndicatorDTO> allIndicatorDTOList) {
        // 筛选合适的数据
        List<AnalyseIndicatorDTO> filterIndicatorDTOList = IndicatorFilterUtils.filterByIndicator(allIndicatorDTOList);
        filterIndicatorDTOList = this.addWriteStockCodeList(filterIndicatorDTOList, allIndicatorDTOList);

        List<AnalyseIndicatorDTO> sortedIndicatorDTOList = Optional.ofNullable(filterIndicatorDTOList).orElse(Lists.newArrayList()).stream()
                .filter(analyseIndicatorDTO -> analyseIndicatorDTO.getCur_q_operating_income_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy).reversed())
                .collect(Collectors.toList());

        List<String> strIndicatorDTOList = sortedIndicatorDTOList.stream()
                .map(indicatorDTO -> {
                    try {
                        Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
                        StringBuilder indicatorBuilder = new StringBuilder();
                        for (Field field : fields) {
                            Object value = jsonObject.get(field.getName());
                            indicatorBuilder.append(",").append(value);
                        }

                        return indicatorBuilder.toString().substring(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return StringUtils.EMPTY;
                })
                .filter(strIndicatorDTO -> StringUtils.isNotBlank(strIndicatorDTO))
                .collect(Collectors.toList());


        // 记录结果
        String fileName = String.format(StockConstant.INDICATOR_LIST_FILTER, kLineDate);
        FileOperateUtils.saveLocalFile(fileName, INDICATOR_HEADER, strIndicatorDTOList, false);
    }

    private List<AnalyseIndicatorDTO> addWriteStockCodeList(List<AnalyseIndicatorDTO> filterIndicatorDTOList, List<AnalyseIndicatorDTO> allIndicatorDTOList){
        // 白名单数据处理
        List<String> whiteStockCodeList = localDataManager.getWhiteStockCodeList();
        if(CollectionUtils.isEmpty(whiteStockCodeList)){
            return filterIndicatorDTOList;
        }

        List<String> filterCodeList = filterIndicatorDTOList.stream()
                .map(AnalyseIndicatorDTO::getCode)
                .collect(Collectors.toList());

        Map<String, AnalyseIndicatorDTO> allIndicatorDTOMap = allIndicatorDTOList.stream()
                .collect(Collectors.toMap(AnalyseIndicatorDTO::getCode, val -> val, (val1, val2) -> val1));

        for (String whiteStockCode : whiteStockCodeList) {
            if (!filterCodeList.contains(whiteStockCode)) {
                AnalyseIndicatorDTO tmpIndicatorDTO = allIndicatorDTOMap.get(whiteStockCode);
                if (tmpIndicatorDTO != null) {
                    filterIndicatorDTOList.add(tmpIndicatorDTO);
                }
            }
        }

        return filterIndicatorDTOList;
    }

    @Override
    public void queryAndSaveNorthHoldShares(List<String> stockCodeList, String queryDate) {
        if (CollectionUtils.isEmpty(stockCodeList) || StringUtils.isBlank(queryDate)) {
            return;
        }

        // 查询最新的沪港通持股数据
        List<DongChaiNorthHoldShareDTO> holdShareDTOList = localDataManager.queryNorthHoldShares(stockCodeList, queryDate);
        if (CollectionUtils.isEmpty(holdShareDTOList)) {
            return;
        }

        // 个股数据生成结果
        List<String> strHoldSharesList = holdShareDTOList.stream()
                .map(holdShareDTO -> {
                    try {
                        // 公司信息
                        CompanyDTO companyDTO = localDataManager.getLocalCompanyDTO(holdShareDTO.getCode());
                        if(companyDTO !=null){
                            holdShareDTO.setIndName(companyDTO.getInd_name());
                        }

                        // 沪港通增减持数量和比例
                        this.queryHoldIncreaseShares(holdShareDTO);

                        // 格式化数据
                        StockCalculateUtils.formatNorthHoldShareDTO(holdShareDTO);

                        Field[] fields = DongChaiNorthHoldShareDTO.class.getDeclaredFields();
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(holdShareDTO));
                        StringBuilder strHoldSharesBuilder = new StringBuilder();
                        for (Field field : fields) {
                            Object value = jsonObject.get(field.getName());
                            strHoldSharesBuilder.append(",").append(value);
                        }

                        return strHoldSharesBuilder.toString().substring(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return StringUtils.EMPTY;
                }).filter(strHoldShare -> StringUtils.isNotBlank(strHoldShare))
                .collect(Collectors.toList());

        // 记录结果
        String resultFileName = String.format(StockConstant.LATEST_HOLD_SHARES_FILE, queryDate);
        FileOperateUtils.saveLocalFile(resultFileName, HOLD_SHARE_HEADER, strHoldSharesList, false);
    }

    @Override
    public void queryAndSaveIndustryHoldShares(String queryDate) {
        if(StringUtils.isBlank(queryDate)){
            return ;
        }

        // 查询最新的行业持股数据
        List<DongChaiIndustryHoldShareDTO> industryHoldShareDTOList = localDataManager.queryIndustryHoldShareDTO(queryDate);
        if (CollectionUtils.isEmpty(industryHoldShareDTOList)) {
            return;
        }

        // 行业数据生成结果
        List<String> strHoldSharesList = Lists.newArrayList();
        for (DongChaiIndustryHoldShareDTO indHoldShareDTO : industryHoldShareDTOList) {
            try {
                // by行业持股的比例变化
                this.queryIndustryHoldShareRatioChange(indHoldShareDTO);

                // 格式化数据
                StockCalculateUtils.formatIndNorthHoldShareDTO(indHoldShareDTO);

                Field[] fields = DongChaiIndustryHoldShareDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indHoldShareDTO));
                StringBuilder strHoldSharesBuilder = new StringBuilder();
                for (Field field : fields) {
                    Object value = jsonObject.get(field.getName());
                    strHoldSharesBuilder.append(",").append(value);
                }
                strHoldSharesList.add(strHoldSharesBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        String resultFileName = String.format(StockConstant.LATEST_IND_HOLD_SHARES_FILE, queryDate);
        strHoldSharesList.add(IND_HOLD_SHARE_HEADER);
        FileOperateUtils.saveLocalFile(resultFileName, IND_HOLD_SHARE_HEADER, strHoldSharesList, false);
    }

    /**
     * 补全增持数量和比例
     *
     * @param holdShareDTO
     */
    private void queryHoldIncreaseShares(DongChaiNorthHoldShareDTO holdShareDTO){
        if(holdShareDTO == null || StringUtils.isBlank(holdShareDTO.getCode()) || StringUtils.isBlank(holdShareDTO.getTradeDate())){
            return;
        }

        // 历史沪港通持股数据
        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
        List<DongChaiNorthHoldShareDTO> historyHoldShareDTOList = localDataManager.queryLocalNorthHoldShareDTOs(holdShareDTO.getCode(), tradeDateTime, 400);
        if(CollectionUtils.isEmpty(historyHoldShareDTOList)){
            return;
        }

        // 7日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay7 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 7);
        if(holdShareDTODay7 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay7.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_7(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay7.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay7.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_7(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay7.getTotalSharesRatio(), 2));
            }
        }

        // 30日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay30 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 30);
        if(holdShareDTODay30 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay30.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_30(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay30.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay30.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_30(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay30.getTotalSharesRatio(), 2));
            }
        }

        // 90日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay90 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 90);
        if(holdShareDTODay90 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay90.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_90(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay90.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay90.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_90(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay90.getTotalSharesRatio(), 2));
            }
        }

        // 180日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay180 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 180);
        if(holdShareDTODay180 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay180.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_180(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay180.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay180.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_180(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay180.getTotalSharesRatio(), 2));
            }
        }

        // 360日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay360 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 360);
        if(holdShareDTODay360 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay360.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_360(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay360.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay360.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_360(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay360.getTotalSharesRatio(), 2));
            }
        }

    }

    /**
     * 获取N天前的最近的北上持股数据
     *
     * @param holdShareDTO
     * @param historyHoldShareDTOList
     * @param beforeDayNum
     * @return
     */
    private DongChaiNorthHoldShareDTO getBeforeNorthHoldShareDTO(DongChaiNorthHoldShareDTO holdShareDTO
            , List<DongChaiNorthHoldShareDTO> historyHoldShareDTOList, Integer beforeDayNum){
        if(holdShareDTO ==null || StringUtils.isBlank(holdShareDTO.getTradeDate())){
            return null;
        }
        if(CollectionUtils.isEmpty(historyHoldShareDTOList) || beforeDayNum ==null){
            return null;
        }

        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);

        LocalDateTime beforeDateTime = tradeDateTime.plusDays(-beforeDayNum);
        DongChaiNorthHoldShareDTO beforeHoldShareDTO = historyHoldShareDTOList.stream()
                .filter(hisHoldShareDTO -> StringUtils.isNotBlank(hisHoldShareDTO.getTradeDate()))
                .filter(hisHoldShareDTO -> {
                    LocalDateTime hisDataTime = DateUtil.parseLocalDateTime(hisHoldShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
                    return hisDataTime.isBefore(beforeDateTime) || hisDataTime.isEqual(beforeDateTime);
                })
                .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                .findFirst()
                .orElse(null);

        return beforeHoldShareDTO;
    }

    /**
     * by行业持股比例变化
     *
     * @param holdShareDTO
     */
    private void queryIndustryHoldShareRatioChange(DongChaiIndustryHoldShareDTO holdShareDTO){
        if(holdShareDTO ==null || StringUtils.isBlank(holdShareDTO.getTradeDate())){
            return;
        }

        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
        if(tradeDateTime ==null){
            return;
        }

        // 近7天的变化
        LocalDateTime queryDateTime7 = tradeDateTime.plusDays(-7);
        String queryDate7 = DateUtil.formatLocalDateTime(queryDateTime7, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO7 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate7);
        if(queryHoldShareDTO7 !=null && queryHoldShareDTO7.getIndustryRatio() !=null){
            double ratio_change_7 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO7.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_7(ratio_change_7);
        }

        // 近30天的变化
        LocalDateTime queryDateTime30 = tradeDateTime.plusDays(-30);
        String queryDate30 = DateUtil.formatLocalDateTime(queryDateTime30, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO30 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate30);
        if(queryHoldShareDTO30 !=null && queryHoldShareDTO30.getIndustryRatio() !=null){
            double ratio_change_30 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO30.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_30(ratio_change_30);
        }

        // 近90天的变化
        LocalDateTime queryDateTime90 = tradeDateTime.plusDays(-90);
        String queryDate90 = DateUtil.formatLocalDateTime(queryDateTime90, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO90 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate90);
        if(queryHoldShareDTO90 !=null && queryHoldShareDTO90.getIndustryRatio() !=null){
            double ratio_change_90 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO90.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_90(ratio_change_90);
        }

        // 近180天的变化
        LocalDateTime queryDateTime180 = tradeDateTime.plusDays(-180);
        String queryDate180 = DateUtil.formatLocalDateTime(queryDateTime180, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO180 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate180);
        if(queryHoldShareDTO180 !=null && queryHoldShareDTO180.getIndustryRatio() !=null){
            double ratio_change_180 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO180.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_180(ratio_change_180);
        }

        // 近360天的变化
        LocalDateTime queryDateTime360 = tradeDateTime.plusDays(-360);
        String queryDate360 = DateUtil.formatLocalDateTime(queryDateTime360, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO360 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate360);
        if(queryHoldShareDTO360 !=null && queryHoldShareDTO360.getIndustryRatio() !=null){
            double ratio_change_360 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO360.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_360(ratio_change_360);
        }
    }

}
