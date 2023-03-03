package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class StockIndicatorManager {

    public static void main(String[] args) {
        StockIndicatorManager manager =new StockIndicatorManager();

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();

        List<String> indicatorList =Lists.newArrayList();
        for(int i=0; i<100; i++){
            try {
                AnalyseIndicatorDTO analyseIndicatorDTO = manager.getAnalyseIndicatorDTO(stockCodeList.get(i), 2022, FinanceReportTypeEnum.QUARTER_3);
                Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(analyseIndicatorDTO));
                StringBuilder indicatorBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName());
                    indicatorBuilder.append(",").append(value);
                }
                indicatorList.add(indicatorBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileUtils.writeLines(new File(StockConstant.INDICATOR_LIST_ANALYSIS), indicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("indicatorList: "+ JSON.toJSONString(indicatorList));
    }

    private AnalyseIndicatorDTO getAnalyseIndicatorDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        JSONObject jsonObject =new JSONObject();
        Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
        for(Field field : fields){
            Double value = this.getIndicatorByKey(field.getName(), code, year, typeEnum);
            if(value !=null){
                jsonObject.put(field.getName(), value);
            }
        }

        AnalyseIndicatorDTO indicatorDTO =JSON.parseObject(JSON.toJSONString(jsonObject), AnalyseIndicatorDTO.class);
        indicatorDTO.setCode(code);

        return indicatorDTO;
    }

    /**
     * 获取指标值
     *
     * @param key
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private Double getIndicatorByKey(String key, String code, Integer year, FinanceReportTypeEnum typeEnum){
        switch(key){
            case "asset_liab_ratio":
            case "bp_and_ap":
            case "ar_and_br":{
                return this.getFromBalanceList(key, code, year, typeEnum);
            }
            case "revenue":{
                return this.getFromIncomeList(key, code, year, typeEnum);
            }
            case "sub_total_of_ci_from_oa":
            case "ncf_from_oa":{
                return this.getFromCashFlowList(key, code, year, typeEnum);
            }
            case "operating_income_yoy":
            case "net_profit_atsopc_yoy":
            case "net_selling_rate":
            case "net_profit_atsopc":
            case "receivable_turnover_days":
            case "inventory_turnover_days": {
                return this.getFromXQIndicatorList(key, code, year, typeEnum);
            }
            case "pe":
            case "pb":
            case "market_capital":{
                return this.getFromKLineList(key, code, KLineTypeEnum.DAY);
            }
            case "gross_margin_rate":{
                Double operating_cost = this.getFromIncomeList("operating_cost", code, year, typeEnum);
                Double revenue = this.getFromIncomeList("revenue", code, year, typeEnum);
                if(operating_cost !=null && revenue !=null){
                    return 1 - operating_cost /revenue;
                }else{
                    return null;
                }
            }
            case "ci_oi_rate":{
                Double sub_total_of_ci_from_oa =this.getFromCashFlowList("sub_total_of_ci_from_oa", code, year, typeEnum);
                Double revenue =this.getFromIncomeList("revenue", code, year, typeEnum);
                if(sub_total_of_ci_from_oa !=null && revenue !=null){
                    return sub_total_of_ci_from_oa /revenue;
                }else{
                    return null;
                }
            }
            case "ncf_pri_rate":{
                Double ncf_from_oa =this.getFromCashFlowList("ncf_from_oa", code, year, typeEnum);
                Double net_profit_atsopc =this.getFromXQIndicatorList("net_profit_atsopc", code, year, typeEnum);
                if(ncf_from_oa !=null && net_profit_atsopc !=null){
                    return ncf_from_oa /net_profit_atsopc;
                }else{
                    return null;
                }
            }
            case "ap_ar_rate":{
                Double bp_and_ap =this.getFromBalanceList("bp_and_ap", code, year, typeEnum);
                Double ar_and_br =this.getFromBalanceList("ar_and_br", code, year, typeEnum);
                if(bp_and_ap !=null && ar_and_br !=null){
                    return bp_and_ap /ar_and_br;
                }else{
                    return null;
                }
            }
            case "pe_p_1000":
            case "pb_p_1000":
            case "avg_roe_ttm":{
                return null;
            }
            default:{
                return null;
            }
        }
    }

    /**
     * 负债表指标查询
     *
     * @param key
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private Double getFromBalanceList(String key, String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.BALANCE_LIST), Charset.forName("UTF-8"));
            XueQiuStockBalanceDTO stockBalanceDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockBalanceDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
            if(stockBalanceDTO !=null){
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(stockBalanceDTO));
                if(jsonObject.containsKey(key)){
                    return jsonObject.getDouble(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 利润表指标查询
     *
     * @param key
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private Double getFromIncomeList(String key, String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.INCOME_LIST), Charset.forName("UTF-8"));
            XueQiuStockIncomeDTO stockIncomeDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockIncomeDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
            if(stockIncomeDTO !=null){
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(stockIncomeDTO));
                if(jsonObject.containsKey(key)){
                    return jsonObject.getDouble(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 利润表指标查询
     *
     * @param key
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private Double getFromCashFlowList(String key, String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.CASH_FLOW_LIST), Charset.forName("UTF-8"));
            XueQiuStockCashFlowDTO stockCashFlowDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockCashFlowDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
            if(stockCashFlowDTO !=null){
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(stockCashFlowDTO));
                if(jsonObject.containsKey(key)){
                    return jsonObject.getDouble(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 指标查询
     *
     * @param key
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private Double getFromXQIndicatorList(String key, String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.INDICATOR_LIST_XQ), Charset.forName("UTF-8"));
            XueQiuStockIndicatorDTO stockIndicatorDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockIndicatorDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
            if(stockIndicatorDTO !=null){
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(stockIndicatorDTO));
                if(jsonObject.containsKey(key)){
                    return jsonObject.getDouble(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * K线指标查询
     *
     * @param key
     * @param code
     * @param typeEnum
     * @return
     */
    private Double getFromKLineList(String key, String code, KLineTypeEnum typeEnum){
        String klineListFileName = StringUtils.EMPTY;
        if(Objects.equals(typeEnum.getCode(), KLineTypeEnum.DAY.getCode())){
            klineListFileName = String.format(StockConstant.KLINE_LIST_DAY, code);
        }
        try {
            List<String> strList =FileUtils.readLines(new File(klineListFileName), Charset.forName("UTF-8"));
            XueQiuStockKLineDTO stockKLineDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockKLineDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                    .findFirst()
                    .orElse(null);
            if(stockKLineDTO !=null){
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(stockKLineDTO));
                if(jsonObject.containsKey(key)){
                    return jsonObject.getDouble(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
