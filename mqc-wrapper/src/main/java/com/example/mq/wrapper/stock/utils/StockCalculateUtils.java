package com.example.mq.wrapper.stock.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.model.*;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiIndustryHoldShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: maqiang
 * @CreateTime: 2025-09-14 09:59:10
 * @Description:
 */
public class StockCalculateUtils {

    /**
     * 计算分析指标
     *
     * @param indicatorElement
     * @return
     */
    public static AnalyseIndicatorDTO calculateAnalyseIndicatorDTO(AnalyseIndicatorElement indicatorElement) {
        JSONObject jsonIndicator = new JSONObject();
        jsonIndicator.put("code", indicatorElement.getCode());
        jsonIndicator.put("kLineDate", indicatorElement.getKLineDate());

        if (indicatorElement.getCompanyDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCompanyDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (indicatorElement.getCurBalanceDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurBalanceDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (indicatorElement.getCurIncomeDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurIncomeDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (indicatorElement.getCurCashFlowDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurCashFlowDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (indicatorElement.getCurIndicatorDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getCurIndicatorDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (CollectionUtils.isNotEmpty(indicatorElement.getKLineDTOList())) {
            XueQiuStockKLineDTO queryDateKLineDTO = indicatorElement.getKLineDTOList().stream()
                    .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                    .collect(Collectors.toList())
                    .get(0);
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(queryDateKLineDTO));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (indicatorElement.getHolderIncreaseDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getHolderIncreaseDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        if (indicatorElement.getFreeShareDTO() != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorElement.getFreeShareDTO()));
            for (String key : jsonObject.keySet()) {
                jsonIndicator.put(key, jsonObject.getString(key));
            }
        }

        AnalyseIndicatorDTO indicatorDTO = JSON.parseObject(JSON.toJSONString(jsonIndicator), AnalyseIndicatorDTO.class);

        // 计算指标值
        assembleAnalyseIndicator(indicatorDTO, indicatorElement);

        System.out.println("indicatorDTO: " + JSON.toJSONString(indicatorDTO));
        return indicatorDTO;
    }

    /**
     * 计算指标值
     *
     * @param indicatorDTO
     * @param indicatorElement
     */
    private static void assembleAnalyseIndicator(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null) {
            return;
        }

        if (indicatorDTO.getMa_20_equal_ma_60_num() == null) {
            Integer value = StockCalculateUtils.getMa20EqualMa60Value(indicatorDTO, indicatorElement);
            indicatorDTO.setMa_20_equal_ma_60_num(value != null ? value : 0);
        }

        if (indicatorDTO.getPe_p_1000() == null) {
            Double pe_p_1000 = StockCalculateUtils.getPeP1000Value(indicatorDTO, indicatorElement);
            if (pe_p_1000 != null) {
                indicatorDTO.setPe_p_1000(pe_p_1000);
            }
        }

        if (indicatorDTO.getPe_p_1000() == null) {
            Double pe_p_1000 = StockCalculateUtils.getPeP1000Value(indicatorDTO, indicatorElement);
            if (pe_p_1000 != null) {
                indicatorDTO.setPe_p_1000(pe_p_1000);
            }
        }

        if (indicatorDTO.getPb_p_1000() == null) {
            Double pb_p_1000 = StockCalculateUtils.getPbP1000Value(indicatorDTO, indicatorElement);
            if (pb_p_1000 != null) {
                indicatorDTO.setPb_p_1000(pb_p_1000);
            }
        }

        if (indicatorDTO.getMa_1000_diff_p() == null) {
            Double ma_1000_diff_p = StockCalculateUtils.getMa1000DiffPercentValue(indicatorDTO, indicatorElement);
            if (ma_1000_diff_p != null) {
                indicatorDTO.setMa_1000_diff_p(ma_1000_diff_p);
            }
        }

        if (indicatorDTO.getAvg_roe_ttm() == null) {
            Double avg_roe_ttm = StockCalculateUtils.getAvgRoeTtm(indicatorDTO, indicatorElement);
            if (avg_roe_ttm != null) {
                indicatorDTO.setAvg_roe_ttm(avg_roe_ttm);
            }
        }
        if (indicatorDTO.getAvg_roe_ttm_v1() == null) {
            Double avg_roe_ttm_v1 = StockCalculateUtils.getAvgRoeTtmV1(indicatorDTO, indicatorElement);
            if (avg_roe_ttm_v1 != null) {
                indicatorDTO.setAvg_roe_ttm_v1(avg_roe_ttm_v1);
            }
        }
        if (indicatorDTO.getGross_margin_rate() == null) {
            Double operating_cost = indicatorDTO.getOperating_cost();
            Double revenue = indicatorDTO.getRevenue();
            if (operating_cost != null && revenue != null && revenue != 0) {
                indicatorDTO.setGross_margin_rate(1 - operating_cost / revenue);
            }
        }

        if (indicatorDTO.getCi_oi_rate() == null) {
            Double sub_total_of_ci_from_oa = indicatorDTO.getSub_total_of_ci_from_oa();
            Double revenue = indicatorDTO.getRevenue();
            if (sub_total_of_ci_from_oa != null && revenue != null && revenue != 0) {
                indicatorDTO.setCi_oi_rate(sub_total_of_ci_from_oa / revenue);
            }
        }

        if (indicatorDTO.getNcf_pri_rate() == null) {
            Double ncf_from_oa = indicatorDTO.getNcf_from_oa();
            Double net_profit_atsopc = indicatorDTO.getNet_profit_atsopc();
            if (ncf_from_oa != null && net_profit_atsopc != null && net_profit_atsopc != 0) {
                indicatorDTO.setNcf_pri_rate(ncf_from_oa / net_profit_atsopc);
            }
        }

        if (indicatorDTO.getAp_ar_rate() == null) {
            Double bp_and_ap = indicatorDTO.getBp_and_ap();
            Double ar_and_br = indicatorDTO.getAr_and_br();
            if (bp_and_ap != null && ar_and_br != null && ar_and_br != 0) {
                indicatorDTO.setAp_ar_rate(bp_and_ap / ar_and_br);
            }
        }

        if (indicatorDTO.getCur_q_gross_margin_rate() == null) {
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            if (curQuarterIncomeDTO != null && curQuarterIncomeDTO.getRevenue() != null && curQuarterIncomeDTO.getOperating_cost() != null) {
                double cur_q_gross_margin_rate = 1 - curQuarterIncomeDTO.getOperating_cost() / curQuarterIncomeDTO.getRevenue();
                indicatorDTO.setCur_q_gross_margin_rate(cur_q_gross_margin_rate);
            }
        }

        if (indicatorDTO.getCur_q_net_selling_rate() == null) {
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            if (curQuarterIncomeDTO != null) {
                Double revenue = curQuarterIncomeDTO.getRevenue();
                Double profit = curQuarterIncomeDTO.getContinous_operating_np() != null
                        ? curQuarterIncomeDTO.getContinous_operating_np() : curQuarterIncomeDTO.getNet_profit();
                if(revenue !=null && profit !=null){
                    double cur_q_net_selling_rate = profit / revenue;
                    indicatorDTO.setCur_q_net_selling_rate(cur_q_net_selling_rate);
                }
            }
        }

        if (indicatorDTO.getCur_q_gross_margin_rate_change() == null) {
            Double cur_q_gross_margin_rate = indicatorDTO.getCur_q_gross_margin_rate();
            if (cur_q_gross_margin_rate != null) {
                QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
                if (lastYearQuarterIncomeDTO != null && lastYearQuarterIncomeDTO.getRevenue() != null && lastYearQuarterIncomeDTO.getOperating_cost() != null) {
                    double last_year_cur_q_gross_margin_rate = 1 - lastYearQuarterIncomeDTO.getOperating_cost() / lastYearQuarterIncomeDTO.getRevenue();
                    indicatorDTO.setCur_q_gross_margin_rate_change(cur_q_gross_margin_rate - last_year_cur_q_gross_margin_rate);
                }
            }
        }

        if (indicatorDTO.getCur_q_net_selling_rate_change() == null) {
            Double cur_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate();
            if (cur_q_net_selling_rate != null) {
                QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
                if (lastYearQuarterIncomeDTO != null) {
                    Double lastYearQuarterRevenue = lastYearQuarterIncomeDTO.getRevenue();
                    Double lastYearQuarterProfit = lastYearQuarterIncomeDTO.getContinous_operating_np() != null
                            ? lastYearQuarterIncomeDTO.getContinous_operating_np() : lastYearQuarterIncomeDTO.getNet_profit();
                    if(lastYearQuarterRevenue !=null && lastYearQuarterProfit !=null){
                        double last_year_cur_q_net_selling_rate = lastYearQuarterProfit / lastYearQuarterRevenue;
                        indicatorDTO.setCur_q_net_selling_rate_change(cur_q_net_selling_rate - last_year_cur_q_net_selling_rate);
                    }
                }
            }
        }

        if (indicatorDTO.getCur_q_gross_margin_rate_q_chg() == null) {
            Double cur_q_gross_margin_rate = indicatorDTO.getCur_q_gross_margin_rate();
            if (cur_q_gross_margin_rate != null) {
                QuarterIncomeDTO lastPeriodQuarterIncomeDTO = indicatorElement.getLastPeriodQuarterIncomeDTO();
                if (lastPeriodQuarterIncomeDTO != null && lastPeriodQuarterIncomeDTO.getRevenue() != null && lastPeriodQuarterIncomeDTO.getOperating_cost() != null) {
                    double last_period_cur_q_gross_margin_rate = 1 - lastPeriodQuarterIncomeDTO.getOperating_cost() / lastPeriodQuarterIncomeDTO.getRevenue();
                    indicatorDTO.setCur_q_gross_margin_rate_q_chg(cur_q_gross_margin_rate - last_period_cur_q_gross_margin_rate);
                }
            }
        }

        if (indicatorDTO.getCur_q_net_selling_rate_q_chg() == null) {
            Double cur_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate();
            if (cur_q_net_selling_rate != null) {
                QuarterIncomeDTO lastPeriodQuarterIncomeDTO = indicatorElement.getLastPeriodQuarterIncomeDTO();
                if (lastPeriodQuarterIncomeDTO != null ) {
                    Double lastPeriodQuarterRevenue = lastPeriodQuarterIncomeDTO.getRevenue();
                    Double lastPeriodQuarterProfit = lastPeriodQuarterIncomeDTO.getContinous_operating_np() != null
                            ? lastPeriodQuarterIncomeDTO.getContinous_operating_np() : lastPeriodQuarterIncomeDTO.getNet_profit();
                    if(lastPeriodQuarterRevenue !=null && lastPeriodQuarterProfit !=null){
                        double last_period_cur_q_net_selling_rate = lastPeriodQuarterProfit / lastPeriodQuarterRevenue;
                        indicatorDTO.setCur_q_net_selling_rate_q_chg(cur_q_net_selling_rate - last_period_cur_q_net_selling_rate);
                    }
                }
            }
        }

        if (indicatorDTO.getCur_q_operating_income_yoy() == null) {
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
            if (curQuarterIncomeDTO != null && curQuarterIncomeDTO.getRevenue() != null
                    && lastYearQuarterIncomeDTO != null && lastYearQuarterIncomeDTO.getRevenue() != null) {
                indicatorDTO.setCur_q_operating_income_yoy(curQuarterIncomeDTO.getRevenue() / lastYearQuarterIncomeDTO.getRevenue() - 1);
            }
        }

        if (indicatorDTO.getCur_q_net_profit_atsopc_yoy() == null) {
            QuarterIncomeDTO curQuarterIncomeDTO = indicatorElement.getCurQuarterIncomeDTO();
            QuarterIncomeDTO lastYearQuarterIncomeDTO = indicatorElement.getLastYearQuarterIncomeDTO();
            if (curQuarterIncomeDTO != null && lastYearQuarterIncomeDTO != null) {
                Double curQuarterProfit = curQuarterIncomeDTO.getContinous_operating_np() != null
                        ? curQuarterIncomeDTO.getContinous_operating_np() : curQuarterIncomeDTO.getNet_profit();
                Double lastYearQuarterProfit = lastYearQuarterIncomeDTO.getContinous_operating_np() != null
                        ? lastYearQuarterIncomeDTO.getContinous_operating_np() : lastYearQuarterIncomeDTO.getNet_profit();
                double cur_q_net_profit_atsopc_yoy = (curQuarterProfit - lastYearQuarterProfit) / Math.abs(lastYearQuarterProfit);
                indicatorDTO.setCur_q_net_profit_atsopc_yoy(cur_q_net_profit_atsopc_yoy);
            }
        }

        if (indicatorDTO.getLast_q_operating_income_yoy() == null) {
            QuarterIncomeDTO lastQuarterIncomeDTO =indicatorElement.getLastPeriodQuarterIncomeDTO();
            QuarterIncomeDTO lastYearAndLastQuarterIncomeDTO = indicatorElement.getLastYearAndLastQuarterIncomeDTO();

            if(lastQuarterIncomeDTO !=null && lastQuarterIncomeDTO.getRevenue() !=null
                    && lastYearAndLastQuarterIncomeDTO !=null && lastYearAndLastQuarterIncomeDTO.getRevenue() !=null){
                double last_q_operating_income_yoy = lastQuarterIncomeDTO.getRevenue() / lastYearAndLastQuarterIncomeDTO.getRevenue() - 1;
                indicatorDTO.setLast_q_operating_income_yoy(last_q_operating_income_yoy);
            }
        }

        if (indicatorDTO.getLast_q_net_profit_atsopc_yoy() == null) {
            QuarterIncomeDTO lastQuarterIncomeDTO =indicatorElement.getLastPeriodQuarterIncomeDTO();
            QuarterIncomeDTO lastYearAndLastQuarterIncomeDTO = indicatorElement.getLastYearAndLastQuarterIncomeDTO();

            if (lastQuarterIncomeDTO != null && lastYearAndLastQuarterIncomeDTO != null) {
                Double lastQuarterProfit = lastQuarterIncomeDTO.getContinous_operating_np() != null
                        ? lastQuarterIncomeDTO.getContinous_operating_np() : lastQuarterIncomeDTO.getNet_profit();
                Double lastYearAndLastQuarterProfit = lastYearAndLastQuarterIncomeDTO.getContinous_operating_np() != null
                        ? lastYearAndLastQuarterIncomeDTO.getContinous_operating_np() : lastYearAndLastQuarterIncomeDTO.getNet_profit();
                if(lastQuarterProfit !=null && lastYearAndLastQuarterProfit !=null){
                    double last_q_net_profit_atsopc_yoy = (lastQuarterProfit - lastYearAndLastQuarterProfit) / Math.abs(lastYearAndLastQuarterProfit + 0.01);
                    indicatorDTO.setLast_q_net_profit_atsopc_yoy(last_q_net_profit_atsopc_yoy);
                }
            }
        }

        if (indicatorDTO.getFixed_asset_sum_inc() == null) {
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = indicatorElement.getLastSamePeriodBalanceDTO();
            if (curBalanceDTO != null && lastSamePeriodBalanceDTO != null && curBalanceDTO.getFixed_asset_sum() != null) {
                if (lastSamePeriodBalanceDTO.getFixed_asset_sum() != null && lastSamePeriodBalanceDTO.getFixed_asset_sum() != 0) {
                    indicatorDTO.setFixed_asset_sum_inc(curBalanceDTO.getFixed_asset_sum() / lastSamePeriodBalanceDTO.getFixed_asset_sum() - 1);
                }
            }
        }

        if (indicatorDTO.getConstruction_in_process_sum_inc() == null) {
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = indicatorElement.getLastSamePeriodBalanceDTO();
            if (curBalanceDTO != null && lastSamePeriodBalanceDTO != null && curBalanceDTO.getConstruction_in_process_sum() != null) {
                if (lastSamePeriodBalanceDTO.getConstruction_in_process_sum() != null && lastSamePeriodBalanceDTO.getConstruction_in_process_sum() != 0) {
                    indicatorDTO.setConstruction_in_process_sum_inc(curBalanceDTO.getConstruction_in_process_sum() / lastSamePeriodBalanceDTO.getConstruction_in_process_sum() - 1);
                }
            }
        }

        if (indicatorDTO.getGw_ia_assert_rate() == null) {
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if (curBalanceDTO != null) {
                Double goodwill = curBalanceDTO.getGoodwill() != null ? curBalanceDTO.getGoodwill() : 0;
                Double intangible_assets = curBalanceDTO.getIntangible_assets() != null ? curBalanceDTO.getIntangible_assets() : 0;
                Double total_holders_equity = curBalanceDTO.getTotal_holders_equity() != null ? curBalanceDTO.getTotal_holders_equity() : 0.01;
                indicatorDTO.setGw_ia_assert_rate((goodwill + intangible_assets) / total_holders_equity);
            }
        }

        if (indicatorDTO.getFixed_assert_rate() == null) {
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if (curBalanceDTO != null) {
                Double fixed_asset_sum = curBalanceDTO.getFixed_asset_sum() != null ? curBalanceDTO.getFixed_asset_sum() : 0;
                Double total_holders_equity = curBalanceDTO.getTotal_holders_equity() != null ? curBalanceDTO.getTotal_holders_equity() : 0.01;
                indicatorDTO.setFixed_assert_rate(fixed_asset_sum / total_holders_equity);
            }
        }

        if (indicatorDTO.getConstruction_assert_rate() == null) {
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if (curBalanceDTO != null) {
                Double construction_in_process_sum = curBalanceDTO.getConstruction_in_process_sum() != null ? curBalanceDTO.getConstruction_in_process_sum() : 0;
                Double total_holders_equity = curBalanceDTO.getTotal_holders_equity() != null ? curBalanceDTO.getTotal_holders_equity() : 0.01;
                indicatorDTO.setConstruction_assert_rate(construction_in_process_sum / total_holders_equity);
            }
        }

        if (indicatorDTO.getCash_sl_rate() == null) {
            XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
            if (curBalanceDTO != null) {
                double currency_funds = curBalanceDTO.getCurrency_funds() != null ? curBalanceDTO.getCurrency_funds() : 0;
                double tradable_fnncl_assets = curBalanceDTO.getTradable_fnncl_assets() != null ? curBalanceDTO.getTradable_fnncl_assets() : 0;
                double st_loan = curBalanceDTO.getSt_loan() != null ? curBalanceDTO.getSt_loan() : 0.01;
                double tradable_fnncl_liab = curBalanceDTO.getTradable_fnncl_liab() != null ? curBalanceDTO.getTradable_fnncl_liab() : 0;
                indicatorDTO.setCash_sl_rate((currency_funds + tradable_fnncl_assets) / (st_loan + tradable_fnncl_liab));
            }
        }

        if(indicatorDTO.getReceivable_turnover_days_rate() == null){
            XueQiuStockIndicatorDTO curIndicatorDTO = indicatorElement.getCurIndicatorDTO();
            XueQiuStockIndicatorDTO lastPeriodIndicatorDTO = indicatorElement.getLastPeriodIndicatorDTO();
            if(curIndicatorDTO !=null && lastPeriodIndicatorDTO !=null){
                Double receivable_turnover_days = curIndicatorDTO.getReceivable_turnover_days();
                Double last_receivable_turnover_days = lastPeriodIndicatorDTO.getReceivable_turnover_days();
                if(receivable_turnover_days !=null && last_receivable_turnover_days !=null){
                    double rate = receivable_turnover_days / (last_receivable_turnover_days + 0.01) - 1;
                    indicatorDTO.setReceivable_turnover_days_rate(rate);
                }
            }
        }

        if(indicatorDTO.getInventory_turnover_days_rate() == null){
            XueQiuStockIndicatorDTO curIndicatorDTO = indicatorElement.getCurIndicatorDTO();
            XueQiuStockIndicatorDTO lastPeriodIndicatorDTO = indicatorElement.getLastPeriodIndicatorDTO();
            if(curIndicatorDTO !=null && lastPeriodIndicatorDTO !=null){
                Double inventory_turnover_days = curIndicatorDTO.getInventory_turnover_days();
                Double last_inventory_turnover_days = lastPeriodIndicatorDTO.getInventory_turnover_days();
                if(inventory_turnover_days !=null && last_inventory_turnover_days !=null){
                    double rate = inventory_turnover_days / (last_inventory_turnover_days + 0.01) - 1;
                    indicatorDTO.setInventory_turnover_days_rate(rate);
                }
            }
        }

        // 查询日期当天的k线
        XueQiuStockKLineDTO queryDateKLineDTO = Optional.ofNullable(indicatorElement.getKLineDTOList()).orElse(Lists.newArrayList()).stream()
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .findFirst()
                .orElse(null);
        if (queryDateKLineDTO != null && queryDateKLineDTO.getClose() != null && queryDateKLineDTO.getClose() != 0) {
            // k线日期近1周的价格变化
            XueQiuStockKLineDTO oneWeekBeforeKLineDTO = indicatorElement.getOneWeekBeforeKLineDTO();
            if (oneWeekBeforeKLineDTO != null && oneWeekBeforeKLineDTO.getClose() != null) {
                indicatorDTO.setOne_week_before_price_change(queryDateKLineDTO.getClose() / oneWeekBeforeKLineDTO.getClose() - 1);
            }

            // k线日期近1月的价格变化
            XueQiuStockKLineDTO oneMonthBeforeKLineDTO = indicatorElement.getOneMonthBeforeKLineDTO();
            if (oneMonthBeforeKLineDTO != null && oneMonthBeforeKLineDTO.getClose() != null) {
                indicatorDTO.setOne_month_before_price_change(queryDateKLineDTO.getClose() / oneMonthBeforeKLineDTO.getClose()  - 1);
            }

            // k线日期近3月的价格变化
            XueQiuStockKLineDTO threeMonthBeforeKLineDTO = indicatorElement.getThreeMonthBeforeKLineDTO();
            if (threeMonthBeforeKLineDTO != null && threeMonthBeforeKLineDTO.getClose() != null) {
                indicatorDTO.setThree_month_before_price_change(queryDateKLineDTO.getClose() / threeMonthBeforeKLineDTO.getClose() - 1);
            }

            // 1个月后的价格变化
            XueQiuStockKLineDTO oneMonthKLineDTO = indicatorElement.getOneMonthLaterKLineDTO();
            if (oneMonthKLineDTO != null && oneMonthKLineDTO.getClose() != null) {
                indicatorDTO.setOne_month_price_change(oneMonthKLineDTO.getClose() / queryDateKLineDTO.getClose() - 1);
            }

            // 3个月后的价格变化
            XueQiuStockKLineDTO threeMonthKLineDTO = indicatorElement.getThreeMonthLaterKLineDTO();
            if (threeMonthKLineDTO != null && threeMonthKLineDTO.getClose() != null) {
                indicatorDTO.setThree_month_price_change(threeMonthKLineDTO.getClose() / queryDateKLineDTO.getClose() - 1);
            }

            // 半年后的价格变化
            XueQiuStockKLineDTO halfYearKLineDTO = indicatorElement.getHalfYearLaterKLineDTO();
            if (halfYearKLineDTO != null && halfYearKLineDTO.getClose() != null) {
                indicatorDTO.setHalf_year_price_change(halfYearKLineDTO.getClose() / queryDateKLineDTO.getClose() - 1);
            }

            // 1年后的价格变化
            XueQiuStockKLineDTO oneYearKLineDTO = indicatorElement.getOneYearLaterKLineDTO();
            if (oneYearKLineDTO != null && oneYearKLineDTO.getClose() != null) {
                indicatorDTO.setOne_year_price_change(oneYearKLineDTO.getClose() / queryDateKLineDTO.getClose() - 1);
            }
        }

        // 过去5个季度的毛利率和净利率
        if (StringUtils.isBlank(indicatorDTO.getGross_net_rate_5_quarter())) {
            List<QuarterIncomeDTO> quarterIncomeDTOList = indicatorElement.getQuarterIncomeDTOList();
            if (CollectionUtils.isNotEmpty(quarterIncomeDTOList)) {
                List<QuarterIncomeDTO> sortedQuarterDTOList = quarterIncomeDTOList.stream()
                        .sorted(Comparator.comparing(QuarterIncomeDTO::getReport_year)
                                .thenComparing(QuarterIncomeDTO::getReport_type).reversed())
                        .collect(Collectors.toList());

                StringBuilder builder = new StringBuilder();
                for (QuarterIncomeDTO quarterIncomeDTO : sortedQuarterDTOList) {
                    if(quarterIncomeDTO.getTotal_revenue() !=null){
                        if (quarterIncomeDTO.getOperating_cost() != null) {
                            double gross_rate = 1 - quarterIncomeDTO.getOperating_cost() / (quarterIncomeDTO.getTotal_revenue() + 0.01);
                            String str_gross_rate = NumberUtil.format(gross_rate * 100, 1) + "%";
                            builder.append(str_gross_rate);
                        }
                        builder.append("/");

                        Double profit = quarterIncomeDTO.getContinous_operating_np() != null
                                ? quarterIncomeDTO.getContinous_operating_np() : quarterIncomeDTO.getNet_profit();
                        if(profit !=null){
                            double net_profit_rate = profit / (quarterIncomeDTO.getTotal_revenue() + 0.01);
                            String str_net_profit_rate = NumberUtil.format(net_profit_rate * 100, 1) + "%";
                            builder.append(str_net_profit_rate);
                        }
                    }
                    builder.append("; ");
                }
                indicatorDTO.setGross_net_rate_5_quarter(builder.toString());
            }
        }

        // 过去5个季度的营收和净利润
        if (StringUtils.isBlank(indicatorDTO.getOi_net_5_quarter())) {
            List<QuarterIncomeDTO> quarterIncomeDTOList = indicatorElement.getQuarterIncomeDTOList();
            if (CollectionUtils.isNotEmpty(quarterIncomeDTOList)) {
                List<QuarterIncomeDTO> sortedQuarterDTOList = quarterIncomeDTOList.stream()
                        .sorted(Comparator.comparing(QuarterIncomeDTO::getReport_year)
                                .thenComparing(QuarterIncomeDTO::getReport_type).reversed())
                        .collect(Collectors.toList());

                StringBuilder builder = new StringBuilder();
                for (QuarterIncomeDTO quarterIncomeDTO : sortedQuarterDTOList) {
                    if (quarterIncomeDTO.getTotal_revenue() != null) {
                        double total_revenue = quarterIncomeDTO.getTotal_revenue() / (1.0 * 10000 * 10000);
                        String str_total_revenue = NumberUtil.format(total_revenue, 1) + "";
                        builder.append(str_total_revenue);
                    }
                    builder.append("/");

                    Double profit = quarterIncomeDTO.getContinous_operating_np() != null
                            ? quarterIncomeDTO.getContinous_operating_np() : quarterIncomeDTO.getNet_profit();
                    if (profit !=null) {
                        double net_profit = profit / (1.0 * 10000 * 10000);
                        String str_net_profit = NumberUtil.format(net_profit, 1) + "";
                        builder.append(str_net_profit);
                    }
                    builder.append("; ");
                }
                indicatorDTO.setOi_net_5_quarter(builder.toString());
            }
        }

        // k线数量
        if (indicatorDTO.getKLineSize() == null) {
            int klineSize = CollectionUtils.isNotEmpty(indicatorElement.getKLineDTOList()) ? indicatorElement.getKLineDTOList().size() : 0;
            indicatorDTO.setKLineSize(klineSize);
        }

        if (indicatorDTO.getCurMatchNum() == null) {
            int curMatchNum = 0;

            // ROE指标
            if ((indicatorDTO.getAvg_roe_ttm() != null && indicatorDTO.getAvg_roe_ttm() >= 0.12)
                    || (indicatorDTO.getAvg_roe_ttm_v1() != null && indicatorDTO.getAvg_roe_ttm_v1() >= 0.16)) {
                curMatchNum++;
            }

            // 毛利率和净利率指标
            if (indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.25
                    && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.15) {
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
    public static Integer getMa20EqualMa60Value(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null || indicatorElement == null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())) {
            return null;
        }

        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime klineDateTime = LocalDate.parse(indicatorDTO.getKLineDate(), df).atStartOfDay();
        long startTimestamp = klineDateTime.plusMonths(-3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        List<XueQiuStockKLineDTO> matchedKLineDTOList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null && kLineDTO.getTimestamp() >= startTimestamp)
                .filter(kLineDTO -> {
                    if (kLineDTO.getClose() == null || kLineDTO.getMa_20_value() == null
                            || kLineDTO.getMa_60_value() == null || kLineDTO.getMa_250_value() == null) {
                        return false;
                    }

                    if (kLineDTO.getClose() > (kLineDTO.getMa_250_value() * 1.1)) {
                        return false;
                    }

                    double lowValue = kLineDTO.getClose() * 0.97;
                    double highValue = kLineDTO.getClose() * 1.03;
                    if (lowValue <= kLineDTO.getMa_20_value() && kLineDTO.getMa_20_value() < highValue
                            && lowValue <= kLineDTO.getMa_60_value() && kLineDTO.getMa_60_value() < highValue) {
                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());

        return matchedKLineDTOList.size();
    }

    /**
     * pe分位值(近1000个交易日)
     *
     * @param indicatorDTO
     * @param indicatorElement
     * @return
     */
    public static Double getPeP1000Value(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null || indicatorDTO.getPe() == null) {
            return null;
        }
        if (indicatorElement == null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())) {
            return null;
        }

        Double currentPe = indicatorDTO.getPe();

        List<Double> peList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getPe() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getPe))
                .map(XueQiuStockKLineDTO::getPe)
                .collect(Collectors.toList());

        for (int index = 0; index < peList.size(); index++) {
            if (Math.abs(currentPe - peList.get(index)) <= 0.01) {
                return (index * 1.0) / peList.size();
            }
        }

        return null;
    }

    /**
     * pb分位值(近1000个交易日)
     *
     * @param indicatorDTO
     * @param indicatorElement
     * @return
     */
    public static Double getPbP1000Value(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null || indicatorDTO.getPb() == null) {
            return null;
        }
        if (indicatorElement == null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())) {
            return null;
        }

        Double currentPb = indicatorDTO.getPb();

        List<Double> pbList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getPb() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getPb))
                .map(XueQiuStockKLineDTO::getPb)
                .collect(Collectors.toList());

        for (int index = 0; index < pbList.size(); index++) {
            if (Math.abs(currentPb - pbList.get(index)) <= 0.01) {
                return (index * 1.0) / pbList.size();
            }
        }

        return null;
    }

    /**
     * K线和ma1000差值的百分位
     *
     * @param indicatorDTO
     * @param indicatorElement
     * @return
     */
    public static Double getMa1000DiffPercentValue(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null || indicatorDTO.getClose() == null || indicatorDTO.getMa_1000_value() == null) {
            return null;
        }
        if (indicatorElement == null || CollectionUtils.isEmpty(indicatorElement.getKLineDTOList())) {
            return null;
        }

        List<Double> diffValueList = indicatorElement.getKLineDTOList().stream()
                .filter(kLineDTO -> kLineDTO.getMa_1000_value() != null && kLineDTO.getClose() != null)
                .map(kLineDTO -> kLineDTO.getClose() - kLineDTO.getMa_1000_value())
                .sorted()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(diffValueList) || diffValueList.size() < 500) {
            return null;
        }

        Double current_diff_value = indicatorDTO.getClose() - indicatorDTO.getMa_1000_value();
        for (int index = 0; index < diffValueList.size(); index++) {
            if (Math.abs(current_diff_value - diffValueList.get(index)) <= 0.01) {
                return (index * 1.0) / diffValueList.size();
            }
        }

        return null;
    }

    /**
     * 净资产收益率TTM
     *
     * @param indicatorDTO
     * @param indicatorElement
     * @return
     */
    public static Double getAvgRoeTtm(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null || StringUtils.isBlank(indicatorDTO.getCode()) || indicatorDTO.getTotal_holders_equity() == null) {
            return null;
        }

        Integer year = indicatorElement.getReportYear();
        String reportType = indicatorElement.getReportType();

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList = Lists.newArrayList();
        if (Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_1.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2));
        } else if (Objects.equals(reportType, FinanceReportTypeEnum.HALF_YEAR.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3));
        } else if (Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_3.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4));
        } else if (Objects.equals(reportType, FinanceReportTypeEnum.ALL_YEAR.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList = Lists.newArrayList();
        for (QuarterIncomeDTO quarterIncomeDTO : indicatorElement.getQuarterIncomeDTOList()) {
            for (ImmutablePair<Integer, FinanceReportTypeEnum> pair : immutablePairList) {
                Integer tmpYear = pair.getLeft();
                FinanceReportTypeEnum reportTypeEnum = pair.getRight();

                if (Objects.equals(quarterIncomeDTO.getReport_year(), tmpYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), reportTypeEnum.getCode())) {
                    quarterIncomeDTOList.add(quarterIncomeDTO);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(quarterIncomeDTOList) && quarterIncomeDTOList.size() == 4) {
            double sum = quarterIncomeDTOList.stream()
                    .mapToDouble(incomeDTO -> incomeDTO.getNet_profit() != null ? incomeDTO.getNet_profit() : 0)
                    .sum();
            return sum / indicatorDTO.getTotal_holders_equity();
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
    public static Double getAvgRoeTtmV1(AnalyseIndicatorDTO indicatorDTO, AnalyseIndicatorElement indicatorElement) {
        if (indicatorDTO == null || StringUtils.isBlank(indicatorDTO.getCode()) || indicatorDTO.getTotal_holders_equity() == null) {
            return null;
        }

        Integer year = indicatorElement.getReportYear();
        String reportType = indicatorElement.getReportType();

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList = Lists.newArrayList();
        if (Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_1.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2));
        } else if (Objects.equals(reportType, FinanceReportTypeEnum.HALF_YEAR.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3));
        } else if (Objects.equals(reportType, FinanceReportTypeEnum.QUARTER_3.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4));
        } else if (Objects.equals(reportType, FinanceReportTypeEnum.ALL_YEAR.getCode())) {
            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList = Lists.newArrayList();
        for (QuarterIncomeDTO quarterIncomeDTO : indicatorElement.getQuarterIncomeDTOList()) {
            for (ImmutablePair<Integer, FinanceReportTypeEnum> pair : immutablePairList) {
                Integer tmpYear = pair.getLeft();
                FinanceReportTypeEnum reportTypeEnum = pair.getRight();

                if (Objects.equals(quarterIncomeDTO.getReport_year(), tmpYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), reportTypeEnum.getCode())) {
                    quarterIncomeDTOList.add(quarterIncomeDTO);
                }
            }
        }
        if (CollectionUtils.isEmpty(quarterIncomeDTOList) || quarterIncomeDTOList.size() != 4) {
            return null;
        }

        // 去掉现金等价物后的净资产
        XueQiuStockBalanceDTO curBalanceDTO = indicatorElement.getCurBalanceDTO();
        if (curBalanceDTO == null) {
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
                .mapToDouble(incomeDTO -> incomeDTO.getNet_profit() != null ? incomeDTO.getNet_profit() : 0)
                .sum();

        return sum / total_holders_equity_v1;
    }

    /**
     * 分析指标数据格式化
     *
     * @param analyseIndicatorDTO
     */
    public static void formatAnalyseIndicatorDTO(AnalyseIndicatorDTO analyseIndicatorDTO) {
        if (analyseIndicatorDTO == null) {
            return;
        }

        if (analyseIndicatorDTO.getOne_week_before_price_change() != null) {
            analyseIndicatorDTO.setOne_week_before_price_change(NumberUtil.format(analyseIndicatorDTO.getOne_week_before_price_change(), 3));
        }

        if (analyseIndicatorDTO.getOne_month_before_price_change() != null) {
            analyseIndicatorDTO.setOne_month_before_price_change(NumberUtil.format(analyseIndicatorDTO.getOne_month_before_price_change(), 3));
        }

        if (analyseIndicatorDTO.getThree_month_before_price_change() != null) {
            analyseIndicatorDTO.setThree_month_before_price_change(NumberUtil.format(analyseIndicatorDTO.getThree_month_before_price_change(), 3));
        }

        if (analyseIndicatorDTO.getOne_month_price_change() != null) {
            analyseIndicatorDTO.setOne_month_price_change(NumberUtil.format(analyseIndicatorDTO.getOne_month_price_change(), 3));
        }

        if (analyseIndicatorDTO.getThree_month_price_change() != null) {
            analyseIndicatorDTO.setThree_month_price_change(NumberUtil.format(analyseIndicatorDTO.getThree_month_price_change(), 3));
        }

        if (analyseIndicatorDTO.getHalf_year_price_change() != null) {
            analyseIndicatorDTO.setHalf_year_price_change(NumberUtil.format(analyseIndicatorDTO.getHalf_year_price_change(), 3));
        }

        if (analyseIndicatorDTO.getOne_year_price_change() != null) {
            analyseIndicatorDTO.setOne_year_price_change(NumberUtil.format(analyseIndicatorDTO.getOne_year_price_change(), 3));
        }

        if (analyseIndicatorDTO.getAsset_liab_ratio() != null) {
            analyseIndicatorDTO.setAsset_liab_ratio(NumberUtil.format(analyseIndicatorDTO.getAsset_liab_ratio() / 100, 3));
        }
        if (analyseIndicatorDTO.getPe() != null) {
            analyseIndicatorDTO.setPe(NumberUtil.format(analyseIndicatorDTO.getPe(), 1));
        }
        if (analyseIndicatorDTO.getPe_p_1000() != null) {
            analyseIndicatorDTO.setPe_p_1000(NumberUtil.format(analyseIndicatorDTO.getPe_p_1000(), 3));
        }
        if (analyseIndicatorDTO.getPb() != null) {
            analyseIndicatorDTO.setPb(NumberUtil.format(analyseIndicatorDTO.getPb(), 1));
        }
        if (analyseIndicatorDTO.getPb_p_1000() != null) {
            analyseIndicatorDTO.setPb_p_1000(NumberUtil.format(analyseIndicatorDTO.getPb_p_1000(), 3));
        }
        if (analyseIndicatorDTO.getTotal_holders_equity() != null) {
            analyseIndicatorDTO.setTotal_holders_equity(NumberUtil.format(analyseIndicatorDTO.getTotal_holders_equity() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getMa_1000_diff_p() != null) {
            analyseIndicatorDTO.setMa_1000_diff_p(NumberUtil.format(analyseIndicatorDTO.getMa_1000_diff_p(), 3));
        }
        if (analyseIndicatorDTO.getAvg_roe_ttm() != null) {
            analyseIndicatorDTO.setAvg_roe_ttm(NumberUtil.format(analyseIndicatorDTO.getAvg_roe_ttm(), 3));
        }
        if (analyseIndicatorDTO.getAvg_roe_ttm_v1() != null) {
            analyseIndicatorDTO.setAvg_roe_ttm_v1(NumberUtil.format(analyseIndicatorDTO.getAvg_roe_ttm_v1(), 3));
        }
        if (analyseIndicatorDTO.getRevenue() != null) {
            analyseIndicatorDTO.setRevenue(NumberUtil.format(analyseIndicatorDTO.getRevenue() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getOperating_cost() != null) {
            analyseIndicatorDTO.setOperating_cost(NumberUtil.format(analyseIndicatorDTO.getOperating_cost() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getGross_margin_rate() != null) {
            analyseIndicatorDTO.setGross_margin_rate(NumberUtil.format(analyseIndicatorDTO.getGross_margin_rate(), 3));
        }
        if (analyseIndicatorDTO.getNet_selling_rate() != null) {
            analyseIndicatorDTO.setNet_selling_rate(NumberUtil.format(analyseIndicatorDTO.getNet_selling_rate() / 100, 3));
        }
        if (analyseIndicatorDTO.getCur_q_gross_margin_rate() != null) {
            analyseIndicatorDTO.setCur_q_gross_margin_rate(NumberUtil.format(analyseIndicatorDTO.getCur_q_gross_margin_rate(), 3));
        }
        if (analyseIndicatorDTO.getCur_q_net_selling_rate() != null) {
            analyseIndicatorDTO.setCur_q_net_selling_rate(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_selling_rate(), 3));
        }
        if(analyseIndicatorDTO.getLast_q_operating_income_yoy() !=null){
            analyseIndicatorDTO.setLast_q_operating_income_yoy(NumberUtil.format(analyseIndicatorDTO.getLast_q_operating_income_yoy(), 3));
        }
        if(analyseIndicatorDTO.getLast_q_net_profit_atsopc_yoy() !=null){
            analyseIndicatorDTO.setLast_q_net_profit_atsopc_yoy(NumberUtil.format(analyseIndicatorDTO.getLast_q_net_profit_atsopc_yoy(), 3));
        }
        if (analyseIndicatorDTO.getCur_q_gross_margin_rate_change() != null) {
            analyseIndicatorDTO.setCur_q_gross_margin_rate_change(NumberUtil.format(analyseIndicatorDTO.getCur_q_gross_margin_rate_change(), 3));
        }
        if (analyseIndicatorDTO.getCur_q_net_selling_rate_change() != null) {
            analyseIndicatorDTO.setCur_q_net_selling_rate_change(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_selling_rate_change(), 3));
        }
        if (analyseIndicatorDTO.getCur_q_gross_margin_rate_q_chg() != null) {
            analyseIndicatorDTO.setCur_q_gross_margin_rate_q_chg(NumberUtil.format(analyseIndicatorDTO.getCur_q_gross_margin_rate_q_chg(), 3));
        }
        if (analyseIndicatorDTO.getCur_q_net_selling_rate_q_chg() != null) {
            analyseIndicatorDTO.setCur_q_net_selling_rate_q_chg(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_selling_rate_q_chg(), 3));
        }
        if (analyseIndicatorDTO.getOperating_income_yoy() != null) {
            analyseIndicatorDTO.setOperating_income_yoy(NumberUtil.format(analyseIndicatorDTO.getOperating_income_yoy() / 100, 3));
        }
        if (analyseIndicatorDTO.getNet_profit_atsopc_yoy() != null) {
            analyseIndicatorDTO.setNet_profit_atsopc_yoy(NumberUtil.format(analyseIndicatorDTO.getNet_profit_atsopc_yoy() / 100, 3));
        }
        if (analyseIndicatorDTO.getCur_q_operating_income_yoy() != null) {
            analyseIndicatorDTO.setCur_q_operating_income_yoy(NumberUtil.format(analyseIndicatorDTO.getCur_q_operating_income_yoy(), 3));
        }
        if (analyseIndicatorDTO.getCur_q_net_profit_atsopc_yoy() != null) {
            analyseIndicatorDTO.setCur_q_net_profit_atsopc_yoy(NumberUtil.format(analyseIndicatorDTO.getCur_q_net_profit_atsopc_yoy(), 3));
        }
        if (analyseIndicatorDTO.getFixed_asset_sum_inc() != null) {
            analyseIndicatorDTO.setFixed_asset_sum_inc(NumberUtil.format(analyseIndicatorDTO.getFixed_asset_sum_inc(), 3));
        }
        if (analyseIndicatorDTO.getConstruction_in_process_sum_inc() != null) {
            analyseIndicatorDTO.setConstruction_in_process_sum_inc(NumberUtil.format(analyseIndicatorDTO.getConstruction_in_process_sum_inc(), 3));
        }
        if (analyseIndicatorDTO.getGw_ia_assert_rate() != null) {
            analyseIndicatorDTO.setGw_ia_assert_rate(NumberUtil.format(analyseIndicatorDTO.getGw_ia_assert_rate(), 3));
        }
        if (analyseIndicatorDTO.getFixed_assert_rate() != null) {
            analyseIndicatorDTO.setFixed_assert_rate(NumberUtil.format(analyseIndicatorDTO.getFixed_assert_rate(), 3));
        }
        if (analyseIndicatorDTO.getConstruction_assert_rate() != null) {
            analyseIndicatorDTO.setConstruction_assert_rate(NumberUtil.format(analyseIndicatorDTO.getConstruction_assert_rate(), 3));
        }
        if (analyseIndicatorDTO.getCash_sl_rate() != null) {
            double cash_sl_rate = analyseIndicatorDTO.getCash_sl_rate() < 10 ? analyseIndicatorDTO.getCash_sl_rate() : 10;
            analyseIndicatorDTO.setCash_sl_rate(NumberUtil.format(cash_sl_rate, 1));
        }
        if (analyseIndicatorDTO.getMarket_capital() != null) {
            analyseIndicatorDTO.setMarket_capital(NumberUtil.format(analyseIndicatorDTO.getMarket_capital() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getSub_total_of_ci_from_oa() != null) {
            analyseIndicatorDTO.setSub_total_of_ci_from_oa(NumberUtil.format(analyseIndicatorDTO.getSub_total_of_ci_from_oa() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getCi_oi_rate() != null) {
            analyseIndicatorDTO.setCi_oi_rate(NumberUtil.format(analyseIndicatorDTO.getCi_oi_rate(), 1));
        }
        if (analyseIndicatorDTO.getNcf_from_oa() != null) {
            analyseIndicatorDTO.setNcf_from_oa(NumberUtil.format(analyseIndicatorDTO.getNcf_from_oa() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getNet_profit_atsopc() != null) {
            analyseIndicatorDTO.setNet_profit_atsopc(NumberUtil.format(analyseIndicatorDTO.getNet_profit_atsopc() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getNcf_pri_rate() != null) {
            analyseIndicatorDTO.setNcf_pri_rate(NumberUtil.format(analyseIndicatorDTO.getNcf_pri_rate(), 1));
        }
        if (analyseIndicatorDTO.getBp_and_ap() != null) {
            analyseIndicatorDTO.setBp_and_ap(NumberUtil.format(analyseIndicatorDTO.getBp_and_ap() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getAr_and_br() != null) {
            analyseIndicatorDTO.setAr_and_br(NumberUtil.format(analyseIndicatorDTO.getAr_and_br() / (10000 * 10000), 1));
        }
        if (analyseIndicatorDTO.getAp_ar_rate() != null) {
            analyseIndicatorDTO.setAp_ar_rate(NumberUtil.format(analyseIndicatorDTO.getAp_ar_rate(), 1));
        }
        if (analyseIndicatorDTO.getReceivable_turnover_days() != null) {
            analyseIndicatorDTO.setReceivable_turnover_days(NumberUtil.format(analyseIndicatorDTO.getReceivable_turnover_days(), 1));
        }
        if (analyseIndicatorDTO.getInventory_turnover_days() != null) {
            analyseIndicatorDTO.setInventory_turnover_days(NumberUtil.format(analyseIndicatorDTO.getInventory_turnover_days(), 1));
        }
        if (analyseIndicatorDTO.getReceivable_turnover_days_rate() != null) {
            analyseIndicatorDTO.setReceivable_turnover_days_rate(NumberUtil.format(analyseIndicatorDTO.getReceivable_turnover_days_rate(), 3));
        }
        if (analyseIndicatorDTO.getInventory_turnover_days_rate() != null) {
            analyseIndicatorDTO.setInventory_turnover_days_rate(NumberUtil.format(analyseIndicatorDTO.getInventory_turnover_days_rate(), 3));
        }
        if(StringUtils.isNotBlank(analyseIndicatorDTO.getNotice_date()) && analyseIndicatorDTO.getNotice_date().length() >=10){
            String noticeDate = StringUtils.substring(analyseIndicatorDTO.getNotice_date(), 0, 10);
            analyseIndicatorDTO.setNotice_date(noticeDate);
        }
        if(StringUtils.isNotBlank(analyseIndicatorDTO.getFree_date()) && analyseIndicatorDTO.getFree_date().length() >=10){
            String freeDate = StringUtils.substring(analyseIndicatorDTO.getFree_date(), 0, 10);
            analyseIndicatorDTO.setFree_date(freeDate);
        }
    }

    /**
     * 东财北上持股数据格式化
     *
     * @param holdShareDTO
     */
    public static void formatNorthHoldShareDTO(DongChaiNorthHoldShareDTO holdShareDTO) {
        if (holdShareDTO == null) {
            return;
        }

        if (holdShareDTO.getHoldShares() != null) {
            double holdShares = NumberUtil.format(holdShareDTO.getHoldShares(), 1);
            holdShareDTO.setHoldShares(holdShares);
        }
        if(holdShareDTO.getHoldMarketCap() !=null){
            double holdMarketCap = NumberUtil.format(holdShareDTO.getHoldMarketCap(), 1);
            holdShareDTO.setHoldMarketCap(holdMarketCap);
        }
        if (holdShareDTO.getTotalSharesRatio() != null) {
            double totalShareRatio = NumberUtil.format(holdShareDTO.getTotalSharesRatio(), 2);
            holdShareDTO.setTotalSharesRatio(totalShareRatio);
        }
        if (holdShareDTO.getHoldSharePercent() != null) {
            double holdSharePercent = NumberUtil.format(holdShareDTO.getHoldSharePercent(), 2);
            holdShareDTO.setHoldSharePercent(holdSharePercent);
        }
        if (holdShareDTO.getIncreaseShares_7() != null) {
            double increaseShares_7 = NumberUtil.format(holdShareDTO.getIncreaseShares_7(), 1);
            holdShareDTO.setIncreaseShares_7(increaseShares_7);
        }
        if (holdShareDTO.getIncreaseRatio_7() != null) {
            double increaseRatio_7 = NumberUtil.format(holdShareDTO.getIncreaseRatio_7(), 2);
            holdShareDTO.setIncreaseRatio_7(increaseRatio_7);
        }
        if (holdShareDTO.getIncreaseShares_30() != null) {
            double increaseShares_30 = NumberUtil.format(holdShareDTO.getIncreaseShares_30(), 1);
            holdShareDTO.setIncreaseShares_30(increaseShares_30);
        }
        if (holdShareDTO.getIncreaseRatio_30() != null) {
            double increaseRatio_30 = NumberUtil.format(holdShareDTO.getIncreaseRatio_30(), 2);
            holdShareDTO.setIncreaseRatio_30(increaseRatio_30);
        }
        if (holdShareDTO.getIncreaseShares_90() != null) {
            double increaseShares_90 = NumberUtil.format(holdShareDTO.getIncreaseShares_90(), 1);
            holdShareDTO.setIncreaseShares_90(increaseShares_90);
        }
        if (holdShareDTO.getIncreaseRatio_90() != null) {
            double increaseRatio_90 = NumberUtil.format(holdShareDTO.getIncreaseRatio_90(), 2);
            holdShareDTO.setIncreaseRatio_90(increaseRatio_90);
        }
        if (holdShareDTO.getIncreaseShares_180() != null) {
            double increaseShares_180 = NumberUtil.format(holdShareDTO.getIncreaseShares_180(), 1);
            holdShareDTO.setIncreaseShares_180(increaseShares_180);
        }
        if (holdShareDTO.getIncreaseRatio_180() != null) {
            double increaseRatio_180 = NumberUtil.format(holdShareDTO.getIncreaseRatio_180(), 2);
            holdShareDTO.setIncreaseRatio_180(increaseRatio_180);
        }
        if (holdShareDTO.getIncreaseShares_360() != null) {
            double increaseShares_360 = NumberUtil.format(holdShareDTO.getIncreaseShares_360(), 1);
            holdShareDTO.setIncreaseShares_360(increaseShares_360);
        }
        if (holdShareDTO.getIncreaseRatio_360() != null) {
            double increaseRatio_360 = NumberUtil.format(holdShareDTO.getIncreaseRatio_360(), 2);
            holdShareDTO.setIncreaseRatio_360(increaseRatio_360);
        }

    }

    /**
     * 东财行业持股数据格式化
     *
     * @param holdShareDTO
     */
    public static void formatIndNorthHoldShareDTO(DongChaiIndustryHoldShareDTO holdShareDTO) {
        if (holdShareDTO == null) {
            return;
        }

        if (holdShareDTO.getIndTotalMarketCap() != null) {
            double indTotalMarketCap = NumberUtil.format(holdShareDTO.getIndTotalMarketCap(), 1);
            holdShareDTO.setIndTotalMarketCap(indTotalMarketCap);
        }
        if (holdShareDTO.getIndTotalMarketRatio() != null) {
            double indTotalMarketRatio = NumberUtil.format((holdShareDTO.getIndTotalMarketRatio() * 100), 1);
            holdShareDTO.setIndTotalMarketRatio(indTotalMarketRatio);
        }
        if (holdShareDTO.getHoldMarketCap() != null) {
            double holdMarketCap = NumberUtil.format(holdShareDTO.getHoldMarketCap(), 1);
            holdShareDTO.setHoldMarketCap(holdMarketCap);
        }
        if (holdShareDTO.getIndustryRatio() != null) {
            double industryRatio = NumberUtil.format((holdShareDTO.getIndustryRatio() * 100), 1);
            holdShareDTO.setIndustryRatio(industryRatio);
        }
        if (holdShareDTO.getOverHoldRatio() != null) {
            double overHoldRatio = NumberUtil.format((holdShareDTO.getOverHoldRatio() * 100), 1);
            holdShareDTO.setOverHoldRatio(overHoldRatio);
        }
        if (holdShareDTO.getInd_ratio_chg_7() != null) {
            double ind_ratio_chg_7 = NumberUtil.format((holdShareDTO.getInd_ratio_chg_7() * 100), 1);
            holdShareDTO.setInd_ratio_chg_7(ind_ratio_chg_7);
        }
        if (holdShareDTO.getInd_ratio_chg_30() != null) {
            double ind_ratio_chg_30 = NumberUtil.format((holdShareDTO.getInd_ratio_chg_30() * 100), 1);
            holdShareDTO.setInd_ratio_chg_30(ind_ratio_chg_30);
        }
        if (holdShareDTO.getInd_ratio_chg_90() != null) {
            double ind_ratio_chg_90 = NumberUtil.format((holdShareDTO.getInd_ratio_chg_90() * 100), 1);
            holdShareDTO.setInd_ratio_chg_90(ind_ratio_chg_90);
        }
        if (holdShareDTO.getInd_ratio_chg_180() != null) {
            double ind_ratio_chg_180 = NumberUtil.format((holdShareDTO.getInd_ratio_chg_180() * 100), 1);
            holdShareDTO.setInd_ratio_chg_180(ind_ratio_chg_180);
        }
        if (holdShareDTO.getInd_ratio_chg_360() != null) {
            double ind_ratio_chg_360 = NumberUtil.format((holdShareDTO.getInd_ratio_chg_360() * 100), 1);
            holdShareDTO.setInd_ratio_chg_360(ind_ratio_chg_360);
        }
    }

}
