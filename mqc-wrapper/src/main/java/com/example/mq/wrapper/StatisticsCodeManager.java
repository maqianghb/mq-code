package com.example.mq.wrapper;

import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.manager.XueQiuStockManager;
import com.example.mq.wrapper.stock.manager.impl.LocalDataManagerImpl;
import com.example.mq.wrapper.stock.manager.impl.XueQiuStockManagerImpl;
import com.example.mq.wrapper.stock.model.CompanyDTO;
import com.example.mq.wrapper.stock.model.XueQiuStockKLineDTO;
import com.example.mq.wrapper.stock.utils.FileOperateUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsCodeManager {

    public static void main(String[] args) {
        StatisticsCodeManager statisticsCodeManager = new StatisticsCodeManager();

        // ma_1000统计数据
        List<ImmutablePair<String, String>> etfPairList = StockConstant.FOCUS_ETF_CODE_LIST;
        String queryDate ="20230201";
        statisticsCodeManager.queryAndSaveStatisticsMa1000PercentData(etfPairList, queryDate, false);
    }

    /**
     * 查询ma1000值
     * @param etfPairList
     * @param queryDate
     */
    private void queryAndSaveStatisticsMa1000PercentData(List<ImmutablePair<String, String>> etfPairList, String queryDate, Boolean withFocusStockCode){
        if(CollectionUtils.isEmpty(etfPairList) || StringUtils.isBlank(queryDate)){
            return;
        }

        String header ="日期,code,名称,均线差值,差值百分比";

        // ETF数据
        List<String> strDataList =Lists.newArrayList();
        for(ImmutablePair<String, String> etfPair : etfPairList){
            String etfCode =etfPair.getLeft();
            String etfName =etfPair.getRight();
            ImmutablePair<Double, Double> curDiffPair = this.queryMa1000Percent(etfCode, queryDate, StockConstant.KLINE_DAY_COUNT);
            if(curDiffPair ==null){
                continue;
            }

            String msg =new StringBuilder().append(queryDate)
                    .append(",").append(etfCode)
                    .append(",").append(etfName)
                    .append(",").append(NumberUtil.format(curDiffPair.getLeft() * 100, 1)).append("%")
                    .append(",").append(NumberUtil.format(curDiffPair.getRight() * 100, 1)).append("%")
                    .toString();
            strDataList.add(msg);
        }

        // 关注公司数据
        if(withFocusStockCode){
            LocalDataManager localDataManager =new LocalDataManagerImpl();
            List<String> focusCompanyCodeList =localDataManager.getFocusCompanyCodeList();
            for(String focusCompanyCode : focusCompanyCodeList){
                CompanyDTO companyDTO = localDataManager.getLocalCompanyDTO(focusCompanyCode);
                String companyName = companyDTO != null ? companyDTO.getName() : StringUtils.EMPTY;

                ImmutablePair<Double, Double> curDiffPair = this.queryMa1000Percent(focusCompanyCode, queryDate, StockConstant.KLINE_DAY_COUNT);
                if(curDiffPair ==null){
                    continue;
                }

                String msg =new StringBuilder().append(queryDate)
                        .append(",").append(focusCompanyCode)
                        .append(",").append(companyName)
                        .append(",").append(NumberUtil.format(curDiffPair.getLeft() * 100, 1)).append("%")
                        .append(",").append(NumberUtil.format(curDiffPair.getRight() * 100, 1)).append("%")
                        .toString();
                strDataList.add(msg);
            }
        }

        // 记录结果
        String filterListName =String.format(StockConstant.STATISTICS_MA_1000_PERCENT_LIST, queryDate);
        FileOperateUtils.saveLocalFile(filterListName, header, strDataList, false);
    }

    /**
     * 计算K线和MA均线间距的分位值
     * @param stockCode
     * @param totalCount
     * @return
     */
    private ImmutablePair<Double, Double> queryMa1000Percent(String stockCode, String queryDate, Integer totalCount){
        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime queryDateTime = LocalDate.parse(queryDate, df).atStartOfDay();
        long queryDateMills = queryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();
        List<XueQiuStockKLineDTO> kLineDTOList = xueQiuStockManager.queryKLineList(stockCode, "day", queryDateMills, totalCount);
        if(CollectionUtils.isEmpty(kLineDTOList)){
            return null;
        }

        List<XueQiuStockKLineDTO> sortedKLinetDTOList = kLineDTOList.stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        Double curDifference =null;
        Double curDiffRatio =null;
        List<Double> differenceList = Lists.newArrayList();
        for(int i=0; i<sortedKLinetDTOList.size()-1000; i++){
            XueQiuStockKLineDTO curKLineDTO = sortedKLinetDTOList.get(i);
            List<XueQiuStockKLineDTO> tmpKLineDTOList = sortedKLinetDTOList.subList(i, i + 1000);
            double ma_1000_value = tmpKLineDTOList.stream().mapToDouble(XueQiuStockKLineDTO::getClose).sum()/1000;
            differenceList.add(curKLineDTO.getClose() -ma_1000_value);
            if(i ==0){
                curDifference = curKLineDTO.getClose() -ma_1000_value;
                curDiffRatio = (curKLineDTO.getClose() -ma_1000_value) / ma_1000_value;
            }
        }
        List<Double> sortedDifferenceList = differenceList.stream()
                .sorted(Comparator.comparing(difference -> difference))
                .collect(Collectors.toList());
        for(int i=0; i< sortedDifferenceList.size(); i++){
            if(Math.abs(curDifference -sortedDifferenceList.get(i)) <=0.01){
                double percent = i / (sortedDifferenceList.size() * 1.0);
                return new ImmutablePair<>(curDiffRatio, percent);
            }
        }

        return null;
    }
}
