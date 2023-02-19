package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.CloseableHttpClientUtil;
import com.example.mq.wrapper.stock.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class XueQiuStockManager {

    private static final String BALANCE_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/balance.json";
    private static final String INCOME_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/income.json";
    private static final String CASH_FLOW_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/cash_flow.json";
    private static final String INDICATOR_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/indicator.json";
    private static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=72dea7021454f100bc72154931cdd6e0a6eecd76; xqat=72dea7021454f100bc72154931cdd6e0a6eecd76; xq_r_token=ad070cd3d55cc70f02f135fb52765cfbe11fa994; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY3ODY2Njg3MCwiY3RtIjoxNjc2NzY5MjYzMzk1LCJjaWQiOiJkOWQwbjRBWnVwIn0.Nj8mRahIOZOdDV_xSP-1gadsjJFShRVdR6TuaDk0mas3KtwFj5C53UpK59a0-ajhkwpXfuY0pUIlJggZSFbSqv-UZZprEfUoZd1WUnh0-J4AY0NUOTY3QVrKBYWlTAT06F7G-P3n_-Zeysevtsbpj1SdLdpHT1voHkby145lCtsO6ZBd6IGjl91l2Qpb2jiiia3eyna3zesxmIOKQlBhZV7UyUB01Nwrkz2IjzZcEL3Lb3Ofnljm2cUbACB__QqwKTra2DP637NPyI--py4PxhLgr8j2wy46BVYlwpSyIX3hadkpk6uJjdT0g1ozw6_mJrdKsOmidgjoNBCAfLa89w; u=661676769266332; Hm_lvt_1db88642e346389874251b5a1eded6e3=1676725308,1676766440,1676769265; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1676796442";
    private static final Integer count =20;

    public static void main(String[] args) {
        XueQiuStockManager manager =new XueQiuStockManager();

//        List<String> stockCodeList = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");
//        List<String> stockCodeList =manager.getStockCodeList();
    }

    public List<XueQiuStockBalanceDTO> queryBalanceList(String code, Integer count) {
        String url =new StringBuilder().append(BALANCE_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(StringUtils.EMPTY)
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockBalanceDTO> balanceDTOList =Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockBalanceDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockBalanceDTO balanceDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockBalanceDTO.class);
                    return balanceDTO;
                })
                .collect(Collectors.toList());
        balanceDTOList.forEach(balanceDTO -> {
                    balanceDTO.setCode(code);
                    balanceDTO.setName(stockName);
                });
        return balanceDTOList;
    }

    public List<XueQiuStockIncomeDTO> queryIncomeList(String code, Integer count) {
        String url =new StringBuilder().append(INCOME_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(StringUtils.EMPTY)
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockIncomeDTO> incomeDTOList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockIncomeDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockIncomeDTO incomeDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockIncomeDTO.class);
                    return incomeDTO;
                })
                .collect(Collectors.toList());
        incomeDTOList.forEach(incomeDTO -> {
                    incomeDTO.setCode(code);
                    incomeDTO.setName(stockName);
                });

        return incomeDTOList;
    }

    public List<XueQiuStockCashFlowDTO> queryCashFlowList(String code, Integer count) {
        String url =new StringBuilder().append(CASH_FLOW_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(StringUtils.EMPTY)
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockCashFlowDTO> cashFlowDTOList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockCashFlowDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockCashFlowDTO cashFlowDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockCashFlowDTO.class);
                    return cashFlowDTO;
                })
                .collect(Collectors.toList());
        cashFlowDTOList.forEach(cashFlowDTO -> {
            cashFlowDTO.setCode(code);
            cashFlowDTO.setName(stockName);
        });

        return cashFlowDTOList;
    }

    public List<XueQiuStockIndicatorDTO> queryIndicatorList(String code, Integer count) {
        String url =new StringBuilder().append(INDICATOR_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(System.currentTimeMillis())
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockIndicatorDTO> indicatorDTOList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockIndicatorDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockIndicatorDTO indicatorDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockIndicatorDTO.class);
                    return indicatorDTO;
                })
                .collect(Collectors.toList());
        indicatorDTOList.forEach(indicatorDTO -> {
            indicatorDTO.setCode(code);
            indicatorDTO.setName(stockName);
        });

        return indicatorDTOList;
    }

    private String getStockName(String strResult){
        return Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("quote_name"))
                .orElse(StringUtils.EMPTY);
    }

    private JSONObject getFieldValue(JSONObject jsonReport, List<Field> fields){
        JSONObject resultJson =new JSONObject();

        Long reportDateMillis = jsonReport.getLong("report_date");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(reportDateMillis).toInstant(), ZoneId.systemDefault());
        Integer year = localDateTime.getYear();
        resultJson.put("report_year", year);

        Integer month = localDateTime.getMonthValue();
        String reportType =StringUtils.EMPTY;
        switch (month){
            case 3:
                reportType ="1季报";
                break;
            case 6:
                reportType ="中报";
                break;
            case 9:
                reportType ="3季报";
                break;
            case 12:
                reportType ="年报";
                break;
            default:
                break;
        }
        if(StringUtils.isNotBlank(reportType)){
            resultJson.put("report_type", reportType);
        }

        for(Field field : fields){
            String key = field.getName();
            if(jsonReport.containsKey(key)){
                List<Double> valueList = JSON.parseArray(jsonReport.getString(key), Double.class);
                if(CollectionUtils.isNotEmpty(valueList)){
                    resultJson.put(key, valueList.get(0));
                }
            }
        }

        return resultJson;
    }

    private List<String> getStockCodeList(){
        List<String> stockCodeList =Lists.newArrayList();
        try {
            List<String> strList = FileUtils.readLines(new File("E:/stock_code_1_sh.txt"), Charset.forName("UTF-8"));
            if(CollectionUtils.isNotEmpty(strList)){
                stockCodeList.addAll(strList);
            }

            strList = FileUtils.readLines(new File("E:/stock_code_2_sz.txt"), Charset.forName("UTF-8"));
            if(CollectionUtils.isNotEmpty(strList)){
                stockCodeList.addAll(strList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stockCodeList;
    }

    private void getAndSaveQuarterIncome(){
        try {
            List<String> strList =FileUtils.readLines(new File("E:/income_list_1916.txt"), Charset.forName("UTF-8"));
            Map<String, List<XueQiuStockIncomeDTO>> codeIncomeMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockIncomeDTO.class))
                    .collect(Collectors.groupingBy(XueQiuStockIncomeDTO::getCode));
            if(MapUtils.isEmpty(codeIncomeMap)){
                return;
            }

            List<QuarterIncomeDTO> quarterIncomeDTOList =Lists.newArrayList();
            for(Map.Entry<String, List<XueQiuStockIncomeDTO>> entry : codeIncomeMap.entrySet()){
                String code = entry.getKey();
                Map<Integer, List<XueQiuStockIncomeDTO>> yearIncomeList = Optional.ofNullable(entry.getValue()).orElse(Lists.newArrayList()).stream()
                        .collect(Collectors.groupingBy(XueQiuStockIncomeDTO::getReport_year));
                if(MapUtils.isEmpty(yearIncomeList)){
                    continue;
                }

                for(Map.Entry<Integer, List<XueQiuStockIncomeDTO>> yearEntry : yearIncomeList.entrySet()){
                    Integer year =yearEntry.getKey();
                    Map<String, XueQiuStockIncomeDTO> reportTypeMap = Optional.ofNullable(yearEntry.getValue()).orElse(Lists.newArrayList()).stream()
                            .collect(Collectors.toMap(XueQiuStockIncomeDTO::getReport_type, dto -> dto, (val1, val2) -> val1));

                    XueQiuStockIncomeDTO incomeDTO1 = reportTypeMap.get("1季报");
                    XueQiuStockIncomeDTO incomeDTO2 = reportTypeMap.get("中报");
                    XueQiuStockIncomeDTO incomeDTO3 = reportTypeMap.get("3季报");
                    XueQiuStockIncomeDTO incomeDTO4 = reportTypeMap.get("年报");
                    if(incomeDTO1 !=null){
                        QuarterIncomeDTO quarterIncomeDTO =new QuarterIncomeDTO();
                        BeanUtils.copyProperties(incomeDTO1, quarterIncomeDTO);
                        quarterIncomeDTO.setReport_type("Q1");
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                    if(incomeDTO1 !=null && incomeDTO2 !=null){
                        QuarterIncomeDTO quarterIncomeDTO = this.getQuarterIncomeDTO(incomeDTO1, incomeDTO2);
                        quarterIncomeDTO.setReport_type("Q2");
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                    if(incomeDTO2 !=null && incomeDTO3 !=null){
                        QuarterIncomeDTO quarterIncomeDTO = this.getQuarterIncomeDTO(incomeDTO2, incomeDTO3);
                        quarterIncomeDTO.setReport_type("Q3");
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                    if(incomeDTO3 !=null && incomeDTO4 !=null){
                        QuarterIncomeDTO quarterIncomeDTO = this.getQuarterIncomeDTO(incomeDTO3, incomeDTO4);
                        quarterIncomeDTO.setReport_type("Q4");
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                }
            }

            List<String> strIncomeList = quarterIncomeDTOList.stream()
                    .sorted(Comparator.comparing(QuarterIncomeDTO::getCode)
                            .thenComparing(QuarterIncomeDTO::getReport_year).reversed()
                            .thenComparing(QuarterIncomeDTO::getReport_type).reversed())
                    .map(dto -> JSON.toJSONString(dto))
                    .collect(Collectors.toList());
            FileUtils.writeLines(new File("E:/quarter_income_List_2101.txt"), strIncomeList, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private QuarterIncomeDTO getQuarterIncomeDTO(XueQiuStockIncomeDTO incomeDTO1, XueQiuStockIncomeDTO incomeDTO2){
        if(incomeDTO1 ==null || incomeDTO2 ==null){
            return null;
        }

        QuarterIncomeDTO quarterIncomeDTO =new QuarterIncomeDTO();
        quarterIncomeDTO.setCode(incomeDTO1.getCode());
        quarterIncomeDTO.setName(incomeDTO1.getName());
        quarterIncomeDTO.setReport_year(incomeDTO1.getReport_year());

        if(incomeDTO1.getTotal_revenue() !=null && incomeDTO2.getTotal_revenue() !=null){
            quarterIncomeDTO.setTotal_revenue(incomeDTO2.getTotal_revenue() - incomeDTO1.getTotal_revenue());
        }

        if(incomeDTO1.getRevenue() !=null && incomeDTO2.getRevenue() !=null){
            quarterIncomeDTO.setRevenue(incomeDTO2.getRevenue() - incomeDTO1.getRevenue());
        }

        if(incomeDTO1.getOperating_costs() !=null && incomeDTO2.getOperating_costs() !=null){
            quarterIncomeDTO.setOperating_costs(incomeDTO2.getOperating_costs() - incomeDTO1.getOperating_costs());
        }

        if(incomeDTO1.getOperating_cost() !=null && incomeDTO2.getOperating_cost() !=null){
            quarterIncomeDTO.setOperating_cost(incomeDTO2.getOperating_cost() - incomeDTO1.getOperating_cost());
        }

        if(incomeDTO1.getOperating_taxes_and_surcharge() !=null && incomeDTO2.getOperating_taxes_and_surcharge() !=null){
            quarterIncomeDTO.setOperating_taxes_and_surcharge(incomeDTO2.getOperating_taxes_and_surcharge() - incomeDTO1.getOperating_taxes_and_surcharge());
        }

        if(incomeDTO1.getSales_fee() !=null && incomeDTO2.getSales_fee() !=null){
            quarterIncomeDTO.setSales_fee(incomeDTO2.getSales_fee() - incomeDTO1.getSales_fee());
        }

        if(incomeDTO1.getManage_fee() !=null && incomeDTO2.getManage_fee() !=null){
            quarterIncomeDTO.setManage_fee(incomeDTO2.getManage_fee() - incomeDTO1.getManage_fee());
        }

        if(incomeDTO1.getRad_cost() !=null && incomeDTO2.getRad_cost() !=null){
            quarterIncomeDTO.setRad_cost(incomeDTO2.getRad_cost() - incomeDTO1.getRad_cost());
        }

        if(incomeDTO1.getFinancing_expenses() !=null && incomeDTO2.getFinancing_expenses() !=null){
            quarterIncomeDTO.setFinancing_expenses(incomeDTO2.getFinancing_expenses() - incomeDTO1.getFinancing_expenses());
        }

        if(incomeDTO1.getFinance_cost_interest_fee() !=null && incomeDTO2.getFinance_cost_interest_fee() !=null){
            quarterIncomeDTO.setFinance_cost_interest_fee(incomeDTO2.getFinance_cost_interest_fee() - incomeDTO1.getFinance_cost_interest_fee());
        }

        if(incomeDTO1.getFinance_cost_interest_income() !=null && incomeDTO2.getFinance_cost_interest_income() !=null){
            quarterIncomeDTO.setFinance_cost_interest_income(incomeDTO2.getFinance_cost_interest_income() - incomeDTO1.getFinance_cost_interest_income());
        }

        if(incomeDTO1.getAsset_impairment_loss() !=null && incomeDTO2.getAsset_impairment_loss() !=null){
            quarterIncomeDTO.setAsset_impairment_loss(incomeDTO2.getAsset_impairment_loss() - incomeDTO1.getAsset_impairment_loss());
        }

        if(incomeDTO1.getCredit_impairment_loss() !=null && incomeDTO2.getCredit_impairment_loss() !=null){
            quarterIncomeDTO.setCredit_impairment_loss(incomeDTO2.getCredit_impairment_loss() - incomeDTO1.getCredit_impairment_loss());
        }

        if(incomeDTO1.getIncome_from_chg_in_fv() !=null && incomeDTO2.getIncome_from_chg_in_fv() !=null){
            quarterIncomeDTO.setIncome_from_chg_in_fv(incomeDTO2.getIncome_from_chg_in_fv() - incomeDTO1.getIncome_from_chg_in_fv());
        }

        if(incomeDTO1.getInvest_income() !=null && incomeDTO2.getInvest_income() !=null){
            quarterIncomeDTO.setInvest_income(incomeDTO2.getInvest_income() - incomeDTO1.getInvest_income());
        }

        if(incomeDTO1.getInvest_incomes_from_rr() !=null && incomeDTO2.getInvest_incomes_from_rr() !=null){
            quarterIncomeDTO.setInvest_incomes_from_rr(incomeDTO2.getInvest_incomes_from_rr() - incomeDTO1.getInvest_incomes_from_rr());
        }

        if(incomeDTO1.getAsset_disposal_income() !=null && incomeDTO2.getAsset_disposal_income() !=null){
            quarterIncomeDTO.setAsset_disposal_income(incomeDTO2.getAsset_disposal_income() - incomeDTO1.getAsset_disposal_income());
        }

        if(incomeDTO1.getOther_income() !=null && incomeDTO2.getOther_income() !=null){
            quarterIncomeDTO.setOther_income(incomeDTO2.getOther_income() - incomeDTO1.getOther_income());
        }

        if(incomeDTO1.getOp() !=null && incomeDTO2.getOp() !=null){
            quarterIncomeDTO.setOp(incomeDTO2.getOp() - incomeDTO1.getOp());
        }

        if(incomeDTO1.getNon_operating_income() !=null && incomeDTO2.getNon_operating_income() !=null){
            quarterIncomeDTO.setNon_operating_income(incomeDTO2.getNon_operating_income() - incomeDTO1.getNon_operating_income());
        }

        if(incomeDTO1.getNon_operating_payout() !=null && incomeDTO2.getNon_operating_payout() !=null){
            quarterIncomeDTO.setNon_operating_payout(incomeDTO2.getNon_operating_payout() - incomeDTO1.getNon_operating_payout());
        }

        if(incomeDTO1.getProfit_total_amt() !=null && incomeDTO2.getProfit_total_amt() !=null){
            quarterIncomeDTO.setProfit_total_amt(incomeDTO2.getProfit_total_amt() - incomeDTO1.getProfit_total_amt());
        }

        if(incomeDTO1.getIncome_tax_expenses() !=null && incomeDTO2.getIncome_tax_expenses() !=null){
            quarterIncomeDTO.setIncome_tax_expenses(incomeDTO2.getIncome_tax_expenses() - incomeDTO1.getIncome_tax_expenses());
        }

        if(incomeDTO1.getNet_profit() !=null && incomeDTO2.getNet_profit() !=null){
            quarterIncomeDTO.setNet_profit(incomeDTO2.getNet_profit() - incomeDTO1.getNet_profit());
        }

        if(incomeDTO1.getContinous_operating_np() !=null && incomeDTO2.getContinous_operating_np() !=null){
            quarterIncomeDTO.setContinous_operating_np(incomeDTO2.getContinous_operating_np() - incomeDTO1.getContinous_operating_np());
        }

        if(incomeDTO1.getNet_profit_atsopc() !=null && incomeDTO2.getNet_profit_atsopc() !=null){
            quarterIncomeDTO.setNet_profit_atsopc(incomeDTO2.getNet_profit_atsopc() - incomeDTO1.getNet_profit_atsopc());
        }

        if(incomeDTO1.getMinority_gal() !=null && incomeDTO2.getMinority_gal() !=null){
            quarterIncomeDTO.setMinority_gal(incomeDTO2.getMinority_gal() - incomeDTO1.getMinority_gal());
        }

        if(incomeDTO1.getNet_profit_after_nrgal_atsolc() !=null && incomeDTO2.getNet_profit_after_nrgal_atsolc() !=null){
            quarterIncomeDTO.setNet_profit_after_nrgal_atsolc(incomeDTO2.getNet_profit_after_nrgal_atsolc() - incomeDTO1.getNet_profit_after_nrgal_atsolc());
        }

        if(incomeDTO1.getBasic_eps() !=null && incomeDTO2.getBasic_eps() !=null){
            quarterIncomeDTO.setBasic_eps(incomeDTO2.getBasic_eps() - incomeDTO1.getBasic_eps());
        }

        if(incomeDTO1.getDlt_earnings_per_share() !=null && incomeDTO2.getDlt_earnings_per_share() !=null){
            quarterIncomeDTO.setDlt_earnings_per_share(incomeDTO2.getDlt_earnings_per_share() - incomeDTO1.getDlt_earnings_per_share());
        }

        if(incomeDTO1.getOthr_compre_income() !=null && incomeDTO2.getOthr_compre_income() !=null){
            quarterIncomeDTO.setOthr_compre_income(incomeDTO2.getOthr_compre_income() - incomeDTO1.getOthr_compre_income());
        }

        if(incomeDTO1.getOthr_compre_income_atoopc() !=null && incomeDTO2.getOthr_compre_income_atoopc() !=null){
            quarterIncomeDTO.setOthr_compre_income_atoopc(incomeDTO2.getOthr_compre_income_atoopc() - incomeDTO1.getOthr_compre_income_atoopc());
        }

        if(incomeDTO1.getTotal_compre_income() !=null && incomeDTO2.getTotal_compre_income() !=null){
            quarterIncomeDTO.setTotal_compre_income(incomeDTO2.getTotal_compre_income() - incomeDTO1.getTotal_compre_income());
        }

        if(incomeDTO1.getTotal_compre_income_atsopc() !=null && incomeDTO2.getTotal_compre_income_atsopc() !=null){
            quarterIncomeDTO.setTotal_compre_income_atsopc(incomeDTO2.getTotal_compre_income_atsopc() - incomeDTO1.getTotal_compre_income_atsopc());
        }

        if(incomeDTO1.getTotal_compre_income_atms() !=null && incomeDTO2.getTotal_compre_income_atms() !=null){
            quarterIncomeDTO.setTotal_compre_income_atms(incomeDTO2.getTotal_compre_income_atms() - incomeDTO1.getTotal_compre_income_atms());
        }

        return quarterIncomeDTO;
    }

}
