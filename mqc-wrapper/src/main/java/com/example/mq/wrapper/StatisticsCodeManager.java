package com.example.mq.wrapper;

import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.manager.XueQiuStockManager;
import com.example.mq.wrapper.stock.manager.impl.XueQiuStockManagerImpl;
import com.example.mq.wrapper.stock.model.XueQiuStockKLineDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
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
        statisticsCodeManager.queryAndSaveStatisticsMa1000PercentData();

    }

    private void queryAndSaveStatisticsMa1000PercentData(){
        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime localDateTime = LocalDateTime.now();//当前时间
        String queryDate = df.format(localDateTime);//格式化为字符串

        List<String> msgList =Lists.newArrayList();
        for(ImmutablePair<String, String> pair : StockConstant.STATISTICS_CODE_LIST){
            String statisticsCode =pair.getLeft();
            String statisticsName =pair.getRight();
            ImmutablePair<Double, Double> curDiffPair = this.queryMa1000Percent(statisticsCode, queryDate, StockConstant.KLINE_DAY_COUNT);
            String msg =new StringBuilder().append(queryDate)
                    .append(",").append(statisticsCode)
                    .append(",").append(statisticsName)
                    .append(",").append(NumberUtil.format(curDiffPair.getLeft() * 100, 1)).append("%")
                    .append(",").append(NumberUtil.format(curDiffPair.getRight() * 100, 1)).append("%")
                    .toString();
            msgList.add(msg);
        }

        List<String> strDataList =Lists.newArrayList();
        strDataList.add("日期,code,名称,均线差值,差值百分比");
        strDataList.addAll(msgList);

        // 记录结果
        try {
            DateTimeFormatter df1 =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String strDateTime = df1.format(localDateTime);//格式化为字符串
            String filterListName =String.format(StockConstant.STATISTICS_MA_1000_PERCENT_LIST, queryDate, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strDataList, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
