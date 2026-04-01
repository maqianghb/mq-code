package com.example.mq.test.stock;

import com.example.mq.common.utils.NumberUtil;
import com.example.mq.test.stock.constant.StockConstant;
import com.example.mq.test.stock.manager.XueQiuServiceWrapper;
import com.example.mq.test.stock.manager.impl.XueQiuServiceWrapperImpl;
import com.example.mq.test.stock.model.xueqiu.XueQiuStockKLineDTO;
import com.example.mq.test.stock.utils.FileOperateUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsCodeManager {

    public static void main(String[] args) {
        StatisticsCodeManager statisticsCodeManager = new StatisticsCodeManager();

        // ma_1000统计数据
        String queryDate ="20260331";
        boolean withFocusStockCode =true;
        statisticsCodeManager.queryAndSaveStatisticsMa1000PercentData(StockConstant.FOCUS_ETF_CODE_LIST, queryDate, withFocusStockCode);
    }

    /**
     * 查询ma1000值
     * @param etfCodeNameList
     * @param queryDate
     */
    private void queryAndSaveStatisticsMa1000PercentData(List<String> etfCodeNameList, String queryDate, Boolean withFocusStockCode){
        if(CollectionUtils.isEmpty(etfCodeNameList) || StringUtils.isBlank(queryDate)){
            return;
        }

        List<String> allCodeNameList = new ArrayList<>(etfCodeNameList);
        if(withFocusStockCode){
            // 增加统计代码
            try {
                List<String> statisticsCodeNameList = FileUtils.readLines(new File(StockConstant.STATISTICS_CODE_LIST), Charset.forName("UTF-8"));
                if (CollectionUtils.isNotEmpty(statisticsCodeNameList)) {
                    allCodeNameList.addAll(statisticsCodeNameList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String header ="日期,code,名称,均线差值,差值百分比";

        // 统计数据
        List<String> strDataList =Lists.newArrayList();
        for(String codeAndName : allCodeNameList){
            String[] split = StringUtils.split(codeAndName, ",");
            if(split ==null || split.length !=2){
                continue;
            }

            String code = split[0].trim();
            String name = split[1].trim();
            ImmutablePair<Double, Double> curDiffPair = this.queryMa1000Percent(code, queryDate, StockConstant.KLINE_DAY_COUNT);
            if(curDiffPair ==null){
                continue;
            }

            String msg =new StringBuilder().append(queryDate)
                    .append(",").append(code)
                    .append(",").append(name)
                    .append(",").append(NumberUtil.format( curDiffPair.getLeft(), 2))
                    .append(",").append(NumberUtil.format(curDiffPair.getRight() , 2))
                    .toString();
            strDataList.add(msg);
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

        XueQiuServiceWrapper xueQiuServiceWrapper =new XueQiuServiceWrapperImpl();
        List<XueQiuStockKLineDTO> kLineDTOList = xueQiuServiceWrapper.queryKLineList(stockCode, "day", queryDateMills, totalCount);
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
