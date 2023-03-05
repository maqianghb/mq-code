package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

public class StockIndicatorManager {

    private static final String HEADER ="编码,名称,资产负债率,市盈率TTM,pe分位值,市净率,pe分位值,净资产收益率TTM,营业收入,营业成本,销售毛利率,销售净利率,营业收入同比增长,净利润同比增长,总市值,经营活动现金流入小计,经营活动现金流入小计/营业收入,经营活动产生的现金流量净额,净利润,经营活动产生的现金流量净额/净利润,应付票据及应付账款,应收票据及应收账款,应付票据及应付账款/应收票据及应收账款,应收账款周转天数,存货周转天数";

    public static void main(String[] args) {
        StockIndicatorManager manager =new StockIndicatorManager();

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
//        List<String> stockCodeList = localStockDataManager.getStockCodeList();
        List<String> stockCodeList = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");

        List<String> indicatorList =Lists.newArrayList();
        indicatorList.add(HEADER);
        for(String stockCode : stockCodeList){
            try {
                AnalyseIndicatorDTO analyseIndicatorDTO = manager.getAnalyseIndicatorDTO(stockCode,  2022, FinanceReportTypeEnum.QUARTER_3);
                manager.assembleAnalyseIndicator(analyseIndicatorDTO);
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
            FileUtils.writeLines(new File(StockConstant.INDICATOR_LIST_ANALYSIS), "UTF-8", indicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("indicatorList: "+ JSON.toJSONString(indicatorList));
    }

    private AnalyseIndicatorDTO getAnalyseIndicatorDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        JSONObject jsonIndicator =new JSONObject();
        jsonIndicator.put("code", code);

        XueQiuStockBalanceDTO balanceDTO = this.getBalanceDTO(code, year, typeEnum);
        if(balanceDTO !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(balanceDTO));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        XueQiuStockIncomeDTO incomeDTO = this.getIncomeDTO(code, year, typeEnum);
        if(incomeDTO !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(incomeDTO));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        XueQiuStockCashFlowDTO cashFlowDTO = this.getCashFlowDTO(code, year, typeEnum);
        if(cashFlowDTO !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(cashFlowDTO));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        XueQiuStockIndicatorDTO indicatorDTO = this.getFromXQIndicatorList(code, year, typeEnum);
        if(indicatorDTO !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        XueQiuStockKLineDTO kLineDTO = this.getFromKLineList(code, KLineTypeEnum.DAY);
        if(kLineDTO !=null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(kLineDTO));
            for(String key : jsonObject.keySet()){
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        System.out.println("jsonIndicator: " + JSON.toJSONString(jsonIndicator));
        return JSON.parseObject(JSON.toJSONString(jsonIndicator), AnalyseIndicatorDTO.class);
    }

    private void assembleAnalyseIndicator(AnalyseIndicatorDTO indicatorDTO){
        if(indicatorDTO ==null){
            return ;
        }
        if(indicatorDTO.getGross_margin_rate() ==null){
            Double operating_cost = indicatorDTO.getOperating_cost();
            Double revenue = indicatorDTO.getRevenue();
            if(operating_cost !=null && revenue !=null){
                indicatorDTO.setGross_margin_rate(1 - operating_cost /revenue);
            }
        }

        if(indicatorDTO.getCi_oi_rate() ==null){
            Double sub_total_of_ci_from_oa = indicatorDTO.getSub_total_of_ci_from_oa();
            Double revenue = indicatorDTO.getRevenue();
            if(sub_total_of_ci_from_oa !=null && revenue !=null){
                indicatorDTO.setCi_oi_rate(sub_total_of_ci_from_oa /revenue);
            }
        }

        if(indicatorDTO.getNcf_pri_rate() ==null){
            Double ncf_from_oa = indicatorDTO.getNcf_from_oa();
            Double net_profit_atsopc = indicatorDTO.getNet_profit_atsopc();
            if(ncf_from_oa !=null && net_profit_atsopc !=null){
                indicatorDTO.setNcf_pri_rate(ncf_from_oa /net_profit_atsopc);
            }
        }

        if(indicatorDTO.getAp_ar_rate() ==null){
            Double bp_and_ap = indicatorDTO.getBp_and_ap();
            Double ar_and_br = indicatorDTO.getAr_and_br();
            if(bp_and_ap !=null && ar_and_br !=null){
                indicatorDTO.setAp_ar_rate(bp_and_ap /ar_and_br);
            }
        }

    }

    /**
     * 负债表指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private XueQiuStockBalanceDTO getBalanceDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.BALANCE_LIST), Charset.forName("UTF-8"));
            XueQiuStockBalanceDTO stockBalanceDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockBalanceDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);

            return stockBalanceDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 利润表指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private XueQiuStockIncomeDTO getIncomeDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.INCOME_LIST), Charset.forName("UTF-8"));
            XueQiuStockIncomeDTO stockIncomeDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockIncomeDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);

            return stockIncomeDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 利润表指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private XueQiuStockCashFlowDTO getCashFlowDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.CASH_FLOW_LIST), Charset.forName("UTF-8"));
            XueQiuStockCashFlowDTO stockCashFlowDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockCashFlowDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);

            return stockCashFlowDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 指标查询
     *
     * @param code
     * @param year
     * @param typeEnum
     * @return
     */
    private XueQiuStockIndicatorDTO getFromXQIndicatorList(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.INDICATOR_LIST_XQ), Charset.forName("UTF-8"));
            XueQiuStockIndicatorDTO stockIndicatorDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockIndicatorDTO.class))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);

            return stockIndicatorDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * K线指标查询
     *
     * @param code
     * @param typeEnum
     * @return
     */
    private XueQiuStockKLineDTO getFromKLineList(String code, KLineTypeEnum typeEnum){
        String klineListFileName = StringUtils.EMPTY;
        if(Objects.equals(typeEnum.getCode(), KLineTypeEnum.DAY.getCode())){
            klineListFileName = String.format(StockConstant.KLINE_LIST_DAY, code);
        }
        try {
            List<String> strList =FileUtils.readLines(new File(klineListFileName), Charset.forName("UTF-8"));
            XueQiuStockKLineDTO stockKLineDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockKLineDTO.class))
                    .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                    .findFirst()
                    .orElse(null);

            return stockKLineDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
