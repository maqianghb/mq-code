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

    private static final String HEADER ="编码,名称,K线日期,1月后股价波动,3月后股价波动,半年后股价波动,1年后股价波动" +
            ",资产负债率,市盈率TTM,pe分位值,市净率,pb分位值,股东权益合计,净资产收益率TTM" +
            ",营业收入,营业成本,毛利率,净利率,当季毛利率,当季净利率,当季毛利率同比,当季净利率同比" +
            ",营收同比,净利润同比,当季营收同比,当季净利润同比,固定资产同比,在建工程同比,商誉+无形/净资产,现金等价物/短期负债,总市值,经营现金流入" +
            ",经营现金流入/营收,经营现金净额,净利润,经营现金净额/净利润,应付票据及应付账款" +
            ",应收票据及应收账款,应付票据及应付账款/应收票据及应收账款,应收账款周转天数,存货周转天数";

    public static void main(String[] args) {
        StockIndicatorManager manager =new StockIndicatorManager();

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
//        List<String> stockCodeList = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");

        String kLineDate = "20230428";
        Integer reportYear = 2023;
        FinanceReportTypeEnum reportTypeEnum =FinanceReportTypeEnum.QUARTER_1;

//        manager.saveAndStatisticsAllAnalysisDTO(stockCodeList, kLineDate, reportYear, reportTypeEnum);

        int matchNum =1;
        manager.filterAndSaveAnalysisDTO(stockCodeList, kLineDate, reportYear, reportTypeEnum, matchNum);
    }

    private void filterAndSaveAnalysisDTO(List<String> stockCodeList, String kLineDate, Integer reportYear, FinanceReportTypeEnum reportTypeEnum
            , int matchNum){
        // 获取全部指标数据
        List<AnalyseIndicatorDTO> allIndicatorDTOList =Lists.newArrayList();
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

        // 筛选合适的数据
        List<AnalyseIndicatorDTO> filterIndicatorDTOList = this.filterByIndicator(allIndicatorDTOList, matchNum);
        filterIndicatorDTOList =Optional.ofNullable(filterIndicatorDTOList).orElse(Lists.newArrayList()).stream()
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb_p_1000))
                .collect(Collectors.toList());

        // 生成结果
        List<String> strIndicatorList =Lists.newArrayList();
        strIndicatorList.add(HEADER);
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
            String filterListName =String.format(StockConstant.INDICATOR_LIST_FILTER, kLineDate, strDateTime);
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
    private void getAndSaveIndicatorDTOPercent(String kLineDate, List<AnalyseIndicatorDTO> indicatorDTOList) {
        if(StringUtils.isBlank(kLineDate) || CollectionUtils.isEmpty(indicatorDTOList)){
            return ;
        }

        String header ="指标,10分位,25分位,50分位,75分位,90分位";
        List<String> strPercentList =Lists.newArrayList();
        strPercentList.add(header);

        List<Double> avg_roe_ttm_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm))
                .map(AnalyseIndicatorDTO::getAvg_roe_ttm)
                .collect(Collectors.toList());
        int totalSize =avg_roe_ttm_list.size();
        String msg = new StringBuilder().append("ROE_TTM")
                .append(",").append(avg_roe_ttm_list.get(totalSize*10/100))
                .append(",").append(avg_roe_ttm_list.get(totalSize*25/100))
                .append(",").append(avg_roe_ttm_list.get(totalSize*50/100))
                .append(",").append(avg_roe_ttm_list.get(totalSize*75/100))
                .append(",").append(avg_roe_ttm_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> gross_margin_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getGross_margin_rate() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getGross_margin_rate))
                .map(AnalyseIndicatorDTO::getGross_margin_rate)
                .collect(Collectors.toList());
        totalSize =gross_margin_rate_list.size();
        msg = new StringBuilder().append("毛利率")
                .append(",").append(gross_margin_rate_list.get(totalSize*10/100))
                .append(",").append(gross_margin_rate_list.get(totalSize*25/100))
                .append(",").append(gross_margin_rate_list.get(totalSize*50/100))
                .append(",").append(gross_margin_rate_list.get(totalSize*75/100))
                .append(",").append(gross_margin_rate_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> net_selling_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getNet_selling_rate() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_selling_rate))
                .map(AnalyseIndicatorDTO::getNet_selling_rate)
                .collect(Collectors.toList());
        totalSize =net_selling_rate_list.size();
        msg = new StringBuilder().append("净利率")
                .append(",").append(net_selling_rate_list.get(totalSize*10/100))
                .append(",").append(net_selling_rate_list.get(totalSize*25/100))
                .append(",").append(net_selling_rate_list.get(totalSize*50/100))
                .append(",").append(net_selling_rate_list.get(totalSize*75/100))
                .append(",").append(net_selling_rate_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> operating_income_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOperating_income_yoy))
                .map(AnalyseIndicatorDTO::getOperating_income_yoy)
                .collect(Collectors.toList());
        totalSize =operating_income_yoy_list.size();
        msg = new StringBuilder().append("营收同比")
                .append(",").append(operating_income_yoy_list.get(totalSize*10/100))
                .append(",").append(operating_income_yoy_list.get(totalSize*25/100))
                .append(",").append(operating_income_yoy_list.get(totalSize*50/100))
                .append(",").append(operating_income_yoy_list.get(totalSize*75/100))
                .append(",").append(operating_income_yoy_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> net_profit_atsopc_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getNet_profit_atsopc_yoy() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy))
                .map(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy)
                .collect(Collectors.toList());
        totalSize =net_profit_atsopc_yoy_list.size();
        msg = new StringBuilder().append("净利润同比")
                .append(",").append(net_profit_atsopc_yoy_list.get(totalSize*10/100))
                .append(",").append(net_profit_atsopc_yoy_list.get(totalSize*25/100))
                .append(",").append(net_profit_atsopc_yoy_list.get(totalSize*50/100))
                .append(",").append(net_profit_atsopc_yoy_list.get(totalSize*75/100))
                .append(",").append(net_profit_atsopc_yoy_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> cur_q_gross_margin_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate))
                .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate)
                .collect(Collectors.toList());
        totalSize =gross_margin_rate_list.size();
        msg = new StringBuilder().append("当季毛利率")
                .append(",").append(cur_q_gross_margin_rate_list.get(totalSize*10/100))
                .append(",").append(cur_q_gross_margin_rate_list.get(totalSize*25/100))
                .append(",").append(cur_q_gross_margin_rate_list.get(totalSize*50/100))
                .append(",").append(cur_q_gross_margin_rate_list.get(totalSize*75/100))
                .append(",").append(cur_q_gross_margin_rate_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> cur_q_net_selling_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate))
                .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate)
                .collect(Collectors.toList());
        totalSize =cur_q_net_selling_rate_list.size();
        msg = new StringBuilder().append("当季净利率")
                .append(",").append(cur_q_net_selling_rate_list.get(totalSize*10/100))
                .append(",").append(cur_q_net_selling_rate_list.get(totalSize*25/100))
                .append(",").append(cur_q_net_selling_rate_list.get(totalSize*50/100))
                .append(",").append(cur_q_net_selling_rate_list.get(totalSize*75/100))
                .append(",").append(cur_q_net_selling_rate_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> cur_q_operating_income_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_operating_income_yoy() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy))
                .map(AnalyseIndicatorDTO::getCur_q_operating_income_yoy)
                .collect(Collectors.toList());
        totalSize =cur_q_operating_income_yoy_list.size();
        msg = new StringBuilder().append("当季营收同比")
                .append(",").append(cur_q_operating_income_yoy_list.get(totalSize*10/100))
                .append(",").append(cur_q_operating_income_yoy_list.get(totalSize*25/100))
                .append(",").append(cur_q_operating_income_yoy_list.get(totalSize*50/100))
                .append(",").append(cur_q_operating_income_yoy_list.get(totalSize*75/100))
                .append(",").append(cur_q_operating_income_yoy_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> cur_q_net_profit_atsopc_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_net_profit_atsopc_yoy() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy))
                .map(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy)
                .collect(Collectors.toList());
        totalSize =cur_q_net_profit_atsopc_yoy_list.size();
        msg = new StringBuilder().append("当季净利润同比")
                .append(",").append(cur_q_net_profit_atsopc_yoy_list.get(totalSize*10/100))
                .append(",").append(cur_q_net_profit_atsopc_yoy_list.get(totalSize*25/100))
                .append(",").append(cur_q_net_profit_atsopc_yoy_list.get(totalSize*50/100))
                .append(",").append(cur_q_net_profit_atsopc_yoy_list.get(totalSize*75/100))
                .append(",").append(cur_q_net_profit_atsopc_yoy_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> receivable_turnover_days_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getReceivable_turnover_days))
                .map(AnalyseIndicatorDTO::getReceivable_turnover_days)
                .collect(Collectors.toList());
        totalSize =receivable_turnover_days_list.size();
        msg = new StringBuilder().append("应收周转天数")
                .append(",").append(receivable_turnover_days_list.get(totalSize*10/100))
                .append(",").append(receivable_turnover_days_list.get(totalSize*25/100))
                .append(",").append(receivable_turnover_days_list.get(totalSize*50/100))
                .append(",").append(receivable_turnover_days_list.get(totalSize*75/100))
                .append(",").append(receivable_turnover_days_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

        List<Double> inventory_turnover_days_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getInventory_turnover_days() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getInventory_turnover_days))
                .map(AnalyseIndicatorDTO::getInventory_turnover_days)
                .collect(Collectors.toList());
        totalSize =inventory_turnover_days_list.size();
        msg = new StringBuilder().append("存货周转天数")
                .append(",").append(inventory_turnover_days_list.get(totalSize*10/100))
                .append(",").append(inventory_turnover_days_list.get(totalSize*25/100))
                .append(",").append(inventory_turnover_days_list.get(totalSize*50/100))
                .append(",").append(inventory_turnover_days_list.get(totalSize*75/100))
                .append(",").append(inventory_turnover_days_list.get(totalSize*90/100))
                .toString();
        strPercentList.add(msg);

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
     * 筛选数据
     *
     * @param indicatorDTOList
     * @return
     */
    private List<AnalyseIndicatorDTO> filterByIndicator(List<AnalyseIndicatorDTO> indicatorDTOList, int matchNum){
        if(CollectionUtils.isEmpty(indicatorDTOList)){
            return Lists.newArrayList();
        }

        return indicatorDTOList.stream()
                .filter(indicatorDTO -> StringUtils.isNoneBlank(indicatorDTO.getName()) && !indicatorDTO.getName().contains("ST"))
                .filter(indicatorDTO -> indicatorDTO.getRevenue() !=null)
                .filter(indicatorDTO -> indicatorDTO.getPb_p_1000() !=null && indicatorDTO.getPb_p_1000() <=0.25)
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm() !=null && indicatorDTO.getAvg_roe_ttm() >=0.08 && indicatorDTO.getAvg_roe_ttm() <0.5)
                .filter(indicatorDTO -> indicatorDTO.getGross_margin_rate() !=null && indicatorDTO.getGross_margin_rate() >=0.1)
                .filter(indicatorDTO -> indicatorDTO.getNet_selling_rate() !=null && indicatorDTO.getNet_selling_rate() >=0.05)
                .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() !=null && indicatorDTO.getOperating_income_yoy() <=2)
                .filter(indicatorDTO -> indicatorDTO.getGw_ia_assert_rate() !=null && indicatorDTO.getGw_ia_assert_rate() <=0.3)
                .filter(indicatorDTO -> {
                    boolean result1 = indicatorDTO.getReceivable_turnover_days() !=null && indicatorDTO.getReceivable_turnover_days() <=200;
                    boolean result2 = indicatorDTO.getInventory_turnover_days() !=null && indicatorDTO.getInventory_turnover_days() <=300;
                    return result1 || result2;
                })
                .filter(indicatorDTO -> {
                    int curMatchNum =0;

                    //ROE指标
                    if(indicatorDTO.getAvg_roe_ttm() >=0.16){
                        curMatchNum ++;
                    }else if(indicatorDTO.getAvg_roe_ttm() >=0.10
                            && indicatorDTO.getAsset_liab_ratio() !=null && indicatorDTO.getAsset_liab_ratio() <=0.25){
                        curMatchNum ++;
                    }

                    // 毛利率和净利率指标
                    if(indicatorDTO.getGross_margin_rate() >=0.35 && indicatorDTO.getNet_selling_rate() >=0.12){
                        curMatchNum ++;
                    }

                    // 营收和利润增长指标
                    if(indicatorDTO.getOperating_income_yoy() !=null && indicatorDTO.getOperating_income_yoy() >=0.15
                            && indicatorDTO.getNet_profit_atsopc_yoy() !=null && indicatorDTO.getNet_profit_atsopc_yoy() >=0.15){
                        curMatchNum ++;
                    }

                    return curMatchNum >=matchNum;
                })
                .collect(Collectors.toList());
    }

    /**
     * 全量的分析数据
     *
     * @param kLineDate
     * @param reportYear
     * @param reportTypeEnum
     */
    private void saveAndStatisticsAllAnalysisDTO(List<String> stockCodeList, String kLineDate, Integer reportYear, FinanceReportTypeEnum reportTypeEnum){
        // 获取全部指标数据
        List<AnalyseIndicatorDTO> allIndicatorDTOList =Lists.newArrayList();
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

        // 统计百分位数据
        this.getAndSaveIndicatorDTOPercent(kLineDate, allIndicatorDTOList);

        System.out.println("indicatorList: "+ JSON.toJSONString(strIndicatorList));
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
        if(analyseIndicatorDTO.getAvg_roe_ttm() !=null){
            analyseIndicatorDTO.setAvg_roe_ttm(NumberUtil.format(analyseIndicatorDTO.getAvg_roe_ttm(), 3));
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

        this.assembleFinanceElement(indicatorElement, fileDate, code, year, typeEnum);
        this.assembleKLineElement(indicatorElement, fileDate, code, kLineDate);

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
        if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_1.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_1.getCode();
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_1));
        }else if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.HALF_YEAR.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_2.getCode();
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_2));
        }else if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_3.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_3.getCode();
            immutablePairList =Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year-1, FinanceReportTypeEnum.SINGLE_Q_3));
        }else if(Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.ALL_YEAR.getCode())){
            curQuarterType =FinanceReportTypeEnum.SINGLE_Q_4.getCode();
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
                    indicatorElement.setLastSamePeriodQuarterIncomeDTO(quarterIncomeDTO);
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

    private AnalyseIndicatorDTO getAnalyseIndicatorDTO(AnalyseIndicatorElement indicatorElement){
        JSONObject jsonIndicator =new JSONObject();
        jsonIndicator.put("code", indicatorElement.getCode());
        jsonIndicator.put("kLineDate", indicatorElement.getKLineDate());

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

        AnalyseIndicatorDTO indicatorDTO = JSON.parseObject(JSON.toJSONString(jsonIndicator), AnalyseIndicatorDTO.class);

        this.assembleAnalyseIndicator(indicatorDTO, indicatorElement);

        System.out.println("indicatorDTO: " + JSON.toJSONString(indicatorDTO));
        return indicatorDTO;
    }

    private void assembleAnalyseIndicator(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement){
        if(indicatorDTO ==null){
            return ;
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

        if(indicatorDTO.getAvg_roe_ttm() ==null){
            Double avg_roe_ttm =this.getAvgRoeTtm(indicatorDTO, indicatorElement);
            if(avg_roe_ttm !=null){
                indicatorDTO.setAvg_roe_ttm(avg_roe_ttm);
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
                QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastSamePeriodQuarterIncomeDTO();
                if(lastYearQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO.getRevenue() !=null && lastYearQuarterIncomeDTO.getOperating_cost() !=null){
                    double last_year_cur_q_gross_margin_rate = 1- lastYearQuarterIncomeDTO.getOperating_cost()/lastYearQuarterIncomeDTO.getRevenue();
                    indicatorDTO.setCur_q_gross_margin_rate_change(cur_q_gross_margin_rate /last_year_cur_q_gross_margin_rate -1);
                }
            }
        }

        if(indicatorDTO.getCur_q_net_selling_rate_change() ==null){
            Double cur_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate();
            if(cur_q_net_selling_rate !=null){
                QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastSamePeriodQuarterIncomeDTO();
                if(lastYearQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO.getRevenue() !=null && lastYearQuarterIncomeDTO.getNet_profit() !=null){
                    double last_year_cur_q_net_selling_rate = lastYearQuarterIncomeDTO.getNet_profit()/lastYearQuarterIncomeDTO.getRevenue();
                    double cur_q_net_selling_rate_change =(cur_q_net_selling_rate - cur_q_net_selling_rate)
                            /Math.abs(last_year_cur_q_net_selling_rate);
                    indicatorDTO.setCur_q_net_selling_rate_change(cur_q_net_selling_rate_change);
                }
            }
        }

        if(indicatorDTO.getCur_q_operating_income_yoy() ==null){
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastSamePeriodQuarterIncomeDTO();
            if(curQuarterIncomeDTO !=null && curQuarterIncomeDTO.getRevenue() !=null
                    && lastYearQuarterIncomeDTO !=null && lastYearQuarterIncomeDTO.getRevenue() !=null ){
                indicatorDTO.setCur_q_operating_income_yoy(curQuarterIncomeDTO.getRevenue()/lastYearQuarterIncomeDTO.getRevenue() -1);
            }
        }

        if(indicatorDTO.getCur_q_net_profit_atsopc_yoy() ==null){
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastSamePeriodQuarterIncomeDTO();
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
        if(indicatorDTO ==null || indicatorDTO.getPe() ==null){
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

}
