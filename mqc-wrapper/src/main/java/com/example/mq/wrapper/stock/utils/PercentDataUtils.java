package com.example.mq.wrapper.stock.utils;

import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.model.AnalyseIndicatorDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: maqiang
 * @CreateTime: 2025-09-20 08:53:59
 * @Description:
 */
public class PercentDataUtils {

    /**
     * 计算百分位数据
     *
     * @param indicatorDTOList
     * @return
     */
    public static void getAndSaveIndicatorDTOPercent(String kLineDate, List<AnalyseIndicatorDTO> indicatorDTOList) {
        if (StringUtils.isBlank(kLineDate) || CollectionUtils.isEmpty(indicatorDTOList)) {
            return;
        }

        // 全行业数据
        String indName ="全行业";
        String msgHeader = "行业,指标,10分位,25分位,50分位,75分位,90分位,95分位";

        List<String> percentMsgList = Lists.newArrayList();

        // ROE_TTM
        List<Double> avg_roe_ttm_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm))
                .map(AnalyseIndicatorDTO::getAvg_roe_ttm)
                .collect(Collectors.toList());
        String percent_msg_roe_ttm = getIndicatorPercentMsg(indName, "ROE_TTM", avg_roe_ttm_list);
        if(StringUtils.isNotBlank(percent_msg_roe_ttm)){
            percentMsgList.add(percent_msg_roe_ttm);
        }

        // ROE_TTM_V1
        List<Double> avg_roe_ttm_v1_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm_v1() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm_v1))
                .map(AnalyseIndicatorDTO::getAvg_roe_ttm_v1)
                .collect(Collectors.toList());
        String percent_msg_roe_ttm_v1 = getIndicatorPercentMsg(indName, "ROE_TTM_V1", avg_roe_ttm_v1_list);
        if(StringUtils.isNotBlank(percent_msg_roe_ttm_v1)){
            percentMsgList.add(percent_msg_roe_ttm_v1);
        }

        // pe
        List<Double> pe_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPe() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe))
                .map(AnalyseIndicatorDTO::getPe)
                .collect(Collectors.toList());
        String percent_msg_pe = getIndicatorPercentMsg(indName, "PE", pe_list);
        if(StringUtils.isNotBlank(percent_msg_pe)){
            percentMsgList.add(percent_msg_pe);
        }

        // pe_p_1000
        List<Double> pe_p_1000_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPe_p_1000() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe_p_1000))
                .map(AnalyseIndicatorDTO::getPe_p_1000)
                .collect(Collectors.toList());
        String percent_msg_pe_p_1000 = getIndicatorPercentMsg(indName, "pe_p_1000", pe_p_1000_list);
        if(StringUtils.isNotBlank(percent_msg_pe_p_1000)){
            percentMsgList.add(percent_msg_pe_p_1000);
        }

        // pb
        List<Double> pb_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPb() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb))
                .map(AnalyseIndicatorDTO::getPb)
                .collect(Collectors.toList());
        String percent_msg_pb = getIndicatorPercentMsg(indName, "pb", pb_list);
        if (StringUtils.isNotBlank(percent_msg_pb)) {
            percentMsgList.add(percent_msg_pb);
        }

        // pb_p_1000
        List<Double> pb_p_1000_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPb_p_1000() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb_p_1000))
                .map(AnalyseIndicatorDTO::getPb_p_1000)
                .collect(Collectors.toList());
        String percent_msg_pb_p_1000 = getIndicatorPercentMsg(indName, "pb_p_1000", pb_p_1000_list);
        if (StringUtils.isNotBlank(percent_msg_pb_p_1000)) {
            percentMsgList.add(percent_msg_pb_p_1000);
        }

        // 毛利率
        List<Double> gross_margin_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getGross_margin_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getGross_margin_rate))
                .map(AnalyseIndicatorDTO::getGross_margin_rate)
                .collect(Collectors.toList());
        String percent_msg_gross_margin_rate = getIndicatorPercentMsg(indName, "毛利率", gross_margin_rate_list);
        if (StringUtils.isNotBlank(percent_msg_gross_margin_rate)) {
            percentMsgList.add(percent_msg_gross_margin_rate);
        }

        // 净利率
        List<Double> net_selling_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getNet_selling_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_selling_rate))
                .map(AnalyseIndicatorDTO::getNet_selling_rate)
                .collect(Collectors.toList());
        String percent_msg_net_selling_rate = getIndicatorPercentMsg(indName, "净利率", net_selling_rate_list);
        if (StringUtils.isNotBlank(percent_msg_net_selling_rate)) {
            percentMsgList.add(percent_msg_net_selling_rate);
        }

        // 营收同比
        List<Double> operating_income_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOperating_income_yoy))
                .map(AnalyseIndicatorDTO::getOperating_income_yoy)
                .collect(Collectors.toList());
        String percent_msg_operating_income_yoy = getIndicatorPercentMsg(indName, "营收同比", operating_income_yoy_list);
        if (StringUtils.isNotBlank(percent_msg_operating_income_yoy)) {
            percentMsgList.add(percent_msg_operating_income_yoy);
        }

        // 净利润同比
        List<Double> net_profit_atsopc_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getNet_profit_atsopc_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy))
                .map(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy)
                .collect(Collectors.toList());
        String percent_msg_net_profit_atsopc_yoy = getIndicatorPercentMsg(indName, "净利润同比", net_profit_atsopc_yoy_list);
        if (StringUtils.isNotBlank(percent_msg_net_profit_atsopc_yoy)) {
            percentMsgList.add(percent_msg_net_profit_atsopc_yoy);
        }

        // 当季毛利率
        List<Double> cur_q_gross_margin_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate))
                .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate)
                .collect(Collectors.toList());
        String percent_msg_cur_q_gross_margin_rate = getIndicatorPercentMsg(indName, "当季毛利率", cur_q_gross_margin_rate_list);
        if (StringUtils.isNotBlank(percent_msg_cur_q_gross_margin_rate)) {
            percentMsgList.add(percent_msg_cur_q_gross_margin_rate);
        }

        // 当季净利率
        List<Double> cur_q_net_selling_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate))
                .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate)
                .collect(Collectors.toList());
        String percent_msg_cur_q_net_selling_rate = getIndicatorPercentMsg(indName, "当季净利率", cur_q_net_selling_rate_list);
        if (StringUtils.isNotBlank(percent_msg_cur_q_net_selling_rate)) {
            percentMsgList.add(percent_msg_cur_q_net_selling_rate);
        }

        // 当季营收同比
        List<Double> cur_q_operating_income_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_operating_income_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy))
                .map(AnalyseIndicatorDTO::getCur_q_operating_income_yoy)
                .collect(Collectors.toList());
        String percent_msg_cur_q_operating_income_yoy = getIndicatorPercentMsg(indName, "当季营收同比", cur_q_operating_income_yoy_list);
        if (StringUtils.isNotBlank(percent_msg_cur_q_operating_income_yoy)) {
            percentMsgList.add(percent_msg_cur_q_operating_income_yoy);
        }

        // 当季净利润同比
        List<Double> cur_q_net_profit_atsopc_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_net_profit_atsopc_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy))
                .map(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy)
                .collect(Collectors.toList());
        String percent_msg_cur_q_net_profit_atsopc_yoy = getIndicatorPercentMsg(indName, "当季净利润同比", cur_q_net_profit_atsopc_yoy_list);
        if (StringUtils.isNotBlank(percent_msg_cur_q_net_profit_atsopc_yoy)) {
            percentMsgList.add(percent_msg_cur_q_net_profit_atsopc_yoy);
        }

        // 应收周转天数
        List<Double> receivable_turnover_days_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getReceivable_turnover_days))
                .map(AnalyseIndicatorDTO::getReceivable_turnover_days)
                .collect(Collectors.toList());
        String percent_msg_receivable_turnover_days = getIndicatorPercentMsg(indName, "应收周转天数", receivable_turnover_days_list);
        if (StringUtils.isNotBlank(percent_msg_receivable_turnover_days)) {
            percentMsgList.add(percent_msg_receivable_turnover_days);
        }

        // 存货周转天数
        List<Double> inventory_turnover_days_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getInventory_turnover_days() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getInventory_turnover_days))
                .map(AnalyseIndicatorDTO::getInventory_turnover_days)
                .collect(Collectors.toList());
        String percent_msg_inventory_turnover_days = getIndicatorPercentMsg(indName, "存货周转天数", inventory_turnover_days_list);
        if (StringUtils.isNotBlank(percent_msg_inventory_turnover_days)) {
            percentMsgList.add(percent_msg_inventory_turnover_days);
        }

        // 市值
        List<Double> market_capital_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getMarket_capital() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getMarket_capital))
                .map(AnalyseIndicatorDTO::getMarket_capital)
                .collect(Collectors.toList());
        String percent_msg_market_capital = getIndicatorPercentMsg(indName, "市值", market_capital_list);
        if (StringUtils.isNotBlank(percent_msg_market_capital)) {
            percentMsgList.add(percent_msg_market_capital);
        }

        // 记录结果
        String percentListName = String.format(StockConstant.INDICATOR_LIST_PERCENT, kLineDate);
        FileOperateUtils.saveLocalFile(percentListName, msgHeader, percentMsgList, false);
    }

    /**
     * 计算指标的百分位值
     *
     * @param indName
     * @param indicatorName
     * @param indicatorValueList
     * @return
     */
    public static String getIndicatorPercentMsg(String indName, String indicatorName, List<Double> indicatorValueList) {
        if (StringUtils.isBlank(indName) || StringUtils.isBlank(indicatorName) || CollectionUtils.isEmpty(indicatorValueList)) {
            return StringUtils.EMPTY;
        }

        List<Double> sortedIndicatorValueList = indicatorValueList.stream()
                .sorted()
                .collect(Collectors.toList());

        int totalSize = sortedIndicatorValueList.size();

        List<Double> percentValueList =Lists.newArrayList();
        percentValueList.add(sortedIndicatorValueList.get(totalSize * 10 / 100));
        percentValueList.add(sortedIndicatorValueList.get(totalSize * 25 / 100));
        percentValueList.add(sortedIndicatorValueList.get(totalSize * 50 / 100));
        percentValueList.add(sortedIndicatorValueList.get(totalSize * 75 / 100));
        percentValueList.add(sortedIndicatorValueList.get(totalSize * 90 / 100));
        percentValueList.add(sortedIndicatorValueList.get(totalSize * 95 / 100));

        return indName + "," + indicatorName + "," + StringUtils.join(percentValueList, ",");
    }

    /**
     * 计算各行业50百分位的指标数据
     *
     * @param indicatorDTOList
     * @return
     */
    public static void getAndSavePercentValueByIndustry(String kLineDate, List<AnalyseIndicatorDTO> indicatorDTOList, int percentValue) {
        if (StringUtils.isBlank(kLineDate) || CollectionUtils.isEmpty(indicatorDTOList)) {
            return;
        }

        String header = "行业,ROE_TTM,ROE_TTM_V1,PE_TTM,pe百分位,市净率,PB百分位,当季营收同比,当季净利润同比,上一季的营收同比,上一季的净利润同比,营收同比,净利润同比" +
                ",当季毛利率,当季净利率,当季毛利率同比,当季净利率同比,当季毛利率环比,当季净利率环比,毛利率,净利率" +
                ",应收周转天数,存货周转天数,应收周转天数同比,存货周转天数同比,市值" +
                ",近1周的股价变化,近1月的股价变化,近3月的股价变化,1月后的股价变化,3月后的股价变化,半年后的股价变化,1年后的股价变化";
        List<String> strPercentList = Lists.newArrayList();

        // 加上全行业的数据
        Map<String, List<AnalyseIndicatorDTO>> indNameAndIndicatorDTOMap = indicatorDTOList.stream()
                .filter(indicatorDTO -> StringUtils.isNotBlank(indicatorDTO.getInd_name()))
                .collect(Collectors.groupingBy(AnalyseIndicatorDTO::getInd_name));
        indNameAndIndicatorDTOMap.put("全行业", new ArrayList<>(indicatorDTOList));

        for (String indName : indNameAndIndicatorDTOMap.keySet()) {
            if(indName.contains("银行") || indName.contains("保险")){
                continue;
            }

            List<AnalyseIndicatorDTO> tmpIndicatorDTOList = indNameAndIndicatorDTOMap.get(indName);
            if (CollectionUtils.isEmpty(tmpIndicatorDTOList)) {
                continue;
            }

            StringBuilder msgBuilder =new StringBuilder(indName);

            // ROE_TTM
            List<Double> avg_roe_ttm_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm))
                    .map(AnalyseIndicatorDTO::getAvg_roe_ttm)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(avg_roe_ttm_list)) {
                int totalSize = avg_roe_ttm_list.size();
                Double value = avg_roe_ttm_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // ROE_TTM_V1
            List<Double> avg_roe_ttm_v1_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm_v1() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm_v1))
                    .map(AnalyseIndicatorDTO::getAvg_roe_ttm_v1)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(avg_roe_ttm_v1_list)) {
                int totalSize = avg_roe_ttm_v1_list.size();
                Double value = avg_roe_ttm_v1_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // pe
            List<Double> pe_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPe() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe))
                    .map(AnalyseIndicatorDTO::getPe)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pe_list)) {
                int totalSize = pe_list.size();
                Double value = pe_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // pe_p_1000
            List<Double> pe_p_1000_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPe_p_1000() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe_p_1000))
                    .map(AnalyseIndicatorDTO::getPe_p_1000)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pe_p_1000_list)) {
                int totalSize = pe_p_1000_list.size();
                Double value = pe_p_1000_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // pb
            List<Double> pb_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPb() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb))
                    .map(AnalyseIndicatorDTO::getPb)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pb_list)) {
                int totalSize = pb_list.size();
                Double value = pb_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // pb_p_100
            List<Double> pb_p_1000_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getPb_p_1000() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb_p_1000))
                    .map(AnalyseIndicatorDTO::getPb_p_1000)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pb_p_1000_list)) {
                int totalSize = pb_p_1000_list.size();
                Double value = pb_p_1000_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季营收同比
            List<Double> cur_q_operating_income_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_operating_income_yoy() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy))
                    .map(AnalyseIndicatorDTO::getCur_q_operating_income_yoy)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_operating_income_yoy_list)) {
                int totalSize = cur_q_operating_income_yoy_list.size();
                Double value = cur_q_operating_income_yoy_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季净利润同比
            List<Double> cur_q_net_profit_atsopc_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_net_profit_atsopc_yoy() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy))
                    .map(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_net_profit_atsopc_yoy_list)) {
                int totalSize = cur_q_net_profit_atsopc_yoy_list.size();
                Double value = cur_q_net_profit_atsopc_yoy_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 上一季的营收同比
            List<Double> last_q_operating_income_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getLast_q_operating_income_yoy() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getLast_q_operating_income_yoy))
                    .map(AnalyseIndicatorDTO::getLast_q_operating_income_yoy)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(last_q_operating_income_yoy_list)) {
                int totalSize = last_q_operating_income_yoy_list.size();
                Double value = last_q_operating_income_yoy_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 上一季的净利润同比
            List<Double> last_q_net_profit_atsopc_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getLast_q_net_profit_atsopc_yoy() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getLast_q_net_profit_atsopc_yoy))
                    .map(AnalyseIndicatorDTO::getLast_q_net_profit_atsopc_yoy)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(last_q_net_profit_atsopc_yoy_list)) {
                int totalSize = last_q_net_profit_atsopc_yoy_list.size();
                Double value = last_q_net_profit_atsopc_yoy_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 营收同比
            List<Double> operating_income_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOperating_income_yoy))
                    .map(AnalyseIndicatorDTO::getOperating_income_yoy)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(operating_income_yoy_list)) {
                int totalSize = operating_income_yoy_list.size();
                Double value = operating_income_yoy_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 净利润同比
            List<Double> net_profit_atsopc_yoy_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getNet_profit_atsopc_yoy() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy))
                    .map(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(net_profit_atsopc_yoy_list)) {
                int totalSize = net_profit_atsopc_yoy_list.size();
                Double value = net_profit_atsopc_yoy_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季毛利率
            List<Double> cur_q_gross_margin_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate))
                    .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_gross_margin_rate_list)) {
                int totalSize = cur_q_gross_margin_rate_list.size();
                Double value = cur_q_gross_margin_rate_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季净利率
            List<Double> cur_q_net_selling_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate))
                    .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_net_selling_rate_list)) {
                int totalSize = cur_q_net_selling_rate_list.size();
                Double value = cur_q_net_selling_rate_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季毛利率同比
            List<Double> cur_q_gross_margin_rate_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate_change))
                    .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_gross_margin_rate_change_list)) {
                int totalSize = cur_q_gross_margin_rate_change_list.size();
                Double value = cur_q_gross_margin_rate_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季净利率同比
            List<Double> cur_q_net_selling_rate_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate_change))
                    .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_net_selling_rate_change_list)) {
                int totalSize = cur_q_net_selling_rate_change_list.size();
                Double value = cur_q_net_selling_rate_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季毛利率环比
            List<Double> cur_q_gross_margin_rate_q_chg_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate_q_chg() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate_q_chg))
                    .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate_q_chg)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_gross_margin_rate_q_chg_list)) {
                int totalSize = cur_q_gross_margin_rate_q_chg_list.size();
                Double value = cur_q_gross_margin_rate_q_chg_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 当季净利率环比
            List<Double> cur_q_net_selling_rate_q_chg_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate_q_chg() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate_q_chg))
                    .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate_q_chg)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cur_q_net_selling_rate_q_chg_list)) {
                int totalSize = cur_q_net_selling_rate_q_chg_list.size();
                Double value = cur_q_net_selling_rate_q_chg_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 毛利率
            List<Double> gross_margin_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getGross_margin_rate() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getGross_margin_rate))
                    .map(AnalyseIndicatorDTO::getGross_margin_rate)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(gross_margin_rate_list)) {
                int totalSize = gross_margin_rate_list.size();
                Double value = gross_margin_rate_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 净利率
            List<Double> net_selling_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getNet_selling_rate() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_selling_rate))
                    .map(AnalyseIndicatorDTO::getNet_selling_rate)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(net_selling_rate_list)) {
                int totalSize = net_selling_rate_list.size();
                Double value = net_selling_rate_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 应收周转天数
            List<Double> receivable_turnover_days_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getReceivable_turnover_days))
                    .map(AnalyseIndicatorDTO::getReceivable_turnover_days)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(receivable_turnover_days_list)) {
                int totalSize = receivable_turnover_days_list.size();
                Double value = receivable_turnover_days_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 存货周转天数
            List<Double> inventory_turnover_days_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getInventory_turnover_days() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getInventory_turnover_days))
                    .map(AnalyseIndicatorDTO::getInventory_turnover_days)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(inventory_turnover_days_list)) {
                int totalSize = inventory_turnover_days_list.size();
                Double value = inventory_turnover_days_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 应收周转天数同比
            List<Double> receivable_turnover_days_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days_rate() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getReceivable_turnover_days_rate))
                    .map(AnalyseIndicatorDTO::getReceivable_turnover_days_rate)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(receivable_turnover_days_rate_list)) {
                int totalSize = receivable_turnover_days_rate_list.size();
                Double value = receivable_turnover_days_rate_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 存货周转天数
            List<Double> inventory_turnover_days_rate_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getInventory_turnover_days_rate() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getInventory_turnover_days_rate))
                    .map(AnalyseIndicatorDTO::getInventory_turnover_days_rate)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(inventory_turnover_days_rate_list)) {
                int totalSize = inventory_turnover_days_rate_list.size();
                Double value = inventory_turnover_days_rate_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 市值
            List<Double> market_capital_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getMarket_capital() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getMarket_capital))
                    .map(AnalyseIndicatorDTO::getMarket_capital)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(market_capital_list)) {
                int totalSize = market_capital_list.size();
                Double value = market_capital_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 近1周的股价变化
            List<Double> one_week_before_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOne_week_before_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOne_week_before_price_change))
                    .map(AnalyseIndicatorDTO::getOne_week_before_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(one_week_before_price_change_list)) {
                int totalSize = one_week_before_price_change_list.size();
                Double value = one_week_before_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 近1月的股价变化
            List<Double> one_month_before_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOne_month_before_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOne_month_before_price_change))
                    .map(AnalyseIndicatorDTO::getOne_month_before_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(one_month_before_price_change_list)) {
                int totalSize = one_month_before_price_change_list.size();
                Double value = one_month_before_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 近3月的股价变化
            List<Double> three_month_before_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getThree_month_before_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getThree_month_before_price_change))
                    .map(AnalyseIndicatorDTO::getThree_month_before_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(three_month_before_price_change_list)) {
                int totalSize = three_month_before_price_change_list.size();
                Double value = three_month_before_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 1月后的股价变化
            List<Double> one_month_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOne_month_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOne_month_price_change))
                    .map(AnalyseIndicatorDTO::getOne_month_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(one_month_price_change_list)) {
                int totalSize = one_month_price_change_list.size();
                Double value = one_month_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 3月后的股价变化
            List<Double> three_month_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getThree_month_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getThree_month_price_change))
                    .map(AnalyseIndicatorDTO::getThree_month_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(three_month_price_change_list)) {
                int totalSize = three_month_price_change_list.size();
                Double value = three_month_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 6月后的股价变化
            List<Double> half_year_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getHalf_year_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getHalf_year_price_change))
                    .map(AnalyseIndicatorDTO::getHalf_year_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(half_year_price_change_list)) {
                int totalSize = half_year_price_change_list.size();
                Double value = half_year_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            // 1年后的股价变化
            List<Double> one_year_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOne_year_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOne_year_price_change))
                    .map(AnalyseIndicatorDTO::getOne_year_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(one_year_price_change_list)) {
                int totalSize = one_year_price_change_list.size();
                Double value = one_year_price_change_list.get(totalSize * percentValue / 100);
                msgBuilder.append(",").append(value);
            }

            strPercentList.add(msgBuilder.toString());
        }

        // 保存文件
        String percentListName = String.format(StockConstant.INDICATOR_LIST_PERCENT_IND, kLineDate);
        FileOperateUtils.saveLocalFile(percentListName, header, strPercentList, false);
    }

}
