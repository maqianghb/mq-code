package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class LocalStockDataManager {

    public static void main(String[] args) {
        LocalStockDataManager manager =new LocalStockDataManager();
        manager.queryAndSaveKLineList();
//        manager.queryAndSaveFinanceList();

        System.out.println("end. ");
    }

    /**
     * 查询并保存全网财务数据
     */
    public void queryAndSaveFinanceList(){
        List<String> stockCodeList = this.getStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManager();

        // 资产负债数据
        try {
            List<XueQiuStockBalanceDTO> balanceDTOList = Lists.newArrayList();
            for(String stockCode : stockCodeList) {
                List<XueQiuStockBalanceDTO> tmpBalanceDTOList = xueQiuStockManager.queryBalanceList(stockCode, StockConstant.COUNT);
                if (CollectionUtils.isNotEmpty(tmpBalanceDTOList)) {
                    balanceDTOList.addAll(tmpBalanceDTOList);
                }
            }

            List<String> strBalanceDTOList = balanceDTOList.stream()
                    .sorted(Comparator.comparing(XueQiuStockBalanceDTO::getCode)
                            .thenComparing(XueQiuStockBalanceDTO::getReport_year).reversed())
                    .map(balanceDTO -> JSON.toJSONString(balanceDTO))
                    .collect(Collectors.toList());
            FileUtils.writeLines(new File(StockConstant.BALANCE_LIST), strBalanceDTOList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 利润数据
        try {
            List<XueQiuStockIncomeDTO> incomeDTOList = Lists.newArrayList();
            for(String stockCode : stockCodeList) {
                List<XueQiuStockIncomeDTO> tmpIncomeDTOList = xueQiuStockManager.queryIncomeList(stockCode, StockConstant.COUNT);
                if (CollectionUtils.isNotEmpty(tmpIncomeDTOList)) {
                    incomeDTOList.addAll(tmpIncomeDTOList);
                }
            }

            List<String> strIncomeDTOList = incomeDTOList.stream()
                    .sorted(Comparator.comparing(XueQiuStockIncomeDTO::getCode)
                            .thenComparing(XueQiuStockIncomeDTO::getReport_year).reversed())
                    .map(incomeDTO -> JSON.toJSONString(incomeDTO))
                    .collect(Collectors.toList());
            FileUtils.writeLines(new File(StockConstant.INCOME_LIST), strIncomeDTOList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 现金流数据
        try {
            List<XueQiuStockCashFlowDTO> cashFlowDTOList = Lists.newArrayList();
            for(String stockCode : stockCodeList) {
                List<XueQiuStockCashFlowDTO> tmpCashFlowDTOList = xueQiuStockManager.queryCashFlowList(stockCode, StockConstant.COUNT);
                if (CollectionUtils.isNotEmpty(tmpCashFlowDTOList)) {
                    cashFlowDTOList.addAll(tmpCashFlowDTOList);
                }
            }

            List<String> strCashFlowDTOList = cashFlowDTOList.stream()
                    .sorted(Comparator.comparing(XueQiuStockCashFlowDTO::getCode)
                            .thenComparing(XueQiuStockCashFlowDTO::getReport_year).reversed())
                    .map(cashFlowDTO -> JSON.toJSONString(cashFlowDTO))
                    .collect(Collectors.toList());
            FileUtils.writeLines(new File(StockConstant.CASH_FLOW_LIST), strCashFlowDTOList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 指标数据
        try {
            List<XueQiuStockIndicatorDTO> indicatorDTOList = Lists.newArrayList();
            for(String stockCode : stockCodeList) {
                List<XueQiuStockIndicatorDTO> tmpIndicatorDTOList = xueQiuStockManager.queryIndicatorList(stockCode, StockConstant.COUNT);
                if (CollectionUtils.isNotEmpty(tmpIndicatorDTOList)) {
                    indicatorDTOList.addAll(tmpIndicatorDTOList);
                }
            }

            List<String> strIndicatorDTOList = indicatorDTOList.stream()
                    .sorted(Comparator.comparing(XueQiuStockIndicatorDTO::getCode)
                            .thenComparing(XueQiuStockIndicatorDTO::getReport_year).reversed())
                    .map(indicatorDTO -> JSON.toJSONString(indicatorDTO))
                    .collect(Collectors.toList());
            FileUtils.writeLines(new File(StockConstant.INDICATOR_LIST_XQ), strIndicatorDTOList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 单季数据
        this.getAndSaveQuarterIncome();
    }

    /**
     * 查询并保存全网K线数据
     *
     * @return
     */
    public void queryAndSaveKLineList() {
        List<String> stockCodeList = this.getStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManager();

        // 查询
        for(String stockCode : stockCodeList){
            String kLineFileName =String.format(StockConstant.KLINE_LIST_DAY, stockCode);
            try {
                List<XueQiuStockKLineDTO> tmpKLineDTOList = xueQiuStockManager.queryKLineList(stockCode, "day", System.currentTimeMillis(), 1200);
                if(CollectionUtils.isNotEmpty(tmpKLineDTOList)){
                    List<String> strKLineList = tmpKLineDTOList.stream()
                            .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                            .map(dto -> JSON.toJSONString(dto))
                            .collect(Collectors.toList());
                    FileUtils.writeLines(new File(kLineFileName), strKLineList, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveStockCodeList(){
        try {
            List<String> strList = FileUtils.readLines(new File(StockConstant.SH_STOCK_LIST), Charset.forName("UTF-8"));
            List<String> formatCodeList =Lists.newArrayList();
            for (String str : strList){
                String[] split = StringUtils.split(str, ", ");
                if(split ==null || split.length <2){
                    continue;
                }

                String code =split[1];
                String formatCode =new StringBuilder()
                        .append("SH").append(code)
                        .toString();
                formatCodeList.add(formatCode);
            }
            FileUtils.writeLines(new File(StockConstant.SH_STOCK_CODE_LIST), formatCodeList, true);

            strList = FileUtils.readLines(new File(StockConstant.SZ_STOCK_LIST), Charset.forName("UTF-8"));
            formatCodeList =Lists.newArrayList();
            for (String str : strList){
                String[] split = StringUtils.split(str, ", ");
                if(split ==null || split.length <2){
                    continue;
                }

                String code =split[1];
                String formatCode =new StringBuilder()
                        .append("SZ").append(code)
                        .toString();
                formatCodeList.add(formatCode);
            }
            FileUtils.writeLines(new File(StockConstant.SZ_STOCK_CODE_LIST), formatCodeList, true);


            strList = FileUtils.readLines(new File(StockConstant.CYB_STOCK_LIST), Charset.forName("UTF-8"));
            formatCodeList =Lists.newArrayList();
            for (String str : strList){
                String[] split = StringUtils.split(str, ", ");
                if(split ==null || split.length <2){
                    continue;
                }

                String code =split[1];
                String formatCode =new StringBuilder()
                        .append("SZ").append(code)
                        .toString();
                formatCodeList.add(formatCode);
            }
            FileUtils.writeLines(new File(StockConstant.CYB_STOCK_CODE_LIST), formatCodeList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取全部编码
     *
     * @return
     */
    public List<String> getStockCodeList(){
        List<String> stockCodeList = Lists.newArrayList();
        try {
            List<String> strList = FileUtils.readLines(new File(StockConstant.SH_STOCK_CODE_LIST), Charset.forName("UTF-8"));
            if(CollectionUtils.isNotEmpty(strList)){
                stockCodeList.addAll(strList);
            }

            strList = FileUtils.readLines(new File(StockConstant.SZ_STOCK_CODE_LIST), Charset.forName("UTF-8"));
            if(CollectionUtils.isNotEmpty(strList)){
                stockCodeList.addAll(strList);
            }

            strList = FileUtils.readLines(new File(StockConstant.CYB_STOCK_CODE_LIST), Charset.forName("UTF-8"));
            if(CollectionUtils.isNotEmpty(strList)){
                stockCodeList.addAll(strList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockCodeList.stream().sorted().collect(Collectors.toList());
    }

    /**
     * 计算并保存单季利润数据
     */
    private void getAndSaveQuarterIncome(){
        try {
            List<String> strList =FileUtils.readLines(new File(StockConstant.INCOME_LIST), Charset.forName("UTF-8"));
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
            FileUtils.writeLines(new File(StockConstant.INCOME_LIST_Q), strIncomeList, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算单季利润数据
     *
     * @param incomeDTO1
     * @param incomeDTO2
     * @return
     */
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
