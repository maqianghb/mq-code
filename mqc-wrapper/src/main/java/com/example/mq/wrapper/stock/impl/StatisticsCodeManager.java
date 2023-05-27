package com.example.mq.wrapper.stock.impl;

import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.model.XueQiuStockKLineDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.util.Lists;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsCodeManager {

    public static void main(String[] args) {
        StatisticsCodeManager statisticsCodeManager =new StatisticsCodeManager();


        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime localDateTime = LocalDateTime.now();//当前时间
        String queryDate = df.format(localDateTime);//格式化为字符串

        List<String> msgList =Lists.newArrayList();
        for(ImmutablePair<String, String> pair : StockConstant.STATISTICS_CODE_LIST){
            String statisticsCode =pair.getLeft();
            String statisticsName =pair.getRight();
            Double percentValue = statisticsCodeManager.queryMa1000Percent(statisticsCode, queryDate, 2000);
            String msg =new StringBuilder()
                    .append("queryDate:").append(queryDate)
                    .append(", code:").append(statisticsCode)
                    .append(", name:").append(statisticsName)
                    .append(", percentValue:").append(percentValue)
                    .append(";")
                    .toString();
            msgList.add(msg);
        }

        msgList.forEach(msg -> System.out.println(msg));
    }

    /**
     * 计算K线和MA均线间距的分位值
     * @param statisticsCode
     * @param totalCount
     * @return
     */
    private Double queryMa1000Percent(String statisticsCode, String queryDate, Integer totalCount){
        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime queryDateTime = LocalDate.parse(queryDate, df).atStartOfDay();
        long queryDateMills = queryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManager();
        List<XueQiuStockKLineDTO> kLineDTOList = xueQiuStockManager.queryKLineList(statisticsCode, "day", queryDateMills, totalCount);
        if(CollectionUtils.isEmpty(kLineDTOList)){
            return null;
        }

        List<XueQiuStockKLineDTO> sortedKLinetDTOList = kLineDTOList.stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        Double curDifference =null;
        List<Double> differenceList = Lists.newArrayList();
        for(int i=0; i<sortedKLinetDTOList.size()-1000; i++){
            XueQiuStockKLineDTO curKLineDTO = sortedKLinetDTOList.get(i);
            List<XueQiuStockKLineDTO> tmpKLineDTOList = sortedKLinetDTOList.subList(i, i + 1000);
            double ma_1000_value = tmpKLineDTOList.stream().mapToDouble(XueQiuStockKLineDTO::getClose).sum()/1000;
            differenceList.add(curKLineDTO.getClose() -ma_1000_value);
            if(i ==0){
                curDifference = curKLineDTO.getClose() -ma_1000_value;
            }
        }
        List<Double> sortedDifferenceList = differenceList.stream()
                .sorted(Comparator.comparing(difference -> difference))
                .collect(Collectors.toList());
        for(int i=0; i< sortedDifferenceList.size(); i++){
            if(Math.abs(curDifference -sortedDifferenceList.get(i)) <=0.01){
                return i/(sortedDifferenceList.size()*1.0);
            }
        }

        return null;
    }
}
