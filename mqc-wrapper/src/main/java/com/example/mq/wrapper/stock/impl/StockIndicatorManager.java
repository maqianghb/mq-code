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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StockIndicatorManager {

    private static final String HEADER ="编码,名称,资产负债率,市盈率TTM,pe分位值,市净率,pb分位值,股东权益合计,净资产收益率TTM" +
            ",营业收入,营业成本,毛利率,净利率,当季毛利率,当季净利率,当季毛利率同比,当季净利率同比" +
            ",营收同比,净利润同比,当季营收同比,当季净利润同比,固定资产同比,在建工程同比,商誉+无形/总资产,现金等价物/短期负债,总市值,经营现金流入" +
            ",经营现金流入/营收,经营现金净额,净利润,经营现金净额/净利润,应付票据及应付账款" +
            ",应收票据及应收账款,应付票据及应付账款/应收票据及应收账款,应收账款周转天数,存货周转天数";

    public static void main(String[] args) {
        StockIndicatorManager manager =new StockIndicatorManager();

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
//        List<String> stockCodeList = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");

        List<String> indicatorList =Lists.newArrayList();
        indicatorList.add(HEADER);
        for(String stockCode : stockCodeList){
            try {
                AnalyseIndicatorElement indicatorElement = manager.getIndicatorElement(StockConstant.FILE_DATE
                        , stockCode, 2022, FinanceReportTypeEnum.ALL_YEAR);
                AnalyseIndicatorDTO analyseIndicatorDTO = manager.getAnalyseIndicatorDTO(indicatorElement);
                manager.formatAnalyseIndicatorDTO(analyseIndicatorDTO);

                Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(analyseIndicatorDTO));
                StringBuilder indicatorBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName ());
                    indicatorBuilder.append(",").append(value);
                }
                indicatorList.add(indicatorBuilder.toString().substring(1));
            } catch (Exception e) {
                System.out.println("errCode: " + stockCode);
                e.printStackTrace();
            }
        }

        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String analysisListName =String.format(StockConstant.INDICATOR_LIST_ANALYSIS, strDateTime);
            FileUtils.writeLines(new File(analysisListName), "UTF-8", indicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("indicatorList: "+ JSON.toJSONString(indicatorList));
    }

    private void formatAnalyseIndicatorDTO(AnalyseIndicatorDTO analyseIndicatorDTO){
        if(analyseIndicatorDTO ==null){
            return ;
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

    private AnalyseIndicatorElement getIndicatorElement(String fileDate, String code, Integer year, FinanceReportTypeEnum typeEnum){
        AnalyseIndicatorElement indicatorElement =new AnalyseIndicatorElement();
        indicatorElement.setCode(code);
        indicatorElement.setReportYear(year);
        indicatorElement.setReportType(typeEnum.getCode());

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

        List<XueQiuStockKLineDTO> kLineDTOList = localStockDataManager.getKLineList(fileDate, code, KLineTypeEnum.DAY, 1000);
        if(CollectionUtils.isNotEmpty(kLineDTOList)){
            indicatorElement.setKLineDTOList(kLineDTOList);
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

        return indicatorElement;
    }

    private AnalyseIndicatorDTO getAnalyseIndicatorDTO(AnalyseIndicatorElement indicatorElement){
        JSONObject jsonIndicator =new JSONObject();
        jsonIndicator.put("code", indicatorElement.getCode());

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
            XueQiuStockKLineDTO curKLineDTO = indicatorElement.getKLineDTOList().stream()
                    .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                    .collect(Collectors.toList())
                    .get(0);
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(curKLineDTO));
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
                    indicatorDTO.setCur_q_net_selling_rate_change(cur_q_net_selling_rate /last_year_cur_q_net_selling_rate -1);
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
                indicatorDTO.setCur_q_net_profit_atsopc_yoy(curQuarterIncomeDTO.getNet_profit()/lastYearQuarterIncomeDTO.getNet_profit() -1);
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
                Double total_assets = curBalanceDTO.getTotal_assets() !=null ? curBalanceDTO.getTotal_assets() : 0.01;
                indicatorDTO.setGw_ia_assert_rate((goodwill+intangible_assets)/total_assets);
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
