package com.example.mq.test.stock.utils;

import com.example.mq.test.stock.model.AnalyseIndicatorDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: maqiang
 * @CreateTime: 2025-09-20 00:49:29
 * @Description:
 */
public class IndicatorFilterUtils {

    /**
     * 筛选数据
     *
     * @param indicatorDTOList
     * @return
     */
    public static List<AnalyseIndicatorDTO> filterByIndicator(List<AnalyseIndicatorDTO> indicatorDTOList) {
        if (CollectionUtils.isEmpty(indicatorDTOList)) {
            return Lists.newArrayList();
        }

        List<AnalyseIndicatorDTO> analyseIndicatorDTOList = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getRevenue() != null)
                .filter(indicatorDTO -> indicatorDTO.getKLineSize() != null && indicatorDTO.getKLineSize() > 300)
                .filter(indicatorDTO -> indicatorDTO.getMarket_capital() != null && indicatorDTO.getMarket_capital() >= 30)
                .filter(indicatorDTO -> indicatorDTO.getGw_ia_assert_rate() != null && indicatorDTO.getGw_ia_assert_rate() <= 0.3)
                .filter(indicatorDTO -> indicatorDTO.getPledge_ratio() !=null && indicatorDTO.getPledge_ratio() <= 0.3)
                .filter(indicatorDTO -> {
                    if(StringUtils.isBlank(indicatorDTO.getName())){
                        return false;
                    }

                    if(indicatorDTO.getName().contains("ST") || indicatorDTO.getName().contains("S")){
                        return false;
                    }

                    return true;
                })
                .filter(indicatorDTO -> {
                    String ind_name = indicatorDTO.getInd_name();
                    if (StringUtils.isNotBlank(ind_name)) {
                        // 农业、环保、建筑相关行业直接过滤掉
                        if (ind_name.contains("农产品") || ind_name.contains("养殖") || ind_name.contains("种植")
                                || ind_name.contains("环保") || ind_name.contains("建筑装饰") || ind_name.contains("房地产")
                                || ind_name.contains("保险") || ind_name.contains("纺织") || ind_name.contains("服装") ) {
                            return false;
                        }

                        // 医药行业，过滤毛利率和净利率差过高的数据
                        if (ind_name.contains("医") || ind_name.contains("药")) {
                            if (indicatorDTO.getGross_margin_rate() != null && indicatorDTO.getNet_selling_rate() != null) {
                                double rate_diff_value = indicatorDTO.getGross_margin_rate() - indicatorDTO.getNet_selling_rate();
                                if(indicatorDTO.getNet_selling_rate() < 0.2 && rate_diff_value > 0.4){
                                    return false;
                                }
                            }
                        }
                    }

                    return true;
                })
                .filter(indicatorDTO -> {
                    // ROE_TTM >8%, 或去现后ROE >12%
                    if (indicatorDTO.getAvg_roe_ttm() != null && indicatorDTO.getAvg_roe_ttm() >= 0.08 && indicatorDTO.getAvg_roe_ttm() <= 0.5) {
                        return true;
                    }
                    if (indicatorDTO.getAvg_roe_ttm_v1() != null && indicatorDTO.getAvg_roe_ttm_v1() >= 0.12 && indicatorDTO.getAvg_roe_ttm_v1() < 2) {
                        return true;
                    }

                    return false;
                })
                .filter(indicatorDTO -> {
                    if (indicatorDTO.getOperating_income_yoy() != null && indicatorDTO.getOperating_income_yoy() <= 1.5) {
                        // 当季营收的最低要求
                        if (indicatorDTO.getCur_q_operating_income_yoy() != null && indicatorDTO.getCur_q_operating_income_yoy() >= -0.05) {
                            return true;
                        }

                        // 当季营收下滑，则要求当季净利润同比增加
                        if (indicatorDTO.getCur_q_net_profit_atsopc_yoy() != null && indicatorDTO.getCur_q_net_profit_atsopc_yoy() >= 0.1) {
                            return true;
                        }

                        // 当季毛利率、净利率同比好转也可
                        if (indicatorDTO.getCur_q_gross_margin_rate_change() != null && indicatorDTO.getCur_q_net_selling_rate_change() != null) {
                            double last_y_same_q_gross_margin_rate = indicatorDTO.getCur_q_gross_margin_rate() - indicatorDTO.getCur_q_gross_margin_rate_change();
                            double last_y_same_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate() - indicatorDTO.getCur_q_net_selling_rate_change();
                            double margin_rate_change_rate = indicatorDTO.getCur_q_gross_margin_rate_change() / last_y_same_q_gross_margin_rate;
                            double net_selling_change_rate = indicatorDTO.getCur_q_net_selling_rate_change() / last_y_same_q_net_selling_rate;
                            if(margin_rate_change_rate > 0.1 && net_selling_change_rate > 0.1){
                                return true;
                            }

                            return false;
                        }

                        // 当季毛利率、净利率环比好转也可
                        if (indicatorDTO.getCur_q_gross_margin_rate_q_chg() != null && indicatorDTO.getCur_q_gross_margin_rate_q_chg() >= 0.1
                                && indicatorDTO.getCur_q_net_selling_rate_q_chg() != null && indicatorDTO.getCur_q_net_selling_rate_q_chg() >= 0.1) {
                            double last_q_gross_margin_rate = indicatorDTO.getCur_q_gross_margin_rate() - indicatorDTO.getCur_q_gross_margin_rate_q_chg();
                            double last_q_net_selling_rate = indicatorDTO.getCur_q_net_selling_rate() - indicatorDTO.getCur_q_net_selling_rate_q_chg();
                            double margin_rate_change_rate = indicatorDTO.getCur_q_gross_margin_rate_q_chg() / last_q_gross_margin_rate;
                            double net_selling_change_rate = indicatorDTO.getCur_q_net_selling_rate_q_chg() / last_q_net_selling_rate;
                            if(margin_rate_change_rate > 0.1 && net_selling_change_rate > 0.1){
                                return true;
                            }

                            return false;
                        }
                    }

                    return false;
                })
                .filter(indicatorDTO -> {
                    boolean isMatch =false;
                    if(indicatorDTO.getReceivable_turnover_days() !=null){
                        // 应收周转天数<250天
                        if(indicatorDTO.getReceivable_turnover_days() <= 250){
                            isMatch =true;
                        }

                        // 应收周转天数>200天, 且同比>15%的，则去掉
                        if(indicatorDTO.getReceivable_turnover_days() >200 && indicatorDTO.getReceivable_turnover_days_rate() !=null
                                && indicatorDTO.getReceivable_turnover_days_rate() > 0.15){
                            isMatch =false;
                        }
                    }

                    return isMatch;
                })
                .filter(indicatorDTO -> {
                    boolean isMatch =false;
                    if(indicatorDTO.getInventory_turnover_days() !=null){
                        // 存货周转天数<350, 或应收账款周转天数<120天
                        if (indicatorDTO.getInventory_turnover_days() <= 350) {
                            isMatch =true;
                        } else if (indicatorDTO.getReceivable_turnover_days() != null && indicatorDTO.getReceivable_turnover_days() <= 120) {
                            isMatch =true;
                        }

                        // 存货周转天数在250～350之间的，同比恶化的去掉
                        if(indicatorDTO.getInventory_turnover_days() >300 && indicatorDTO.getInventory_turnover_days_rate() !=null
                                && indicatorDTO.getInventory_turnover_days_rate() > 0.15){
                            isMatch =false;
                        } else if (indicatorDTO.getInventory_turnover_days() > 250 && indicatorDTO.getInventory_turnover_days_rate() != null
                                && indicatorDTO.getInventory_turnover_days_rate() > 0.2) {
                            isMatch =false;
                        }
                    }

                    return isMatch;
                })
                .filter(indicatorDTO -> {
                    // 均线分位值在30%以下
                    if (indicatorDTO.getMa_1000_diff_p() != null && indicatorDTO.getMa_1000_diff_p() < 0.3) {
                        return true;
                    }

                    // pb分位值在30%以下
                    if(indicatorDTO.getPb_p_1000() !=null && indicatorDTO.getPb_p_1000() <=0.3){
                        return true;
                    }else if(indicatorDTO.getPb_p_1000() !=null && indicatorDTO.getPb_p_1000() <=0.5){
                        // pb分位值在30%～50%时，营收和净利润双增
                        if(indicatorDTO.getCur_q_operating_income_yoy() !=null && indicatorDTO.getCur_q_operating_income_yoy() >= 0.1
                                && indicatorDTO.getCur_q_net_profit_atsopc_yoy() !=null && indicatorDTO.getCur_q_net_profit_atsopc_yoy() >= 0.15){
                            return true;
                        }
                    }else{
                        // pb分位值>50%时，营收和净利润双增, 且毛利率和净利率有要求
                        if(indicatorDTO.getCur_q_operating_income_yoy() !=null && indicatorDTO.getCur_q_operating_income_yoy() >= 0.1
                                && indicatorDTO.getCur_q_net_profit_atsopc_yoy() !=null && indicatorDTO.getCur_q_net_profit_atsopc_yoy() >= 0.15
                                && indicatorDTO.getCur_q_gross_margin_rate() !=null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.30
                                && indicatorDTO.getCur_q_net_selling_rate() !=null && indicatorDTO.getCur_q_net_selling_rate() >= 0.15){
                            return true;
                        }
                    }

                    return false;
                })
                .filter(indicatorDTO -> {
                    if(indicatorDTO.getMarket_capital() ==null){
                        return false;
                    }

                    if (indicatorDTO.getMarket_capital() >= 1000) {
                        // 市值千亿以上，毛利率和净利率条件放宽
                        return indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.1
                                && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.05;
                    } else if (indicatorDTO.getMarket_capital() >= 100) {
                        // 市值在100亿~1000亿间
                        return indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.15
                                && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.07;
                    } else if (indicatorDTO.getMarket_capital() >= 50) {
                        // 市值在50亿~100亿间，毛利率和净利率要求提高, 且要求营收增长率
                        return indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.2
                                && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.1
                                && indicatorDTO.getCur_q_operating_income_yoy() !=null && indicatorDTO.getCur_q_operating_income_yoy() >=0.10;
                    } else {
                        // 市值50亿以下的，毛利率和净利率要求更高, 且要求营收增长率
                        return indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.25
                                && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.15
                                && indicatorDTO.getCur_q_operating_income_yoy() !=null && indicatorDTO.getCur_q_operating_income_yoy() >=0.15;
                    }
                })
                .collect(Collectors.toList());

        return analyseIndicatorDTOList;
    }

}
