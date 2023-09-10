package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.util.Lists;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StockIndicatorManager {

    private static final String HEADER = "编码,名称,行业,省市,K线差值百分位,总市值,匹配次数,均线相等次数" +
            ",资产负债率,PE_TTM,pe百分位,市净率,pb百分位,净资产收益率,去现后的ROE" +
            ",当季毛利率,当季净利率,当季毛利率同比,当季净利率同比,当季毛利率环比,当季净利率环比" +
            ",当季营收同比,当季净利润同比,营收同比,净利润同比,毛利率,净利率" +
            ",固定资产同比,在建工程同比,商誉+无形/净资产,固定资产/净资产,在建工程/净资产" +
            ",现金等价物/短期负债,经营现金流入/营收,经营现金净额/净利润" +
            ",应付票据及应付账款,应收票据及应收账款,应付票据及应付账款/应收票据及应收账款,应收账款周转天数,存货周转天数" +
            ",增减持类型,增减持数量(万股),变动比例(%),解禁时间,解禁数量(万股),占总市值的比例(%)" +
            ",近5季度的毛利率和净利率,近5季度的营收和利润(亿)" +
            ",K线日期,K线数量,收盘价,ma1000值,股东权益合计,营业收入,营业成本,经营现金流入,经营现金净额,净利润" +
            ",1月后股价波动,3月后股价波动,半年后股价波动,1年后股价波动";

    public static void main(String[] args) {
        StockIndicatorManager manager =new StockIndicatorManager();

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
//        List<String> stockCodeList =StockConstant.TEST_STOCK_CODE_LIST;

        String kLineDate = StockConstant.FILE_DATE;
        Integer reportYear = 2023;
        FinanceReportTypeEnum reportTypeEnum =FinanceReportTypeEnum.HALF_YEAR;

        manager.saveAndStatisticsAllAnalysisDTO(kLineDate, stockCodeList, reportYear, reportTypeEnum);
    }

    /**
     * 全量的分析数据
     *
     */
    private void saveAndStatisticsAllAnalysisDTO(String kLineDate, List<String> stockCodeList, Integer reportYear, FinanceReportTypeEnum reportTypeEnum){
        // 获取全部指标数据
        List<AnalyseIndicatorDTO> allIndicatorDTOList = Lists.newArrayList();
        for(String stockCode : stockCodeList){
            try {
                AnalyseIndicatorElement indicatorElement = this.getIndicatorElement(StockConstant.FILE_DATE
                        , stockCode, reportYear, reportTypeEnum, kLineDate);
                AnalyseIndicatorDTO analyseIndicatorDTO = this.getAnalyseIndicatorDTO(indicatorElement);
                this.formatAnalyseIndicatorDTO(analyseIndicatorDTO);

                allIndicatorDTOList.add(analyseIndicatorDTO);
            } catch (Exception e) {
                System.out.println("errCode: " + stockCode);
                e.printStackTrace();
            }
        }

        // 生成结果
        List<String> strIndicatorList =Lists.newArrayList();
        strIndicatorList.add(HEADER);
        for(AnalyseIndicatorDTO indicatorDTO : allIndicatorDTOList){
            try {
                Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
                StringBuilder indicatorBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName ());
                    indicatorBuilder.append(",").append(value);
                }
                strIndicatorList.add(indicatorBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String analysisListName =String.format(StockConstant.INDICATOR_LIST_ANALYSIS, kLineDate, strDateTime);
            FileUtils.writeLines(new File(analysisListName), "UTF-8", strIndicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("indicatorList: "+ JSON.toJSONString(strIndicatorList));

        // 按行业统计百分位数据
        this.getAndSaveIndicatorDTOPercent(kLineDate, allIndicatorDTOList);

        // 按指标筛选数据
        this.filterAndSaveIndicatorDTO(kLineDate, allIndicatorDTOList);
    }

    /**
     * 计算百分位数据
     *
     * @param indicatorDTOList
     * @return
     */
    private void getAndSaveIndicatorDTOPercent(String kLineDate, List<AnalyseIndicatorDTO> indicatorDTOList) {
        if(StringUtils.isBlank(kLineDate) || CollectionUtils.isEmpty(indicatorDTOList)){
            return ;
        }

        String header ="行业,指标,10分位,25分位,50分位,75分位,90分位";
        List<String> strPercentList =Lists.newArrayList();
        strPercentList.add(header);

        Map<String, List<AnalyseIndicatorDTO>> indNameAndIndicatorDTOMap = indicatorDTOList.stream()
                .filter(indicatorDTO -> StringUtils.isNotBlank(indicatorDTO.getInd_name()))
                .collect(Collectors.groupingBy(AnalyseIndicatorDTO::getInd_name));
        Set<String> indNameSet = indNameAndIndicatorDTOMap.keySet();

        // 增加全行业数据
        indNameAndIndicatorDTOMap.put("全行业", new ArrayList<>(indicatorDTOList));
        List<String> indNameList =Lists.newArrayList();
        indNameList.add("全行业");
        indNameList.addAll(indNameSet);

        for(String indName : indNameList){
            List<AnalyseIndicatorDTO> tmpIndicatorDTOList = indNameAndIndicatorDTOMap.get(indName);
            if(CollectionUtils.isEmpty(tmpIndicatorDTOList)){
                continue;
            }

            // ROE_TTM
            List<Double> avg_roe_ttm_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm))
                    .map(AnalyseIndicatorDTO::getAvg_roe_ttm)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(avg_roe_ttm_list)){
                String msg = this.getIndicatorPercentValue(indName, "ROE_TTM", avg_roe_ttm_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // ROE_TTM_V1
            List<Double> avg_roe_ttm_v1_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm_v1() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm_v1))
                    .map(AnalyseIndicatorDTO::getAvg_roe_ttm_v1)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(avg_roe_ttm_v1_list)){
                String msg = this.getIndicatorPercentValue(indName, "ROE_TTM_v1", avg_roe_ttm_v1_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // pe
            List<Double> pe_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPe() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe))
                    .map(AnalyseIndicatorDTO::getPe)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(pe_list)){
                String msg = this.getIndicatorPercentValue(indName, "pe", pe_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // pe_p_1000
            List<Double> pe_p_1000_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPe_p_1000() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe_p_1000))
                    .map(AnalyseIndicatorDTO::getPe_p_1000)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(pe_p_1000_list)){
                String msg = this.getIndicatorPercentValue(indName, "pe_p_1000", pe_p_1000_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // pb
            List<Double> pb_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPb() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb))
                    .map(AnalyseIndicatorDTO::getPb)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(pb_list)){
                String msg = this.getIndicatorPercentValue(indName, "pb", pb_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // pb_p_100
            List<Double> pb_p_1000_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPb_p_1000() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb_p_1000))
                    .map(AnalyseIndicatorDTO::getPb_p_1000)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(pb_p_1000_list)){
                String msg = this.getIndicatorPercentValue(indName, "pb_p_100", pb_p_1000_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 毛利率
            List<Double> gross_margin_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getGross_margin_rate() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getGross_margin_rate))
                    .map(AnalyseIndicatorDTO::getGross_margin_rate)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(gross_margin_rate_list)){
                String msg = this.getIndicatorPercentValue(indName, "毛利率", gross_margin_rate_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 净利率
            List<Double> net_selling_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getNet_selling_rate() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_selling_rate))
                    .map(AnalyseIndicatorDTO::getNet_selling_rate)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(net_selling_rate_list)){
                String msg = this.getIndicatorPercentValue(indName, "净利率", net_selling_rate_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 营收同比
            List<Double> operating_income_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOperating_income_yoy))
                    .map(AnalyseIndicatorDTO::getOperating_income_yoy)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(operating_income_yoy_list)){
                String msg = this.getIndicatorPercentValue(indName, "营收同比", operating_income_yoy_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 净利润同比
            List<Double> net_profit_atsopc_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getNet_profit_atsopc_yoy() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy))
                    .map(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(net_profit_atsopc_yoy_list)){
                String msg = this.getIndicatorPercentValue(indName, "净利润同比", net_profit_atsopc_yoy_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 当季毛利率
            List<Double> cur_q_gross_margin_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate))
                    .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(cur_q_gross_margin_rate_list)){
                String msg = this.getIndicatorPercentValue(indName, "当季毛利率", cur_q_gross_margin_rate_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 当季净利率
            List<Double> cur_q_net_selling_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate))
                    .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(cur_q_net_selling_rate_list)){
                String msg = this.getIndicatorPercentValue(indName, "当季净利率", cur_q_net_selling_rate_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 当季营收同比
            List<Double> cur_q_operating_income_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_operating_income_yoy() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy))
                    .map(AnalyseIndicatorDTO::getCur_q_operating_income_yoy)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(cur_q_operating_income_yoy_list)){
                String msg = this.getIndicatorPercentValue(indName, "当季营收同比", cur_q_operating_income_yoy_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 当季净利润同比
            List<Double> cur_q_net_profit_atsopc_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_net_profit_atsopc_yoy() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy))
                    .map(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(cur_q_net_profit_atsopc_yoy_list)){
                String msg = this.getIndicatorPercentValue(indName, "当季净利润同比", cur_q_net_profit_atsopc_yoy_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 当季净利润同比
            List<Double> receivable_turnover_days_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getReceivable_turnover_days))
                    .map(AnalyseIndicatorDTO::getReceivable_turnover_days)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(receivable_turnover_days_list)){
                String msg = this.getIndicatorPercentValue(indName, "应收周转天数", receivable_turnover_days_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 存货周转天数
            List<Double> inventory_turnover_days_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getInventory_turnover_days() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getInventory_turnover_days))
                    .map(AnalyseIndicatorDTO::getInventory_turnover_days)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(inventory_turnover_days_list)){
                String msg = this.getIndicatorPercentValue(indName, "存货周转天数", inventory_turnover_days_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }

            // 市值
            List<Double> market_capital_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getMarket_capital() !=null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getMarket_capital))
                    .map(AnalyseIndicatorDTO::getMarket_capital)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(market_capital_list)){
                String msg = this.getIndicatorPercentValue(indName, "市值", market_capital_list);
                if(StringUtils.isNotBlank(msg)){
                    strPercentList.add(msg);
                }
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String percentListName =String.format(StockConstant.INDICATOR_LIST_PERCENT, kLineDate, strDateTime);
            FileUtils.writeLines(new File(percentListName), "UTF-8", strPercentList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 计算指标的百分位值
     *
     * @param indName
     * @param indicatorName
     * @param indicatorValueList
     * @return
     */
    private String getIndicatorPercentValue(String indName, String indicatorName, List<Double> indicatorValueList){
        if(StringUtils.isBlank(indName) || StringUtils.isBlank(indicatorName) || CollectionUtils.isEmpty(indicatorValueList)){
            return StringUtils.EMPTY;
        }

        List<Double> sortedIndicatorValueList = indicatorValueList.stream()
                .sorted()
                .collect(Collectors.toList());

        int totalSize = sortedIndicatorValueList.size();
        String msg = new StringBuilder().append(indName)
                .append(",").append(indicatorName)
                .append(",").append(sortedIndicatorValueList.get(totalSize*10/100))
                .append(",").append(sortedIndicatorValueList.get(totalSize*25/100))
                .append(",").append(sortedIndicatorValueList.get(totalSize*50/100))
                .append(",").append(sortedIndicatorValueList.get(totalSize*75/100))
                .append(",").append(sortedIndicatorValueList.get(totalSize*90/100))
                .toString();

        return msg;
    }

    /**
     * 筛选并记录结果
     *
     * @param kLineDate
     * @param allIndicatorDTOList
     */
    private void filterAndSaveIndicatorDTO(String kLineDate, List<AnalyseIndicatorDTO> allIndicatorDTOList){
        // 筛选合适的数据
        List<AnalyseIndicatorDTO> filterIndicatorDTOList = this.filterByIndicator(allIndicatorDTOList);

        // 白名单数据处理
        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> whiteStockCodeList = localStockDataManager.getWhiteStockCodeList();
        if(CollectionUtils.isNotEmpty(whiteStockCodeList)){
            List<String> filterCodeList = filterIndicatorDTOList.stream()
                    .map(AnalyseIndicatorDTO::getCode)
                    .collect(Collectors.toList());

            Map<String, AnalyseIndicatorDTO> allCodeAndIndicatorList = allIndicatorDTOList.stream()
                    .collect(Collectors.toMap(AnalyseIndicatorDTO::getCode, val -> val, (val1, val2) -> val1));

            for(String whiteStockCode : whiteStockCodeList){
                if(!filterCodeList.contains(whiteStockCode)){
                    AnalyseIndicatorDTO tmpIndicatorDTO = allCodeAndIndicatorList.get(whiteStockCode);
                    if(tmpIndicatorDTO !=null){
                        filterIndicatorDTOList.add(tmpIndicatorDTO);
                    }
                }
            }
        }

        // 生成结果
        List<String> strIndicatorList =Lists.newArrayList();
        strIndicatorList.add(HEADER);

        filterIndicatorDTOList =Optional.ofNullable(filterIndicatorDTOList).orElse(Lists.newArrayList()).stream()
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb_p_1000))
                .collect(Collectors.toList());
        for(AnalyseIndicatorDTO indicatorDTO : filterIndicatorDTOList){
            try {
                Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
                StringBuilder indicatorBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName ());
                    indicatorBuilder.append(",").append(value);
                }
                strIndicatorList.add(indicatorBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String filterListName =String.format(StockConstant.INDICATOR_LIST_FILTER, kLineDate, 0, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strIndicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("indicatorList: "+ JSON.toJSONString(strIndicatorList));
    }

    /**
     * 筛选数据
     *
     * @param indicatorDTOList
     * @return
     */
    private List<AnalyseIndicatorDTO> filterByIndicator(List<AnalyseIndicatorDTO> indicatorDTOList){
        if(CollectionUtils.isEmpty(indicatorDTOList)){
            return Lists.newArrayList();
        }

        List<AnalyseIndicatorDTO> analyseIndicatorDTOList = indicatorDTOList.stream()
                .filter(indicatorDTO -> StringUtils.isNoneBlank(indicatorDTO.getName()) && !indicatorDTO.getName().contains("ST"))
                .filter(indicatorDTO -> indicatorDTO.getKLineSize() != null && indicatorDTO.getKLineSize() > 300)
                .filter(indicatorDTO -> indicatorDTO.getMarket_capital() != null && indicatorDTO.getMarket_capital() >= 30)
                .filter(indicatorDTO -> indicatorDTO.getRevenue() != null)
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm_v1() != null && indicatorDTO.getAvg_roe_ttm_v1() >= 0.15 && indicatorDTO.getAvg_roe_ttm_v1() < 2)
                .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() != null && indicatorDTO.getOperating_income_yoy() <= 2)
                .filter(indicatorDTO -> indicatorDTO.getGw_ia_assert_rate() != null && indicatorDTO.getGw_ia_assert_rate() <= 0.3)
                .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days() != null && indicatorDTO.getReceivable_turnover_days() <= 250)
                .filter(indicatorDTO -> {
                    boolean result1 = indicatorDTO.getReceivable_turnover_days() != null && indicatorDTO.getReceivable_turnover_days() <= 120;
                    boolean result2 = indicatorDTO.getInventory_turnover_days() != null && indicatorDTO.getInventory_turnover_days() <= 350;
                    return result1 || result2;
                })
                .filter(indicatorDTO -> {
                    String ind_name = indicatorDTO.getInd_name();
                    if(StringUtils.isNotBlank(ind_name)){
                        // 农业、环保、建筑相关直接过滤掉
                        if(ind_name.contains("农") || ind_name.contains("环保")|| ind_name.contains("建筑")){
                            return false;
                        }

                        // 医药行业，过滤毛利率和净利率差过高的数据
                        if(ind_name.contains("医") || ind_name.contains("药")){
                            if(indicatorDTO.getGross_margin_rate() !=null && indicatorDTO.getNet_selling_rate() !=null){
                                double rate_diff_value = indicatorDTO.getGross_margin_rate() - indicatorDTO.getNet_selling_rate();
                                return rate_diff_value <0.5;
                            }
                        }
                    }

                    return true;
                })
                .filter(indicatorDTO -> {
                    if(indicatorDTO.getPb_p_1000() !=null && indicatorDTO.getPb_p_1000() > 0.3){
                        if(indicatorDTO.getMa_1000_diff_p() !=null && indicatorDTO.getMa_1000_diff_p() <0.2){
                            return true;
                        }

                        return false;
                    }

                    return true;
                })
                .filter(indicatorDTO -> {
                    if(indicatorDTO.getMarket_capital() !=null && indicatorDTO.getMarket_capital() >=1000){
                        // 市值千亿以上，毛利率和净利率条件放宽
                        return indicatorDTO.getGross_margin_rate() != null && indicatorDTO.getGross_margin_rate() >= 0.1
                                && indicatorDTO.getNet_selling_rate() != null && indicatorDTO.getNet_selling_rate() >= 0.05;
                    }else if(indicatorDTO.getMarket_capital() !=null && indicatorDTO.getMarket_capital() <= 50){
                        // 市值50亿以下的， 要求有高利润或高增长
                        int curMatchNum = 0;

                        // 毛利率和净利率指标
                        if (indicatorDTO.getCur_q_gross_margin_rate() >= 0.30 && indicatorDTO.getCur_q_net_selling_rate() >= 0.15) {
                            curMatchNum++;
                        }

                        // 营收和利润增长指标
                        if (indicatorDTO.getCur_q_operating_income_yoy() != null && indicatorDTO.getCur_q_operating_income_yoy() >= 0.10
                                && indicatorDTO.getCur_q_net_profit_atsopc_yoy() != null && indicatorDTO.getCur_q_net_profit_atsopc_yoy() >= 0.15) {
                            curMatchNum++;
                        }

                        return curMatchNum >= 1;
                    } else {
                        return indicatorDTO.getGross_margin_rate() != null && indicatorDTO.getGross_margin_rate() >= 0.15
                                && indicatorDTO.getNet_selling_rate() != null && indicatorDTO.getNet_selling_rate() >= 0.07;
                    }
                })
                .collect(Collectors.toList());

        return analyseIndicatorDTOList;
    }

    /**
     * 格式化指标
     *
     * @param analyseIndicatorDTO
     */
    private void formatAnalyseIndicatorDTO(AnalyseIndicatorDTO analyseIndicatorDTO){
        if(analyseIndicatorDTO ==null){
            return ;
        }

        if(analyseIndicatorDTO.getOne_month_price_change() !=null){
            analyseIndicatorDTO.setOne_month_price_change(NumberUtil.format(analyseIndicatorDTO.getOne_month_price_change(), 3));
        }

        if(analyseIndicatorDTO.getThree_month_price_change() !=null){
            analyseIndicatorDTO.setThree_month_price_change(NumberUtil.format(analyseIndicatorDTO.getThree_month_price_change(), 3));
        }

        if(analyseIndicatorDTO.getHalf_year_price_change() !=null){
            analyseIndicatorDTO.setHalf_year_price_change(NumberUtil.format(analyseIndicatorDTO.getHalf_year_price_change(), 3));
        }

        if(analyseIndicatorDTO.getOne_year_price_change() !=null){
            analyseIndicatorDTO.setOne_year_price_change(NumberUtil.format(analyseIndicatorDTO.getOne_year_price_change(), 3));
        }

        if(analyseIndicatorDTO.getAsset_liab_ratio() !=null){
            analyseIndicatorDTO.setAsset_liab_ratio(NumberUtil.format(analyseIndicatorDTO.getAsset_liab_ratio()/100, 3));
        }
        if(analyseIndicatorDTO.getPe() !=null){
            analyseIndicatorDTO.setPe(NumberUtil.format(analyseIndicatorDTO.getPe(), 1));
        }
        if(analyseIndicatorDTO.getPe_p_1000() !=null){
            analyseIndicatorDTO.setPe_p_1000(NumberUtil.format(analyseIndicatorDTO.getPe_p_1000(), 3));
        }
        if(analyseIndicatorDTO.getPb() !=null){
            analyseIndicatorDTO.setPb(NumberUtil.format(analyseIndicatorDTO.getPb(), 1));
        }
        if(analyseIndicatorDTO.getPb_p_1000() !=null){
            analyseIndicatorDTO.setPb_p_1000(NumberUtil.format(analyseIndicatorDTO.getPb_p_1000(), 3));
        }
        if(analyseIndicatorDTO.getTotal_holders_equity() !=null){
            analyseIndicatorDTO.setTotal_holders_equity(NumberUtil.format(analyseIndicatorDTO.getTotal_holders_equity() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getMa_1000_diff_p() !=null){
            analyseIndicatorDTO.setMa_1000_diff_p(NumberUtil.format(analyseIndicatorDTO.getMa_1000_diff_p() , 9));
        }
        if(analyseIndicatorDTO.getAvg_roe_ttm() !=null){
            analyseIndicatorDTO.setAvg_roe_ttm(NumberUtil.format(analyseIndicatorDTO.getAvg_roe_ttm(), 3));
        }
        if(analyseIndicatorDTO.getAvg_roe_ttm_v1() !=null){
            analyseIndicatorDTO.setAvg_roe_ttm_v1(NumberUtil.format(analyseIndicatorDTO.getAvg_roe_ttm_v1(), 3));
        }
        if(analyseIndicatorDTO.getRevenue() !=null){
            analyseIndicatorDTO.setRevenue(NumberUtil.format(analyseIndicatorDTO.getRevenue() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getOperating_cost() !=null){
            analyseIndicatorDTO.setOperating_cost(NumberUtil.format(analyseIndicatorDTO.getOperating_cost() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getGross_margin_rate() !=null){
            analyseIndicatorDTO.setGross_margin_rate(NumberUtil.format(analyseIndicatorDTO.getGross_margin_rate(), 3));
        }
        if(analyseIndicatorDTO.getNet_selling_rate() !=null){
            analyseIndicatorDTO.setNet_selling_rate(NumberUtil.format(analyseIndicatorDTO.getNet_selling_rate()/100, 3));
        }
        if(analyseIndicatorDTO.getCur_q_gross_margin_rate() !=null){
            analyseIndicatorDTO.setCur_q_gross_margin_rate(NumberUtil.format(analyseIndicatorDTO.getCur_q_gross_margin_rate(), 3));
        }
        if(analyseIndicatorDTO.getCur_q_net_selling_rate() !=null){
            analyseIndicatorDTO.setCur_q_net_selling_rate(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_selling_rate(), 3));
        }
        if(analyseIndicatorDTO.getCur_q_gross_margin_rate_change() !=null){
            analyseIndicatorDTO.setCur_q_gross_margin_rate_change(NumberUtil.format(analyseIndicatorDTO.getCur_q_gross_margin_rate_change(), 3));
        }
        if(analyseIndicatorDTO.getCur_q_net_selling_rate_change() !=null){
            analyseIndicatorDTO.setCur_q_net_selling_rate_change(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_selling_rate_change(), 3));
        }
        if(analyseIndicatorDTO.getCur_q_gross_margin_rate_q_chg() !=null){
            analyseIndicatorDTO.setCur_q_gross_margin_rate_q_chg(NumberUtil.format(analyseIndicatorDTO.getCur_q_gross_margin_rate_q_chg(), 3));
        }
        if(analyseIndicatorDTO.getCur_q_net_selling_rate_q_chg() !=null){
            analyseIndicatorDTO.setCur_q_net_selling_rate_q_chg(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_selling_rate_q_chg(), 3));
        }
        if(analyseIndicatorDTO.getOperating_income_yoy() !=null){
            analyseIndicatorDTO.setOperating_income_yoy(NumberUtil.format(analyseIndicatorDTO.getOperating_income_yoy()/100, 3));
        }
        if(analyseIndicatorDTO.getNet_profit_atsopc_yoy() !=null){
            analyseIndicatorDTO.setNet_profit_atsopc_yoy(NumberUtil.format(analyseIndicatorDTO.getNet_profit_atsopc_yoy()/100, 3));
        }
        if(analyseIndicatorDTO.getCur_q_operating_income_yoy() !=null){
            analyseIndicatorDTO.setCur_q_operating_income_yoy(NumberUtil.format(analyseIndicatorDTO.getCur_q_operating_income_yoy(), 3));
        }
        if(analyseIndicatorDTO.getCur_q_net_profit_atsopc_yoy() !=null){
            analyseIndicatorDTO.setCur_q_net_profit_atsopc_yoy(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_profit_atsopc_yoy(), 3));
        }
        if(analyseIndicatorDTO.getFixed_asset_sum_inc() !=null){
            analyseIndicatorDTO.setFixed_asset_sum_inc(NumberUtil.format(analyseIndicatorDTO.getFixed_asset_sum_inc(), 3));
        }
        if(analyseIndicatorDTO.getConstruction_in_process_sum_inc() !=null){
            analyseIndicatorDTO.setConstruction_in_process_sum_inc(NumberUtil.format(analyseIndicatorDTO.getConstruction_in_process_sum_inc(), 3));
        }
        if(analyseIndicatorDTO.getGw_ia_assert_rate() !=null){
            analyseIndicatorDTO.setGw_ia_assert_rate(NumberUtil.format(analyseIndicatorDTO.getGw_ia_assert_rate(), 3));
        }
        if(analyseIndicatorDTO.getFixed_assert_rate() !=null){
            analyseIndicatorDTO.setFixed_assert_rate(NumberUtil.format(analyseIndicatorDTO.getFixed_assert_rate(), 3));
        }
        if(analyseIndicatorDTO.getConstruction_assert_rate() !=null){
            analyseIndicatorDTO.setConstruction_assert_rate(NumberUtil.format(analyseIndicatorDTO.getConstruction_assert_rate(), 3));
        }
        if(analyseIndicatorDTO.getCash_sl_rate() !=null){
            double cash_sl_rate = analyseIndicatorDTO.getCash_sl_rate() <10 ? analyseIndicatorDTO.getCash_sl_rate() : 10;
            analyseIndicatorDTO.setCash_sl_rate(NumberUtil.format(cash_sl_rate, 1));
        }
        if(analyseIndicatorDTO.getMarket_capital() !=null){
            analyseIndicatorDTO.setMarket_capital(NumberUtil.format(analyseIndicatorDTO.getMarket_capital() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getSub_total_of_ci_from_oa() !=null){
            analyseIndicatorDTO.setSub_total_of_ci_from_oa(NumberUtil.format(analyseIndicatorDTO.getSub_total_of_ci_from_oa() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getCi_oi_rate() !=null){
            analyseIndicatorDTO.setCi_oi_rate(NumberUtil.format(analyseIndicatorDTO.getCi_oi_rate(), 1));
        }
        if(analyseIndicatorDTO.getNcf_from_oa() !=null){
            analyseIndicatorDTO.setNcf_from_oa(NumberUtil.format(analyseIndicatorDTO.getNcf_from_oa() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getNet_profit_atsopc() !=null){
            analyseIndicatorDTO.setNet_profit_atsopc(NumberUtil.format(analyseIndicatorDTO.getNet_profit_atsopc() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getNcf_pri_rate() !=null){
            analyseIndicatorDTO.setNcf_pri_rate(NumberUtil.format(analyseIndicatorDTO.getNcf_pri_rate(), 1));
        }
        if(analyseIndicatorDTO.getBp_and_ap() !=null){
            analyseIndicatorDTO.setBp_and_ap(NumberUtil.format(analyseIndicatorDTO.getBp_and_ap() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getAr_and_br() !=null){
            analyseIndicatorDTO.setAr_and_br(NumberUtil.format(analyseIndicatorDTO.getAr_and_br() /(10000 *10000), 1));
        }
        if(analyseIndicatorDTO.getAp_ar_rate() !=null){
            analyseIndicatorDTO.setAp_ar_rate(NumberUtil.format(analyseIndicatorDTO.getAp_ar_rate(), 1));
        }
        if(analyseIndicatorDTO.getReceivable_turnover_days() !=null){
            analyseIndicatorDTO.setReceivable_turnover_days(NumberUtil.format(analyseIndicatorDTO.getReceivable_turnover_days(), 1));
        }
        if(analyseIndicatorDTO.getInventory_turnover_days() !=null){
            analyseIndicatorDTO.setInventory_turnover_days(NumberUtil.format(analyseIndicatorDTO.getInventory_turnover_days(), 1));
        }
    }

    private AnalyseIndicatorElement getIndicatorElement(String fileDate, String code, Integer year, FinanceReportTypeEnum typeEnum
            , String kLineDate){
        AnalyseIndicatorElement indicatorElement =new AnalyseIndicatorElement();
        indicatorElement.setCode(code);
        indicatorElement.setReportYear(year);
        indicatorElement.setReportType(typeEnum.getCode());
        indicatorElement.setKLineDate(kLineDate);

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        CompanyDTO companyDTO = localStockDataManager.getCompanyDTO(code);
        indicatorElement.setCompanyDTO(companyDTO);

        this.assembleFinanceElement(indicatorElement, fileDate, code, year, typeEnum);

        this.assembleKLineElement(indicatorElement, fileDate, code, kLineDate);

        this.assembleHolderIncreaseElement(indicatorElement, code);

        this.assembleFreeShareElement(indicatorElement, code);

        return indicatorElement;
    }

    /**
     * 财务分析源数据
     *
     * @param indicatorElement
     * @param fileDate
     * @param code
     * @param year
     * @param typeEnum
     */
    private void assembleFinanceElement(AnalyseIndicatorElement indicatorElement, String fileDate, String code
            , Integer year, FinanceReportTypeEnum typeEnum){
        if(indicatorElement ==null || year==null || typeEnum ==null){
            return ;
        }

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        XueQiuStockBalanceDTO balanceDTO = localStockDataManager.getBalanceDTO(fileDate, code, year, typeEnum);
        if(balanceDTO !=null){
            indicatorElement.setCurBalanceDTO(balanceDTO);
        }

        XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = localStockDataManager.getBalanceDTO(fileDate, code, year-1, typeEnum);
        if(lastSamePeriodBalanceDTO !=null){
            indicatorElement.setLastSamePeriodBalanceDTO(lastSamePeriodBalanceDTO);
        }

        XueQiuStockBalanceDTO lastYearBalanceDTO = localStockDataManager.getBalanceDTO(fileDate, code, year-1, FinanceReportTypeEnum.ALL_YEAR);
        if(lastYearBalanceDTO !=null){
            indicatorElement.setLastYearBalanceDTO(lastYearBalanceDTO);
        }

        XueQiuStockIncomeDTO incomeDTO = localStockDataManager.getIncomeDTO(fileDate, code, year, typeEnum);
        if(incomeDTO !=null){
            indicatorElement.setCurIncomeDTO(incomeDTO);
        }

        XueQiuStockIncomeDTO lastYearIncomeDTO = localStockDataManager.getIncomeDTO(fileDate, code, year-1, FinanceReportTypeEnum.ALL_YEAR);
        if(lastYearIncomeDTO !=null){
            indicatorElement.setLastYearIncomeDTO(lastYearIncomeDTO);
        }

        XueQiuStockCashFlowDTO cashFlowDTO = localStockDataManager.getCashFlowDTO(fileDate, code, year, typeEnum);
        if(cashFlowDTO !=null){
            indicatorElement.setCurCashFlowDTO(cashFlowDTO);
        }

        XueQiuStockCashFlowDTO lastYearCashFlowDTO = localStockDataManager.getCashFlowDTO(fileDate, code, year-1, FinanceReportTypeEnum.ALL_YEAR);
        if(lastYearCashFlowDTO !=null){
            indicatorElement.setLastYearCashFlowDTO(lastYearCashFlowDTO);
        }

        XueQiuStockIndicatorDTO indicatorDTO = localStockDataManager.getXQIndicatorDTO(fileDate, code, year, typeEnum);
        if(indicatorDTO !=null){
            indicatorElement.setCurIndicatorDTO(indicatorDTO);
        }

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList =Lists.newArrayList();
        String curQuarterType =StringUtils.EMPTY;
        Integer lastQuarterYear =0;
        String lastQuarterType =StringUtils.EMPTY;
        if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_1.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_1.getCode();
            lastQuarterYear =year-1;
            lastQuarterType =FinanceReportTypeEnum.SINGLE_Q_4.getCode();

            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_1));
        }else if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.HALF_YEAR.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_2.getCode();
            lastQuarterYear =year;
            lastQuarterType =FinanceReportTypeEnum.SINGLE_Q_1.getCode();

            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_2));
        }else if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_3.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_3.getCode();
            lastQuarterYear =year;
            lastQuarterType =FinanceReportTypeEnum.SINGLE_Q_2.getCode();

            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3));
        }else if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.ALL_YEAR.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_4.getCode();
            lastQuarterYear =year;
            lastQuarterType =FinanceReportTypeEnum.SINGLE_Q_3.getCode();

            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList = localStockDataManager.getQuarterIncomeDTO(fileDate, code, immutablePairList);
        if(CollectionUtils.isNotEmpty(quarterIncomeDTOList)){
            indicatorElement.setQuarterIncomeDTOList(quarterIncomeDTOList);

            for(QuarterIncomeDTO quarterIncomeDTO : quarterIncomeDTOList){
                if(Objects.equals(quarterIncomeDTO.getReport_year(), year)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), curQuarterType)){
                    indicatorElement.setCurQuarterIncomeDTO(quarterIncomeDTO);
                }

                if(Objects.equals(quarterIncomeDTO.getReport_year(), year-1)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), curQuarterType)){
                    indicatorElement.setLastYearQuarterIncomeDTO(quarterIncomeDTO);
                }

                if(Objects.equals(quarterIncomeDTO.getReport_year(), lastQuarterYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), lastQuarterType)){
                    indicatorElement.setLastPeriodQuarterIncomeDTO(quarterIncomeDTO);
                }
            }
        }

    }

    /**
     * k线源数据
     *
     * @param indicatorElement
     * @param fileDate
     * @param code
     * @param kLineDate
     */
    private void assembleKLineElement(AnalyseIndicatorElement indicatorElement, String fileDate, String code, String kLineDate){
        if(indicatorElement ==null || StringUtils.isBlank(fileDate) || StringUtils.isBlank(code) || StringUtils.isBlank(kLineDate)){
            return;
        }

        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime kLineDateTime = LocalDate.parse(kLineDate, df).atStartOfDay();

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<XueQiuStockKLineDTO> kLineDTOList = localStockDataManager.getKLineList(fileDate, code, kLineDateTime, KLineTypeEnum.DAY, 1000);
        if(CollectionUtils.isNotEmpty(kLineDTOList)){
            indicatorElement.setKLineDTOList(kLineDTOList);
        }


        LocalDateTime oneMonthDateTime = kLineDateTime.plusMonths(1);
        List<XueQiuStockKLineDTO> oneMonthKLineDTOList = localStockDataManager.getKLineList(fileDate, code, oneMonthDateTime, KLineTypeEnum.DAY, 1);
        if(CollectionUtils.isNotEmpty(oneMonthKLineDTOList)){
            XueQiuStockKLineDTO oneMonthKLineDTO = oneMonthKLineDTOList.get(0);
            LocalDateTime oneMonthKLineDateTime = LocalDateTime.ofEpochSecond(oneMonthKLineDTO.getTimestamp() / 1000, 0, ZoneOffset.ofHours(8));
            if(StringUtils.equals(oneMonthDateTime.format(df), oneMonthKLineDateTime.format(df))){
                indicatorElement.setOneMonthKLineDTO(oneMonthKLineDTOList.get(0));
            }
        }

        LocalDateTime threeMonthDateTime = kLineDateTime.plusMonths(3);
        List<XueQiuStockKLineDTO> threeMonthKLineDTOList = localStockDataManager.getKLineList(fileDate, code, threeMonthDateTime, KLineTypeEnum.DAY, 1);
        if(CollectionUtils.isNotEmpty(threeMonthKLineDTOList)){
            XueQiuStockKLineDTO threeMonthKLineDTO = threeMonthKLineDTOList.get(0);
            LocalDateTime threeMonthKLineDateTime = LocalDateTime.ofEpochSecond(threeMonthKLineDTO.getTimestamp() / 1000, 0, ZoneOffset.ofHours(8));
            if(StringUtils.equals(threeMonthDateTime.format(df), threeMonthKLineDateTime.format(df))) {
                indicatorElement.setThreeMonthKLineDTO(threeMonthKLineDTOList.get(0));
            }
        }

        LocalDateTime halfYearDateTime = kLineDateTime.plusMonths(6);
        List<XueQiuStockKLineDTO> halfYearKLineDTOList = localStockDataManager.getKLineList(fileDate, code, halfYearDateTime, KLineTypeEnum.DAY, 1);
        if(CollectionUtils.isNotEmpty(halfYearKLineDTOList)){
            XueQiuStockKLineDTO halfYearKLineDTO = halfYearKLineDTOList.get(0);
            LocalDateTime halfYearKLineDateTime = LocalDateTime.ofEpochSecond(halfYearKLineDTO.getTimestamp() / 1000, 0, ZoneOffset.ofHours(8));
            if(StringUtils.equals(halfYearDateTime.format(df), halfYearKLineDateTime.format(df))) {
                indicatorElement.setHalfYearKLineDTO(halfYearKLineDTOList.get(0));
            }
        }

        LocalDateTime oneYearDateTime = kLineDateTime.plusYears(1);
        List<XueQiuStockKLineDTO> oneYearKLineDTOList = localStockDataManager.getKLineList(fileDate, code, oneYearDateTime, KLineTypeEnum.DAY, 1);
        if(CollectionUtils.isNotEmpty(oneYearKLineDTOList)){
            XueQiuStockKLineDTO oneYearKLineDTO = oneYearKLineDTOList.get(0);
            LocalDateTime oneYearKLineDateTime = LocalDateTime.ofEpochSecond(oneYearKLineDTO.getTimestamp() / 1000, 0, ZoneOffset.ofHours(8));
            if(StringUtils.equals(oneYearDateTime.format(df), oneYearKLineDateTime.format(df))) {
                indicatorElement.setOneYearKLineDTO(oneYearKLineDTOList.get(0));
            }
        }

    }

    /**
     * 增减持数据
     *
     * @param indicatorElement
     * @param code
     */
    private void assembleHolderIncreaseElement(AnalyseIndicatorElement indicatorElement, String code){
        if(indicatorElement ==null || StringUtils.isBlank(code)){
            return;
        }

        DongChaiLocalDataManager manager =new DongChaiLocalDataManager();
        DongChaiHolderIncreaseDTO holderIncreaseDTO = manager.getHolderIncreaseDTO(code);
        if(holderIncreaseDTO !=null){
            indicatorElement.setHolderIncreaseDTO(holderIncreaseDTO);
        }
    }

    /**
     * 解禁数据
     *
     * @param indicatorElement
     * @param code
     */
    private void assembleFreeShareElement(AnalyseIndicatorElement indicatorElement, String code){
        if(indicatorElement ==null || StringUtils.isBlank(code)){
            return;
        }

        DongChaiLocalDataManager manager =new DongChaiLocalDataManager();
        DongChaiFreeShareDTO freeShareDTO = manager.getFreeShareDTO(code);
        if(freeShareDTO !=null){
            indicatorElement.setFreeShareDTO(freeShareDTO);
        }
    }

    private AnalyseIndicatorDTO getAnalyseIndicatorDTO(AnalyseIndicatorElement indicatorElement){
        JSONObject jsonIndicator =new JSONObject();
        jsonIndicator.put("code", indicatorElement.getCode());
        jsonIndicator.put("kLineDate", indicatorElement.getKLineDate());

        if(indicatorElement.getCompanyDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCompanyDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(indicatorElement.getCurBalanceDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurBalanceDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(indicatorElement.getCurIncomeDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurIncomeDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(indicatorElement.getCurCashFlowDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurCashFlowDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(indicatorElement.getCurIndicatorDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurIndicatorDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(CollectionUtils.isNotEmpty(indicatorElement.getKLineDTOList())){
            XueQiuStockKLineDTO queryDateKLineDTO = indicatorElement.getKLineDTOList().stream()
                    .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                    .collect(Collectors.toList())
                    .get(0);
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(queryDateKLineDTO));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(indicatorElement.getHolderIncreaseDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getHolderIncreaseDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if(indicatorElement.getFreeShareDTO() !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getFreeShareDTO()));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        AnalyseIndicatorDTO indicatorDTO = JSON.parseObject(JSON.toJSONString(jsonIndicator), AnalyseIndicatorDTO.class);

        this.assembleAnalyseIndicator(indicatorDTO, indicatorElement);

        System.out.println("indicatorDTO: " + JSON.toJSONString(indicatorDTO));
        return indicatorDTO;
    }

    private void assembleAnalyseIndicator(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null){
            return ;
        }

        if(indicatorDTO.getMa_20_equal_ma_60_num() ==null){
            Integer value = this.getMa20EqualMa60Value(indicatorDTO, indicatorElement);
            indicatorDTO.setMa_20_equal_ma_60_num(value !=null ? value : 0);
        }

        if(indicatorDTO.getPe_p_1000() ==null){
            Double pe_p_1000 =this.getPeP1000Value(indicatorDTO, indicatorElement);
            if(pe_p_1000 !=null){
                indicatorDTO.setPe_p_1000(pe_p_1000);
            }
        }

        if(indicatorDTO.getPe_p_1000() ==null){
            Double pe_p_1000 =this.getPeP1000Value(indicatorDTO, indicatorElement);
            if(pe_p_1000 !=null){
                indicatorDTO.setPe_p_1000(pe_p_1000);
            }
        }

        if(indicatorDTO.getPb_p_1000() ==null){
            Double pb_p_1000 =this.getPbP1000Value(indicatorDTO, indicatorElement);
            if(pb_p_1000 !=null){
                indicatorDTO.setPb_p_1000(pb_p_1000);
            }
        }

        if(indicatorDTO.getMa_1000_diff_p() ==null){
            Double ma_1000_diff_p =this.getMa1000DiffPercentValue(indicatorDTO, indicatorElement);
            if(ma_1000_diff_p !=null){
                indicatorDTO.setMa_1000_diff_p(ma_1000_diff_p);
            }
        }

        if(indicatorDTO.getAvg_roe_ttm() ==null){
            Double avg_roe_ttm =this.getAvgRoeTtm(indicatorDTO, indicatorElement);
            if(avg_roe_ttm !=null){
                indicatorDTO.setAvg_roe_ttm(avg_roe_ttm);
            }
        }
        if(indicatorDTO.getAvg_roe_ttm_v1() ==null){
            Double avg_roe_ttm_v1 =this.getAvgRoeTtmV1(indicatorDTO, indicatorElement);
            if(avg_roe_ttm_v1 !=null){
                indicatorDTO.setAvg_roe_ttm_v1(avg_roe_ttm_v1);
            }
        }
        if(indicatorDTO.getGross_margin_rate() ==null){
            Double operating_cost = indicatorDTO.getOperating_cost();
            Double revenue = indicatorDTO.getRevenue();
            if(operating_cost !=null && revenue !=null && revenue !=0){
                indicatorDTO.setGross_margin_rate(1 - operating_cost /revenue);
            }
        }

        if(indicatorDTO.getCi_oi_rate() ==null){
            Double sub_total_of_ci_from_oa = indicatorDTO.getSub_total_of_ci_from_oa();
            Double revenue = indicatorDTO.getRevenue();
            if(sub_total_of_ci_from_oa !=null && revenue !=null && revenue !=0){
                indicatorDTO.setCi_oi_rate(sub_total_of_ci_from_oa /revenue);
            }
        }

        if(indicatorDTO.getNcf_pri_rate() ==null){
            Double ncf_from_oa = indicatorDTO.getNcf_from_oa();
            Double net_profit_atsopc = indicatorDTO.getNet_profit_atsopc();
            if(ncf_from_oa !=null && net_profit_atsopc !=null && net_profit_atsopc !=0){
                indicatorDTO.setNcf_pri_rate(ncf_from_oa /net_profit_atsopc);
            }
        }

        if(indicatorDTO.getAp_ar_rate() ==null){
            Double bp_and_ap = indicatorDTO.getBp_and_ap();
            Double ar_and_br = indicatorDTO.getAr_and_br();
            if(bp_and_ap !=null && ar_and_br !=null && ar_and_br !=0){
                indicatorDTO.setAp_ar_rate(bp_and_ap /ar_and_br);
            }
        }

        if(indicatorDTO.getCur_q_gross_margin_rate() ==null){
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            if(curQuarterIncomeDTO !=null && curQuarterIncomeDTO.getRevenue() !=null && curQuarterIncomeDTO.getOperating_cost() !=null){
                double cur_q_gross_margin_rate = 1- curQuarterIncomeDTO.getOperating_cost()/curQuarterIncomeDTO.getRevenue();
                indicatorDTO.setCur_q_gross_margin_rate(cur_q_gross_margin_rate);
            }
        }

        if(indicatorDTO.getCur_q_net_selling_rate() ==null){
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            if(curQuarterIncomeDTO !=null && curQuarterIncomeDTO.getRevenue() !=null && curQuarterIncomeDTO.getNet_profit() !=null){
                double cur_q_net_selling_rate = curQuarterIncomeDTO.getNet_profit()/curQuarterIncomeDTO.getRevenue();
                indicatorDTO.setCur_q_net_selling_rate(cur_q_net_selling_rate);
            }
        }

        if(indicatorDTO.getCur_q_gross_margin_rate_change() ==null){
            Double cur_q_gross_margin_rate = indicatorDTO.getCur_q_gross_margin_rate();
            if(cur_q_gross_margin_rate !=null){
                QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
                if(lastYearQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO.getRevenue() !=null && lastYearQuarterIncomeDTO.getOperating_cost() !=null){
                    double last_year_cur_q_gross_margin_rate = 1- lastYearQuarterIncomeDTO.getOperating_cost()/lastYearQuarterIncomeDTO.getRevenue();
                    indicatorDTO.setCur_q_gross_margin_rate_change(cur_q_gross_margin_rate /last_year_cur_q_gross_margin_rate -1);
                }
            }
        }

        if(indicatorDTO.getCur_q_net_selling_rate_change() ==null){
            Double cur_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate();
            if(cur_q_net_selling_rate !=null){
                QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
                if(lastYearQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO.getRevenue() !=null && lastYearQuarterIncomeDTO.getNet_profit() !=null){
                    double last_year_cur_q_net_selling_rate = lastYearQuarterIncomeDTO.getNet_profit()/lastYearQuarterIncomeDTO.getRevenue();
                    double cur_q_net_selling_rate_change =(cur_q_net_selling_rate - last_year_cur_q_net_selling_rate)
                            /Math.abs(last_year_cur_q_net_selling_rate);
                    indicatorDTO.setCur_q_net_selling_rate_change(cur_q_net_selling_rate_change);
                }
            }
        }

        if(indicatorDTO.getCur_q_gross_margin_rate_q_chg() ==null){
            Double cur_q_gross_margin_rate = indicatorDTO.getCur_q_gross_margin_rate();
            if(cur_q_gross_margin_rate !=null){
                QuarterIncomeDTO lastPeriodQuarterIncomeDTO = indicatorElement.getLastPeriodQuarterIncomeDTO();
                if(lastPeriodQuarterIncomeDTO !=null && lastPeriodQuarterIncomeDTO.getRevenue() !=null && lastPeriodQuarterIncomeDTO.getOperating_cost() !=null){
                    double last_period_cur_q_gross_margin_rate = 1- lastPeriodQuarterIncomeDTO.getOperating_cost()/lastPeriodQuarterIncomeDTO.getRevenue();
                    indicatorDTO.setCur_q_gross_margin_rate_q_chg(cur_q_gross_margin_rate /last_period_cur_q_gross_margin_rate -1);
                }
            }
        }

        if(indicatorDTO.getCur_q_net_selling_rate_q_chg() ==null){
            Double cur_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate();
            if(cur_q_net_selling_rate !=null){
                QuarterIncomeDTO lastPeriodQuarterIncomeDTO = indicatorElement.getLastPeriodQuarterIncomeDTO();
                if(lastPeriodQuarterIncomeDTO !=null && lastPeriodQuarterIncomeDTO.getRevenue() !=null && lastPeriodQuarterIncomeDTO.getNet_profit() !=null){
                    double last_period_cur_q_net_selling_rate = lastPeriodQuarterIncomeDTO.getNet_profit()/lastPeriodQuarterIncomeDTO.getRevenue();
                    double cur_q_net_selling_rate_q_chg =(cur_q_net_selling_rate - last_period_cur_q_net_selling_rate)
                            /Math.abs(last_period_cur_q_net_selling_rate);
                    indicatorDTO.setCur_q_net_selling_rate_q_chg(cur_q_net_selling_rate_q_chg);
                }
            }
        }

        if(indicatorDTO.getCur_q_operating_income_yoy() ==null){
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
            if(curQuarterIncomeDTO !=null && curQuarterIncomeDTO.getRevenue() !=null
                    && lastYearQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO.getRevenue() !=null ){
                indicatorDTO.setCur_q_operating_income_yoy(curQuarterIncomeDTO.getRevenue()/lastYearQuarterIncomeDTO.getRevenue() -1);
            }
        }

        if(indicatorDTO.getCur_q_net_profit_atsopc_yoy() ==null){
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
            if(curQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO !=null){
                double cur_q_net_profit_atsopc_yoy = (curQuarterIncomeDTO.getNet_profit()-lastYearQuarterIncomeDTO.getNet_profit())
                        /Math.abs(lastYearQuarterIncomeDTO.getNet_profit());
                indicatorDTO.setCur_q_net_profit_atsopc_yoy(cur_q_net_profit_atsopc_yoy);
            }
        }

        if(indicatorDTO.getFixed_asset_sum_inc() ==null){
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = indicatorElement.getLastSamePeriodBalanceDTO();
            if(curBalanceDTO !=null && lastSamePeriodBalanceDTO !=null && curBalanceDTO.getFixed_asset_sum() !=null){
                if(lastSamePeriodBalanceDTO.getFixed_asset_sum() !=null && lastSamePeriodBalanceDTO.getFixed_asset_sum() !=0){
                    indicatorDTO.setFixed_asset_sum_inc(curBalanceDTO.getFixed_asset_sum()/lastSamePeriodBalanceDTO.getFixed_asset_sum() -1);
                }
            }
        }

        if(indicatorDTO.getConstruction_in_process_sum_inc() ==null){
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = indicatorElement.getLastSamePeriodBalanceDTO();
            if(curBalanceDTO !=null && lastSamePeriodBalanceDTO !=null && curBalanceDTO.getConstruction_in_process_sum() !=null){
                if(lastSamePeriodBalanceDTO.getConstruction_in_process_sum() !=null && lastSamePeriodBalanceDTO.getConstruction_in_process_sum() !=0){
                    indicatorDTO.setConstruction_in_process_sum_inc(curBalanceDTO.getConstruction_in_process_sum()/lastSamePeriodBalanceDTO.getConstruction_in_process_sum() -1);
                }
            }
        }

        if(indicatorDTO.getGw_ia_assert_rate() ==null){
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if(curBalanceDTO !=null){
                Double goodwill = curBalanceDTO.getGoodwill() !=null ? curBalanceDTO.getGoodwill() : 0;
                Double intangible_assets = curBalanceDTO.getIntangible_assets() !=null ? curBalanceDTO.getIntangible_assets() : 0;
                Double total_holders_equity = curBalanceDTO.getTotal_holders_equity() !=null ? curBalanceDTO.getTotal_holders_equity() : 0.01;
                indicatorDTO.setGw_ia_assert_rate((goodwill+intangible_assets)/total_holders_equity);
            }
        }

        if(indicatorDTO.getFixed_assert_rate() ==null){
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if(curBalanceDTO !=null){
                Double fixed_asset_sum = curBalanceDTO.getFixed_asset_sum() !=null ? curBalanceDTO.getFixed_asset_sum() : 0;
                Double total_holders_equity = curBalanceDTO.getTotal_holders_equity() !=null ? curBalanceDTO.getTotal_holders_equity() : 0.01;
                indicatorDTO.setFixed_assert_rate(fixed_asset_sum/total_holders_equity);
            }
        }

        if(indicatorDTO.getConstruction_assert_rate() ==null){
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if(curBalanceDTO !=null){
                Double construction_in_process_sum = curBalanceDTO.getConstruction_in_process_sum() !=null ? curBalanceDTO.getConstruction_in_process_sum() : 0;
                Double total_holders_equity = curBalanceDTO.getTotal_holders_equity() !=null ? curBalanceDTO.getTotal_holders_equity() : 0.01;
                indicatorDTO.setConstruction_assert_rate(construction_in_process_sum/total_holders_equity);
            }
        }

        if(indicatorDTO.getCash_sl_rate() ==null){
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if(curBalanceDTO !=null){
                double currency_funds = curBalanceDTO.getCurrency_funds() != null ? curBalanceDTO.getCurrency_funds() : 0;
                double tradable_fnncl_assets = curBalanceDTO.getTradable_fnncl_assets() != null ? curBalanceDTO.getTradable_fnncl_assets() : 0;
                double st_loan = curBalanceDTO.getSt_loan() != null ? curBalanceDTO.getSt_loan() : 0.01;
                double tradable_fnncl_liab = curBalanceDTO.getTradable_fnncl_liab() != null ? curBalanceDTO.getTradable_fnncl_liab() : 0;
                indicatorDTO.setCash_sl_rate((currency_funds +tradable_fnncl_assets)/(st_loan + tradable_fnncl_liab));
            }
        }

        XueQiuStockKLineDTO queryDateKLineDTO = Optional.ofNullable(indicatorElement.getKLineDTOList()).orElse(Lists.newArrayList()).stream()
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .findFirst()
                .orElse(null);
        if(queryDateKLineDTO !=null && queryDateKLineDTO.getClose() !=null && queryDateKLineDTO.getClose() !=0){
            XueQiuStockKLineDTO oneMonthKLineDTO = indicatorElement.getOneMonthKLineDTO();
            if(oneMonthKLineDTO !=null && oneMonthKLineDTO.getClose() !=null){
                indicatorDTO.setOne_month_price_change( oneMonthKLineDTO.getClose()/queryDateKLineDTO.getClose() -1);
            }

            XueQiuStockKLineDTO threeMonthKLineDTO = indicatorElement.getThreeMonthKLineDTO();
            if(threeMonthKLineDTO !=null && threeMonthKLineDTO.getClose() !=null){
                indicatorDTO.setThree_month_price_change( threeMonthKLineDTO.getClose()/queryDateKLineDTO.getClose() -1);
            }

            XueQiuStockKLineDTO halfYearKLineDTO = indicatorElement.getHalfYearKLineDTO();
            if(halfYearKLineDTO !=null && halfYearKLineDTO.getClose() !=null){
                indicatorDTO.setHalf_year_price_change( halfYearKLineDTO.getClose()/queryDateKLineDTO.getClose() -1);
            }

            XueQiuStockKLineDTO oneYearKLineDTO = indicatorElement.getOneYearKLineDTO();
            if(oneYearKLineDTO !=null && oneYearKLineDTO.getClose() !=null){
                indicatorDTO.setOne_year_price_change( oneYearKLineDTO.getClose()/queryDateKLineDTO.getClose() -1);
            }
        }

        if(StringUtils.isBlank(indicatorDTO.getGross_net_rate_5_quarter())){
            List<QuarterIncomeDTO> quarterIncomeDTOList = indicatorElement.getQuarterIncomeDTOList();
            if(CollectionUtils.isNotEmpty(quarterIncomeDTOList)){
                List<QuarterIncomeDTO> sortedQuarterDTOList = quarterIncomeDTOList.stream()
                        .sorted(Comparator.comparing(QuarterIncomeDTO::getReport_year)
                                .thenComparing(QuarterIncomeDTO::getReport_type).reversed())
                        .collect(Collectors.toList());

                StringBuilder builder =new StringBuilder();
                for(QuarterIncomeDTO quarterIncomeDTO : sortedQuarterDTOList){
                    if(quarterIncomeDTO.getTotal_revenue() !=null && quarterIncomeDTO.getOperating_cost() !=null){
                        double gross_rate = 1- quarterIncomeDTO.getOperating_cost()/quarterIncomeDTO.getTotal_revenue();
                        String str_gross_rate = NumberUtil.format(gross_rate*100, 1) + "%";
                        builder.append(str_gross_rate);
                    }
                    builder.append("/");
                    if(quarterIncomeDTO.getTotal_revenue() !=null && quarterIncomeDTO.getNet_profit() !=null){
                        double net_profit_rate = quarterIncomeDTO.getNet_profit()/quarterIncomeDTO.getTotal_revenue();
                        String str_net_profit_rate = NumberUtil.format(net_profit_rate*100, 1) + "%";
                        builder.append(str_net_profit_rate);
                    }
                    builder.append("; ");
                }
                indicatorDTO.setGross_net_rate_5_quarter(builder.toString());
            }
        }

        if(StringUtils.isBlank(indicatorDTO.getOi_net_5_quarter())){
            List<QuarterIncomeDTO> quarterIncomeDTOList = indicatorElement.getQuarterIncomeDTOList();
            if(CollectionUtils.isNotEmpty(quarterIncomeDTOList)){
                List<QuarterIncomeDTO> sortedQuarterDTOList = quarterIncomeDTOList.stream()
                        .sorted(Comparator.comparing(QuarterIncomeDTO::getReport_year)
                                .thenComparing(QuarterIncomeDTO::getReport_type).reversed())
                        .collect(Collectors.toList());

                StringBuilder builder =new StringBuilder();
                for(QuarterIncomeDTO quarterIncomeDTO : sortedQuarterDTOList){
                    if(quarterIncomeDTO.getTotal_revenue() !=null ){
                        double total_revenue = quarterIncomeDTO.getTotal_revenue() / (1.0 * 10000 * 10000);
                        String str_total_revenue = NumberUtil.format(total_revenue, 1) +"";
                        builder.append(str_total_revenue);
                    }
                    builder.append("/");
                    if(quarterIncomeDTO.getNet_profit() !=null){
                        double net_profit = quarterIncomeDTO.getNet_profit() / (1.0 * 10000 * 10000);
                        String str_net_profit = NumberUtil.format(net_profit, 1)+"";
                        builder.append(str_net_profit);
                    }
                    builder.append("; ");
                }
                indicatorDTO.setOi_net_5_quarter(builder.toString());
            }
        }

        if(indicatorDTO.getKLineSize() ==null){
            int klineSize = CollectionUtils.isNotEmpty(indicatorElement.getKLineDTOList()) ? indicatorElement.getKLineDTOList().size() : 0;
            indicatorDTO.setKLineSize(klineSize);
        }

        if(indicatorDTO.getCurMatchNum() ==null){
            int curMatchNum = 0;

            // ROE指标
            if ((indicatorDTO.getAvg_roe_ttm() !=null && indicatorDTO.getAvg_roe_ttm() >= 0.12)
                    || (indicatorDTO.getAvg_roe_ttm_v1() !=null && indicatorDTO.getAvg_roe_ttm_v1() >= 0.16)) {
                curMatchNum++;
            }

            // 毛利率和净利率指标
            if (indicatorDTO.getCur_q_gross_margin_rate() !=null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.25
                    && indicatorDTO.getCur_q_net_selling_rate() !=null && indicatorDTO.getCur_q_net_selling_rate() >= 0.15) {
                curMatchNum++;
            }

            // 营收和利润增长指标
            if (indicatorDTO.getCur_q_operating_income_yoy() != null && indicatorDTO.getCur_q_operating_income_yoy() >= 0.10
                    && indicatorDTO.getCur_q_net_profit_atsopc_yoy() != null && indicatorDTO.getCur_q_net_profit_atsopc_yoy() >= 0.15) {
                curMatchNum++;
            }

            indicatorDTO.setCurMatchNum(curMatchNum);
        }

    }

    /**
     * 计算最近一段时间内股价和ma20、ma60相同，并小于ma250的次数
     *
     * @param indicatorDTO
     * @param indicatorElement
     * @return
     */
    private Integer getMa20EqualMa60Value(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null || indicatorElement ==null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())){
            return null;
        }

        long startTimestamp = LocalDateTime.now().plusMonths(-3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        List<XueQiuStockKLineDTO> matchedKLineDTOList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null && kLineDTO.getTimestamp() >= startTimestamp)
                .filter(kLineDTO ->{
                    if(kLineDTO.getClose() ==null || kLineDTO.getMa_20_value() ==null
                            || kLineDTO.getMa_60_value() ==null || kLineDTO.getMa_250_value() ==null){
                        return false;
                    }

                    if(kLineDTO.getClose() > (kLineDTO.getMa_250_value() * 1.1)){
                        return false;
                    }

                    double lowValue = kLineDTO.getClose() * 0.97;
                    double highValue = kLineDTO.getClose() * 1.03;
                    if( lowValue <= kLineDTO.getMa_20_value() && kLineDTO.getMa_20_value() < highValue
                            && lowValue <= kLineDTO.getMa_60_value() && kLineDTO.getMa_60_value() < highValue){
                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());

        return matchedKLineDTOList.size();
    }

    private Double getPeP1000Value(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null || indicatorDTO.getPe() ==null){
            return null;
        }
        if(indicatorElement ==null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())){
            return null;
        }

        Double currentPe = indicatorDTO.getPe();

        List<Double> peList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getPe() !=null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getPe))
                .map(XueQiuStockKLineDTO::getPe)
                .collect(Collectors.toList());

        for(int index =0; index <peList.size(); index++){
            if(Math.abs(currentPe -peList.get(index)) <=0.01){
                return (index*1.0)/peList.size();
            }
        }

        return null;
    }

    private Double getPbP1000Value(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null || indicatorDTO.getPb() ==null){
            return null;
        }
        if(indicatorElement ==null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())){
            return null;
        }

        Double currentPb = indicatorDTO.getPb();

        List<Double> pbList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getPb() !=null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getPb))
                .map(XueQiuStockKLineDTO::getPb)
                .collect(Collectors.toList());

        for(int index =0; index <pbList.size(); index++){
            if(Math.abs(currentPb -pbList.get(index)) <=0.01){
                return (index*1.0)/pbList.size();
            }
        }

        return null;
    }

    private Double getMa1000DiffPercentValue(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null || indicatorDTO.getClose() ==null || indicatorDTO.getMa_1000_value() ==null){
            return null;
        }
        if(indicatorElement ==null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())){
            return null;
        }

        List<Double> diffValueList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getMa_1000_value() !=null && kLineDTO.getClose() !=null)
                .map(kLineDTO -> kLineDTO.getClose() - kLineDTO.getMa_1000_value())
                .sorted()
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(diffValueList) || diffValueList.size() <1000){
            return null;
        }

        Double current_diff_value = indicatorDTO.getClose() - indicatorDTO.getMa_1000_value();
        for(int index =0; index < diffValueList.size(); index++){
            if(Math.abs(current_diff_value -diffValueList.get(index)) <=0.01){
                return (index *1.0)/diffValueList.size();
            }
        }

        return null;
    }

    private Double getAvgRoeTtm(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null || StringUtils.isBlank(indicatorDTO.getCode()) ||indicatorDTO.getTotal_holders_equity() ==null){
            return null;
        }

        Integer year = indicatorElement.getReportYear();
        String reportType = indicatorElement.getReportType();

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList =Lists.newArrayList();
        if(Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_1.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_2));
        }else if(Objects.equals(reportType, FinanceReportTypeEnum.HALF_YEAR.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3));
        }else if(Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_3.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4));
        }else if(Objects.equals(reportType, FinanceReportTypeEnum.ALL_YEAR.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList =Lists.newArrayList();
        for(QuarterIncomeDTO quarterIncomeDTO : indicatorElement.getQuarterIncomeDTOList()){
            for(ImmutablePair<Integer, FinanceReportTypeEnum> pair : immutablePairList){
                Integer tmpYear =pair.getLeft();
                FinanceReportTypeEnum reportTypeEnum =pair.getRight();

                if(Objects.equals(quarterIncomeDTO.getReport_year(), tmpYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), reportTypeEnum.getCode())){
                    quarterIncomeDTOList.add(quarterIncomeDTO);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(quarterIncomeDTOList) && quarterIncomeDTOList.size() ==4){
            double sum = quarterIncomeDTOList.stream()
                    .mapToDouble(incomeDTO -> incomeDTO.getNet_profit() !=null ? incomeDTO.getNet_profit(): 0)
                    .sum();
            return sum /indicatorDTO.getTotal_holders_equity();
        }

        return null;
    }

    /**
     * 去掉现金后的ROE
     *
     * @param indicatorDTO
     * @param indicatorElement
     * @return
     */
    private Double getAvgRoeTtmV1(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null || StringUtils.isBlank(indicatorDTO.getCode()) ||indicatorDTO.getTotal_holders_equity() ==null){
            return null;
        }

        Integer year = indicatorElement.getReportYear();
        String reportType = indicatorElement.getReportType();

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList =Lists.newArrayList();
        if(Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_1.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_2));
        }else if(Objects.equals(reportType, FinanceReportTypeEnum.HALF_YEAR.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3));
        }else if(Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_3.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4));
        }else if(Objects.equals(reportType, FinanceReportTypeEnum.ALL_YEAR.getCode())){
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList =Lists.newArrayList();
        for(QuarterIncomeDTO quarterIncomeDTO : indicatorElement.getQuarterIncomeDTOList()){
            for(ImmutablePair<Integer, FinanceReportTypeEnum> pair : immutablePairList){
                Integer tmpYear =pair.getLeft();
                FinanceReportTypeEnum reportTypeEnum =pair.getRight();

                if(Objects.equals(quarterIncomeDTO.getReport_year(), tmpYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), reportTypeEnum.getCode())){
                    quarterIncomeDTOList.add(quarterIncomeDTO);
                }
            }
        }
        if(CollectionUtils.isEmpty(quarterIncomeDTOList) || quarterIncomeDTOList.size() !=4){
            return null;
        }

        // 去掉现金等价物后的净资产
        XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
        if(curBalanceDTO ==null){
            return null;
        }

        double total_holders_equity = indicatorDTO.getTotal_holders_equity() != null ? indicatorDTO.getTotal_holders_equity() : 0;
        double currency_funds = curBalanceDTO.getCurrency_funds() != null ? curBalanceDTO.getCurrency_funds() : 0;
        double tradable_fnncl_assets = curBalanceDTO.getTradable_fnncl_assets() != null ? curBalanceDTO.getTradable_fnncl_assets() : 0;
        double ar_and_br = curBalanceDTO.getAr_and_br() != null ? curBalanceDTO.getAr_and_br() : 0;
        double st_loan = curBalanceDTO.getSt_loan() != null ? curBalanceDTO.getSt_loan() : 0;
        double tradable_fnncl_liab = curBalanceDTO.getTradable_fnncl_liab() != null ? curBalanceDTO.getTradable_fnncl_liab() : 0;
        double bp_and_ap = curBalanceDTO.getBp_and_ap() != null ? curBalanceDTO.getBp_and_ap() : 0;

        double total_holders_equity_v1 = total_holders_equity - (currency_funds + tradable_fnncl_assets + ar_and_br
                - st_loan - tradable_fnncl_liab - bp_and_ap);

        double sum = quarterIncomeDTOList.stream()
                .mapToDouble(incomeDTO -> incomeDTO.getNet_profit() !=null ? incomeDTO.getNet_profit(): 0)
                .sum();

        return sum /total_holders_equity_v1;
    }

}
