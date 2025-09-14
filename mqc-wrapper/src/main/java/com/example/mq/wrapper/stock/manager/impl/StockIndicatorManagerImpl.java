package com.example.mq.wrapper.stock.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.DateUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.StockCalculateUtils;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.manager.DongChaiDataManager;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.manager.StockIndicatorManager;
import com.example.mq.wrapper.stock.model.*;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiIndustryHoldShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StockIndicatorManagerImpl implements StockIndicatorManager {

    private static final String HEADER = "编码,名称,行业,省市,K线差值百分位,总市值,匹配次数,均线次数" +
            ",资产负债率,PE_TTM,pe百分位,市净率,pb百分位,ROE_TTM,去现后的ROE" +
            ",当季营收同比,当季净利同比,上一季营收同比,上一季净利同比,当季毛利率,当季净利率,当季毛利率同比,当季净利率同比,当季毛利率环比,当季净利率环比" +
            ",营收同比,净利润同比,毛利率,净利率" +
            ",固定资产同比,在建工程同比,商誉+无形/净资产,固定资产/净资产,在建工程/净资产" +
            ",现金等价物/短期负债,经营现金流入/营收,经营现金净额/净利润" +
            ",应付票据及账款,应收票据及账款,应付/应收,应收周转天数,存货周转天数,应收周转天数同比,存货周转天数同比" +
            ",增减持预告时间,增减持类型,增减持数量(万股),变动比例(%),解禁时间,解禁数量(万股),占总市值的比例(%)" +
            ",近6季度的毛利率和净利率,近6季度的营收和利润(亿)" +
            ",K线日期,K线数量,收盘价,ma1000值,股东权益合计,营业收入,营业成本,经营现金流入,经营现金净额,净利润" +
            ",1月后股价波动,3月后股价波动,半年后股价波动,1年后股价波动";

    private static final String HOLD_SHARE_HEADER = "编码,名称,行业,日期,沪港通持股数(万),沪港通持有市值(亿),沪港通持股占比(%),近1000天持股数的百分位" +
            ",近7天的增减持数(万),近7天的增持比例(%),近30天的增减持数(万),近30天的增持比例(%),近90天的增减持数(万),近90天的增持比例(%)" +
            ",近180天增减持数(万),近180天增持比例(%),近360天的增减持数(万),近360天的增持比例(%)";
    private static final String IND_HOLD_SHARE_HEADER = "行业,日期,行业总市值,行业总市值占比,沪港通持有市值,占北上总市值的比例(%)" +
            ",沪港通超配比例(%),7天沪港通持股占比变化(%),30天沪港通持股占比变化(%),90天沪港通持股占比变化(%),180天沪港通持股占比变化(%),360天沪港通持股占比变化(%)";

    @Override
    public void calculateAndSaveAllAnalysisDTO(String kLineDate, List<String> stockCodeList, Integer reportYear, FinanceReportTypeEnum reportTypeEnum) {
        // 获取全部股票的指标数据
        List<AnalyseIndicatorDTO> allIndicatorDTOList = stockCodeList.parallelStream()
                .map(stockCode -> {
                    try {
                        // 指标源数据
                        AnalyseIndicatorElement indicatorElement = this.queryIndicatorElement(stockCode, reportYear, reportTypeEnum, kLineDate);

                        // 计算指标值
                        AnalyseIndicatorDTO analyseIndicatorDTO = StockCalculateUtils.calculateAnalyseIndicatorDTO(indicatorElement);

                        // 指标值格式化
                        StockCalculateUtils.formatAnalyseIndicatorDTO(analyseIndicatorDTO);

                        return analyseIndicatorDTO;
                    } catch (Exception e) {
                        System.out.println("errCode: " + stockCode);
                        e.printStackTrace();
                    }

                    return null;
                })
                .filter(analyseIndicatorDTO -> analyseIndicatorDTO != null)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(allIndicatorDTOList)){
            return ;
        }

        // 生成结果
        List<String> strIndicatorList = Lists.newArrayList();
        strIndicatorList.add(HEADER);
        for (AnalyseIndicatorDTO indicatorDTO : allIndicatorDTOList) {
            try {
                Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
                StringBuilder indicatorBuilder = new StringBuilder();
                for (Field field : fields) {
                    Object value = jsonObject.get(field.getName());
                    indicatorBuilder.append(",").append(value);
                }

                strIndicatorList.add(indicatorBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录全部股票的结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String analysisListName = String.format(StockConstant.INDICATOR_LIST_ANALYSIS, kLineDate, strDateTime);
            FileUtils.writeLines(new File(analysisListName), "UTF-8", strIndicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 全市场百分位数据
        this.getAndSaveIndicatorDTOPercent(kLineDate, allIndicatorDTOList);

        // 各行业50百分位指标
        this.getAndSavePercent50ByIndustry(kLineDate, allIndicatorDTOList);

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
        if (StringUtils.isBlank(kLineDate) || CollectionUtils.isEmpty(indicatorDTOList)) {
            return;
        }

        String header = "行业,指标,10分位,25分位,50分位,75分位,90分位,95分位";
        List<String> strPercentList = Lists.newArrayList();
        strPercentList.add(header);

        // 增加全行业数据
        String indName ="全行业";

        // ROE_TTM
        List<Double> avg_roe_ttm_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm))
                .map(AnalyseIndicatorDTO::getAvg_roe_ttm)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(avg_roe_ttm_list)) {
            String msg = this.getIndicatorPercentValue(indName, "ROE_TTM", avg_roe_ttm_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // ROE_TTM_V1
        List<Double> avg_roe_ttm_v1_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getAvg_roe_ttm_v1() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getAvg_roe_ttm_v1))
                .map(AnalyseIndicatorDTO::getAvg_roe_ttm_v1)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(avg_roe_ttm_v1_list)) {
            String msg = this.getIndicatorPercentValue(indName, "ROE_TTM_v1", avg_roe_ttm_v1_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // pe
        List<Double> pe_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPe() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe))
                .map(AnalyseIndicatorDTO::getPe)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pe_list)) {
            String msg = this.getIndicatorPercentValue(indName, "pe", pe_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // pe_p_1000
        List<Double> pe_p_1000_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPe_p_1000() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPe_p_1000))
                .map(AnalyseIndicatorDTO::getPe_p_1000)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pe_p_1000_list)) {
            String msg = this.getIndicatorPercentValue(indName, "pe_p_1000", pe_p_1000_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // pb
        List<Double> pb_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPb() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb))
                .map(AnalyseIndicatorDTO::getPb)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pb_list)) {
            String msg = this.getIndicatorPercentValue(indName, "pb", pb_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // pb_p_100
        List<Double> pb_p_1000_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getPb_p_1000() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getPb_p_1000))
                .map(AnalyseIndicatorDTO::getPb_p_1000)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pb_p_1000_list)) {
            String msg = this.getIndicatorPercentValue(indName, "pb_p_1000", pb_p_1000_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 毛利率
        List<Double> gross_margin_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getGross_margin_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getGross_margin_rate))
                .map(AnalyseIndicatorDTO::getGross_margin_rate)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gross_margin_rate_list)) {
            String msg = this.getIndicatorPercentValue(indName, "毛利率", gross_margin_rate_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 净利率
        List<Double> net_selling_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getNet_selling_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_selling_rate))
                .map(AnalyseIndicatorDTO::getNet_selling_rate)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(net_selling_rate_list)) {
            String msg = this.getIndicatorPercentValue(indName, "净利率", net_selling_rate_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 营收同比
        List<Double> operating_income_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getOperating_income_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOperating_income_yoy))
                .map(AnalyseIndicatorDTO::getOperating_income_yoy)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(operating_income_yoy_list)) {
            String msg = this.getIndicatorPercentValue(indName, "营收同比", operating_income_yoy_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 净利润同比
        List<Double> net_profit_atsopc_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getNet_profit_atsopc_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy))
                .map(AnalyseIndicatorDTO::getNet_profit_atsopc_yoy)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(net_profit_atsopc_yoy_list)) {
            String msg = this.getIndicatorPercentValue(indName, "净利润同比", net_profit_atsopc_yoy_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 当季毛利率
        List<Double> cur_q_gross_margin_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_gross_margin_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_gross_margin_rate))
                .map(AnalyseIndicatorDTO::getCur_q_gross_margin_rate)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cur_q_gross_margin_rate_list)) {
            String msg = this.getIndicatorPercentValue(indName, "当季毛利率", cur_q_gross_margin_rate_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 当季净利率
        List<Double> cur_q_net_selling_rate_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_net_selling_rate() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_selling_rate))
                .map(AnalyseIndicatorDTO::getCur_q_net_selling_rate)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cur_q_net_selling_rate_list)) {
            String msg = this.getIndicatorPercentValue(indName, "当季净利率", cur_q_net_selling_rate_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 当季营收同比
        List<Double> cur_q_operating_income_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_operating_income_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy))
                .map(AnalyseIndicatorDTO::getCur_q_operating_income_yoy)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cur_q_operating_income_yoy_list)) {
            String msg = this.getIndicatorPercentValue(indName, "当季营收同比", cur_q_operating_income_yoy_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 当季净利润同比
        List<Double> cur_q_net_profit_atsopc_yoy_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getCur_q_net_profit_atsopc_yoy() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy))
                .map(AnalyseIndicatorDTO::getCur_q_net_profit_atsopc_yoy)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cur_q_net_profit_atsopc_yoy_list)) {
            String msg = this.getIndicatorPercentValue(indName, "当季净利润同比", cur_q_net_profit_atsopc_yoy_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 当季净利润同比
        List<Double> receivable_turnover_days_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getReceivable_turnover_days() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getReceivable_turnover_days))
                .map(AnalyseIndicatorDTO::getReceivable_turnover_days)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(receivable_turnover_days_list)) {
            String msg = this.getIndicatorPercentValue(indName, "应收周转天数", receivable_turnover_days_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 存货周转天数
        List<Double> inventory_turnover_days_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getInventory_turnover_days() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getInventory_turnover_days))
                .map(AnalyseIndicatorDTO::getInventory_turnover_days)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(inventory_turnover_days_list)) {
            String msg = this.getIndicatorPercentValue(indName, "存货周转天数", inventory_turnover_days_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 市值
        List<Double> market_capital_list = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getMarket_capital() != null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getMarket_capital))
                .map(AnalyseIndicatorDTO::getMarket_capital)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(market_capital_list)) {
            String msg = this.getIndicatorPercentValue(indName, "市值", market_capital_list);
            if (StringUtils.isNotBlank(msg)) {
                strPercentList.add(msg);
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String percentListName = String.format(StockConstant.INDICATOR_LIST_PERCENT, kLineDate, strDateTime);
            FileUtils.writeLines(new File(percentListName), "UTF-8", strPercentList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 计算各行业50百分位的指标数据
     *
     * @param indicatorDTOList
     * @return
     */
    private void getAndSavePercent50ByIndustry(String kLineDate, List<AnalyseIndicatorDTO> indicatorDTOList) {
        if (StringUtils.isBlank(kLineDate) || CollectionUtils.isEmpty(indicatorDTOList)) {
            return;
        }

        String header = "行业,ROE_TTM,ROE_TTM_V1,PE_TTM,pe百分位,市净率,PB百分位,当季营收同比,当季净利润同比,上一季的营收同比,上一季的净利润同比,营收同比,净利润同比" +
                ",当季毛利率,当季净利率,当季毛利率同比,当季净利率同比,当季毛利率环比,当季净利率环比,毛利率,净利率" +
                ",应收周转天数,存货周转天数,应收周转天数同比,存货周转天数同比,市值,近1月股价变化,近3月股价变化,近6月股价变化,近1年股价变化";
        List<String> strPercentList = Lists.newArrayList();
        strPercentList.add(header);

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
                Double value = avg_roe_ttm_list.get(totalSize * 50 / 100);
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
                Double value = avg_roe_ttm_v1_list.get(totalSize * 50 / 100);
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
                Double value = pe_list.get(totalSize * 50 / 100);
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
                Double value = pe_p_1000_list.get(totalSize * 50 / 100);
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
                Double value = pb_list.get(totalSize * 50 / 100);
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
                Double value = pb_p_1000_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_operating_income_yoy_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_net_profit_atsopc_yoy_list.get(totalSize * 50 / 100);
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
                Double value = last_q_operating_income_yoy_list.get(totalSize * 50 / 100);
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
                Double value = last_q_net_profit_atsopc_yoy_list.get(totalSize * 50 / 100);
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
                Double value = operating_income_yoy_list.get(totalSize * 50 / 100);
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
                Double value = net_profit_atsopc_yoy_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_gross_margin_rate_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_net_selling_rate_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_gross_margin_rate_change_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_net_selling_rate_change_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_gross_margin_rate_q_chg_list.get(totalSize * 50 / 100);
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
                Double value = cur_q_net_selling_rate_q_chg_list.get(totalSize * 50 / 100);
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
                Double value = gross_margin_rate_list.get(totalSize * 50 / 100);
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
                Double value = net_selling_rate_list.get(totalSize * 50 / 100);
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
                Double value = receivable_turnover_days_list.get(totalSize * 50 / 100);
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
                Double value = inventory_turnover_days_list.get(totalSize * 50 / 100);
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
                Double value = receivable_turnover_days_rate_list.get(totalSize * 50 / 100);
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
                Double value = inventory_turnover_days_rate_list.get(totalSize * 50 / 100);
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
                Double value = market_capital_list.get(totalSize * 50 / 100);
                msgBuilder.append(",").append(value);
            }

            // 近1月股价变化
            List<Double> one_month_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOne_month_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOne_month_price_change))
                    .map(AnalyseIndicatorDTO::getOne_month_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(one_month_price_change_list)) {
                int totalSize = one_month_price_change_list.size();
                Double value = one_month_price_change_list.get(totalSize * 50 / 100);
                msgBuilder.append(",").append(value);
            }

            // 近3月股价变化
            List<Double> three_month_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getThree_month_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getThree_month_price_change))
                    .map(AnalyseIndicatorDTO::getThree_month_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(three_month_price_change_list)) {
                int totalSize = three_month_price_change_list.size();
                Double value = three_month_price_change_list.get(totalSize * 50 / 100);
                msgBuilder.append(",").append(value);
            }

            // 近6月股价变化
            List<Double> half_year_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getHalf_year_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getHalf_year_price_change))
                    .map(AnalyseIndicatorDTO::getHalf_year_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(half_year_price_change_list)) {
                int totalSize = half_year_price_change_list.size();
                Double value = half_year_price_change_list.get(totalSize * 50 / 100);
                msgBuilder.append(",").append(value);
            }

            // 近1年股价变化
            List<Double> one_year_price_change_list = tmpIndicatorDTOList.stream()
                    .filter(indicatorDTO -> indicatorDTO.getOne_year_price_change() != null)
                    .sorted(Comparator.comparing(AnalyseIndicatorDTO::getOne_year_price_change))
                    .map(AnalyseIndicatorDTO::getOne_year_price_change)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(one_year_price_change_list)) {
                int totalSize = one_year_price_change_list.size();
                Double value = one_year_price_change_list.get(totalSize * 50 / 100);
                msgBuilder.append(",").append(value);
            }

            strPercentList.add(msgBuilder.toString());
        }

        // 记录结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String percentListName = String.format(StockConstant.INDICATOR_LIST_PERCENT_IND, kLineDate, strDateTime);
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
    private String getIndicatorPercentValue(String indName, String indicatorName, List<Double> indicatorValueList) {
        if (StringUtils.isBlank(indName) || StringUtils.isBlank(indicatorName) || CollectionUtils.isEmpty(indicatorValueList)) {
            return StringUtils.EMPTY;
        }

        List<Double> sortedIndicatorValueList = indicatorValueList.stream()
                .sorted()
                .collect(Collectors.toList());

        int totalSize = sortedIndicatorValueList.size();
        String msg = new StringBuilder().append(indName)
                .append(",").append(indicatorName)
                .append(",").append(sortedIndicatorValueList.get(totalSize * 10 / 100))
                .append(",").append(sortedIndicatorValueList.get(totalSize * 25 / 100))
                .append(",").append(sortedIndicatorValueList.get(totalSize * 50 / 100))
                .append(",").append(sortedIndicatorValueList.get(totalSize * 75 / 100))
                .append(",").append(sortedIndicatorValueList.get(totalSize * 90 / 100))
                .append(",").append(sortedIndicatorValueList.get(totalSize * 95 / 100))
                .toString();

        return msg;
    }

    /**
     * 筛选并记录结果
     *
     * @param kLineDate
     * @param allIndicatorDTOList
     */
    private void filterAndSaveIndicatorDTO(String kLineDate, List<AnalyseIndicatorDTO> allIndicatorDTOList) {
        // 筛选合适的数据
        List<AnalyseIndicatorDTO> filterIndicatorDTOList = this.filterByIndicator(allIndicatorDTOList);

        // 白名单数据处理
        LocalDataManager localDataManager = new LocalDataManagerImpl();
        List<String> whiteStockCodeList = localDataManager.getWhiteStockCodeList();
        if (CollectionUtils.isNotEmpty(whiteStockCodeList)) {
            List<String> filterCodeList = filterIndicatorDTOList.stream()
                    .map(AnalyseIndicatorDTO::getCode)
                    .collect(Collectors.toList());

            Map<String, AnalyseIndicatorDTO> allCodeAndIndicatorList = allIndicatorDTOList.stream()
                    .collect(Collectors.toMap(AnalyseIndicatorDTO::getCode, val -> val, (val1, val2) -> val1));

            for (String whiteStockCode : whiteStockCodeList) {
                if (!filterCodeList.contains(whiteStockCode)) {
                    AnalyseIndicatorDTO tmpIndicatorDTO = allCodeAndIndicatorList.get(whiteStockCode);
                    if (tmpIndicatorDTO != null) {
                        filterIndicatorDTOList.add(tmpIndicatorDTO);
                    }
                }
            }
        }

        // 生成结果
        List<String> strIndicatorList = Lists.newArrayList();
        strIndicatorList.add(HEADER);

        filterIndicatorDTOList = Optional.ofNullable(filterIndicatorDTOList).orElse(Lists.newArrayList()).stream()
                .filter(analyseIndicatorDTO -> analyseIndicatorDTO.getCur_q_operating_income_yoy() !=null)
                .sorted(Comparator.comparing(AnalyseIndicatorDTO::getCur_q_operating_income_yoy).reversed())
                .collect(Collectors.toList());
        for (AnalyseIndicatorDTO indicatorDTO : filterIndicatorDTOList) {
            try {
                Field[] fields = AnalyseIndicatorDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indicatorDTO));
                StringBuilder indicatorBuilder = new StringBuilder();
                for (Field field : fields) {
                    Object value = jsonObject.get(field.getName());
                    indicatorBuilder.append(",").append(value);
                }
                strIndicatorList.add(indicatorBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String filterListName = String.format(StockConstant.INDICATOR_LIST_FILTER, kLineDate, 0, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strIndicatorList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("indicatorList: " + JSON.toJSONString(strIndicatorList));
    }

    /**
     * 筛选数据
     *
     * @param indicatorDTOList
     * @return
     */
    private List<AnalyseIndicatorDTO> filterByIndicator(List<AnalyseIndicatorDTO> indicatorDTOList) {
        if (CollectionUtils.isEmpty(indicatorDTOList)) {
            return Lists.newArrayList();
        }

        List<AnalyseIndicatorDTO> analyseIndicatorDTOList = indicatorDTOList.stream()
                .filter(indicatorDTO -> indicatorDTO.getRevenue() != null)
                .filter(indicatorDTO -> indicatorDTO.getKLineSize() != null && indicatorDTO.getKLineSize() > 300)
                .filter(indicatorDTO -> indicatorDTO.getMarket_capital() != null && indicatorDTO.getMarket_capital() >= 30)
                .filter(indicatorDTO -> indicatorDTO.getGw_ia_assert_rate() != null && indicatorDTO.getGw_ia_assert_rate() <= 0.3)
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
                                || ind_name.contains("环保") || ind_name.contains("建筑") || ind_name.contains("房地产")
                                || ind_name.contains("保险") || ind_name.contains("纺织") || ind_name.contains("服装") ) {
                            return false;
                        }

                        // 医药行业，过滤毛利率和净利率差过高的数据
                        if (ind_name.contains("医") || ind_name.contains("药")) {
                            if (indicatorDTO.getGross_margin_rate() != null && indicatorDTO.getNet_selling_rate() != null) {
                                double rate_diff_value = indicatorDTO.getGross_margin_rate() - indicatorDTO.getNet_selling_rate();
                                return rate_diff_value < 0.4;
                            }
                        }
                    }

                    return true;
                })
                .filter(indicatorDTO -> {
                    // ROE_TTM >12%, 或去现后ROE >16%
                    if (indicatorDTO.getAvg_roe_ttm() != null && indicatorDTO.getAvg_roe_ttm() >= 0.12 && indicatorDTO.getAvg_roe_ttm() <= 0.5) {
                        return true;
                    }
                    if (indicatorDTO.getAvg_roe_ttm_v1() != null && indicatorDTO.getAvg_roe_ttm_v1() >= 0.16 && indicatorDTO.getAvg_roe_ttm_v1() < 2) {
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

                        // 存货周转天数的同比过滤规则
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
                        // 市值在50亿~100亿间
                        return indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.2
                                && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.1;
                    } else {
                        // 市值50亿以下的， 毛利率和净利率要求更高
                        return indicatorDTO.getCur_q_gross_margin_rate() != null && indicatorDTO.getCur_q_gross_margin_rate() >= 0.25
                                && indicatorDTO.getCur_q_net_selling_rate() != null && indicatorDTO.getCur_q_net_selling_rate() >= 0.15;
                    }
                })
                .collect(Collectors.toList());

        return analyseIndicatorDTOList;
    }

    /**
     * 查询指标源数据
     *
     * @param code
     * @param year
     * @param typeEnum
     * @param kLineDate
     * @return
     */
    private AnalyseIndicatorElement queryIndicatorElement(String code, Integer year, FinanceReportTypeEnum typeEnum
            , String kLineDate) {
        AnalyseIndicatorElement indicatorElement = new AnalyseIndicatorElement();
        indicatorElement.setCode(code);
        indicatorElement.setReportYear(year);
        indicatorElement.setReportType(typeEnum.getCode());
        indicatorElement.setKLineDate(kLineDate);

        LocalDataManager localDataManager = new LocalDataManagerImpl();
        CompanyDTO companyDTO = localDataManager.getLocalCompanyDTO(code);
        indicatorElement.setCompanyDTO(companyDTO);

        this.assembleFinanceElement(indicatorElement, code, year, typeEnum);

        this.assembleKLineElement(indicatorElement, code, kLineDate);

        this.assembleHolderIncreaseElement(indicatorElement, code, kLineDate);

        this.assembleFreeShareElement(indicatorElement, code, kLineDate);

        return indicatorElement;
    }

    /**
     * 财务分析源数据
     *
     * @param indicatorElement
     * @param code
     * @param year
     * @param typeEnum
     */
    private void assembleFinanceElement(AnalyseIndicatorElement indicatorElement, String code, Integer year, FinanceReportTypeEnum typeEnum) {
        if (indicatorElement == null || year == null || typeEnum == null) {
            return;
        }

        LocalDataManager localDataManager = new LocalDataManagerImpl();
        XueQiuStockBalanceDTO balanceDTO = localDataManager.getLocalBalanceDTO(code, year, typeEnum);
        if (balanceDTO != null) {
            indicatorElement.setCurBalanceDTO(balanceDTO);
        }

        XueQiuStockBalanceDTO lastSamePeriodBalanceDTO = localDataManager.getLocalBalanceDTO(code, year - 1, typeEnum);
        if (lastSamePeriodBalanceDTO != null) {
            indicatorElement.setLastSamePeriodBalanceDTO(lastSamePeriodBalanceDTO);
        }

        XueQiuStockBalanceDTO lastYearBalanceDTO = localDataManager.getLocalBalanceDTO(code, year - 1, FinanceReportTypeEnum.ALL_YEAR);
        if (lastYearBalanceDTO != null) {
            indicatorElement.setLastYearBalanceDTO(lastYearBalanceDTO);
        }

        XueQiuStockIncomeDTO incomeDTO = localDataManager.getLocalIncomeDTO(code, year, typeEnum);
        if (incomeDTO != null) {
            indicatorElement.setCurIncomeDTO(incomeDTO);
        }

        XueQiuStockIncomeDTO lastYearIncomeDTO = localDataManager.getLocalIncomeDTO(code, year - 1, FinanceReportTypeEnum.ALL_YEAR);
        if (lastYearIncomeDTO != null) {
            indicatorElement.setLastYearIncomeDTO(lastYearIncomeDTO);
        }

        XueQiuStockCashFlowDTO cashFlowDTO = localDataManager.getLocalCashFlowDTO(code, year, typeEnum);
        if (cashFlowDTO != null) {
            indicatorElement.setCurCashFlowDTO(cashFlowDTO);
        }

        XueQiuStockCashFlowDTO lastYearCashFlowDTO = localDataManager.getLocalCashFlowDTO(code, year - 1, FinanceReportTypeEnum.ALL_YEAR);
        if (lastYearCashFlowDTO != null) {
            indicatorElement.setLastYearCashFlowDTO(lastYearCashFlowDTO);
        }

        XueQiuStockIndicatorDTO indicatorDTO = localDataManager.getLocalXqIndicatorDTO(code, year, typeEnum);
        if (indicatorDTO != null) {
            indicatorElement.setCurIndicatorDTO(indicatorDTO);
        }

        XueQiuStockIndicatorDTO lastPeriodIndicatorDTO = localDataManager.getLocalXqIndicatorDTO(code, year - 1, typeEnum);
        if (lastPeriodIndicatorDTO != null) {
            indicatorElement.setLastPeriodIndicatorDTO(lastPeriodIndicatorDTO);
        }

        List<ImmutablePair<Integer, FinanceReportTypeEnum>> immutablePairList = Lists.newArrayList();
        String curQuarterType = StringUtils.EMPTY;
        Integer lastQuarterYear = 0;
        String lastQuarterType = StringUtils.EMPTY;
        if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_1.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_1.getCode();
            lastQuarterYear = year - 1;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_4.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 2, FinanceReportTypeEnum.SINGLE_Q_4));
        } else if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.HALF_YEAR.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_2.getCode();
            lastQuarterYear = year;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_1.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_1));
        } else if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.QUARTER_3.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_3.getCode();
            lastQuarterYear = year;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_2.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_2));
        } else if (Objects.equals(typeEnum.getCode(), FinanceReportTypeEnum.ALL_YEAR.getCode())) {
            curQuarterType = FinanceReportTypeEnum.SINGLE_Q_4.getCode();
            lastQuarterYear = year;
            lastQuarterType = FinanceReportTypeEnum.SINGLE_Q_3.getCode();

            immutablePairList = Arrays.asList(
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_3),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_2),
                    new ImmutablePair<>(year, FinanceReportTypeEnum.SINGLE_Q_1),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_4),
                    new ImmutablePair<>(year - 1, FinanceReportTypeEnum.SINGLE_Q_3));
        }

        List<QuarterIncomeDTO> quarterIncomeDTOList = localDataManager.getLocalQuarterIncomeDTO(code, immutablePairList);
        if (CollectionUtils.isNotEmpty(quarterIncomeDTOList)) {
            indicatorElement.setQuarterIncomeDTOList(quarterIncomeDTOList);

            for (QuarterIncomeDTO quarterIncomeDTO : quarterIncomeDTOList) {
                if (Objects.equals(quarterIncomeDTO.getReport_year(), year)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), curQuarterType)) {
                    indicatorElement.setCurQuarterIncomeDTO(quarterIncomeDTO);
                }

                if (Objects.equals(quarterIncomeDTO.getReport_year(), year - 1)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), curQuarterType)) {
                    indicatorElement.setLastYearQuarterIncomeDTO(quarterIncomeDTO);
                }

                if (Objects.equals(quarterIncomeDTO.getReport_year(), lastQuarterYear)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), lastQuarterType)) {
                    indicatorElement.setLastPeriodQuarterIncomeDTO(quarterIncomeDTO);
                }

                if (Objects.equals(quarterIncomeDTO.getReport_year(), lastQuarterYear -1)
                        && Objects.equals(quarterIncomeDTO.getReport_type(), lastQuarterType)) {
                    indicatorElement.setLastYearAndLastQuarterIncomeDTO(quarterIncomeDTO);
                }
            }
        }

    }

    /**
     * k线源数据
     *
     * @param indicatorElement
     * @param code
     * @param kLineDate
     */
    private void assembleKLineElement(AnalyseIndicatorElement indicatorElement, String code, String kLineDate) {
        if (indicatorElement == null || StringUtils.isBlank(code) || StringUtils.isBlank(kLineDate)) {
            return;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime kLineDateTime = LocalDate.parse(kLineDate, df).atStartOfDay();

        LocalDataManager localDataManager = new LocalDataManagerImpl();
        List<XueQiuStockKLineDTO> kLineDTOList = localDataManager.getLocalKLineList(code, kLineDateTime, KLineTypeEnum.DAY, 1000);
        if (CollectionUtils.isNotEmpty(kLineDTOList)) {
            indicatorElement.setKLineDTOList(kLineDTOList);
        }


        LocalDateTime oneMonthDateTime = kLineDateTime.plusMonths(1);
        List<XueQiuStockKLineDTO> oneMonthKLineDTOList = localDataManager.getLocalKLineList(code, oneMonthDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(oneMonthKLineDTOList)) {
            Long oneMonthKLineTimestamp = oneMonthKLineDTOList.get(0).getTimestamp();
            long oneMonthTimestamp = oneMonthDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(oneMonthTimestamp -oneMonthKLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setOneMonthKLineDTO(oneMonthKLineDTOList.get(0));
            }
        }

        LocalDateTime threeMonthDateTime = kLineDateTime.plusMonths(3);
        List<XueQiuStockKLineDTO> threeMonthKLineDTOList = localDataManager.getLocalKLineList(code, threeMonthDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(threeMonthKLineDTOList)) {
            Long threeMonthKLineTimestamp = threeMonthKLineDTOList.get(0).getTimestamp();
            long threeMonthTimestamp = threeMonthDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(Math.abs(threeMonthTimestamp -threeMonthKLineTimestamp) <= 7 *24 *3600 *1000){
                indicatorElement.setThreeMonthKLineDTO(threeMonthKLineDTOList.get(0));
            }
        }

        LocalDateTime halfYearDateTime = kLineDateTime.plusMonths(6);
        List<XueQiuStockKLineDTO> halfYearKLineDTOList = localDataManager.getLocalKLineList(code, halfYearDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(halfYearKLineDTOList)) {
            Long halfYearKLineTimestamp = halfYearKLineDTOList.get(0).getTimestamp();
            long halfYearTimestamp = halfYearDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (Math.abs(halfYearTimestamp - halfYearKLineTimestamp) <= 7 *24 *3600 *1000) {
                indicatorElement.setHalfYearKLineDTO(halfYearKLineDTOList.get(0));
            }
        }

        LocalDateTime oneYearDateTime = kLineDateTime.plusYears(1);
        List<XueQiuStockKLineDTO> oneYearKLineDTOList = localDataManager.getLocalKLineList(code, oneYearDateTime, KLineTypeEnum.DAY, 1);
        if (CollectionUtils.isNotEmpty(oneYearKLineDTOList)) {
            Long oneYearKLineTimestamp = oneYearKLineDTOList.get(0).getTimestamp();
            long oneYearTimestamp = oneYearDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (Math.abs(oneYearTimestamp - oneYearKLineTimestamp) <= 7 *24 *3600 *1000) {
                indicatorElement.setOneYearKLineDTO(oneYearKLineDTOList.get(0));
            }
        }

    }

    /**
     * 6月内公告的增减持数据
     *
     * @param indicatorElement
     * @param code
     */
    private void assembleHolderIncreaseElement(AnalyseIndicatorElement indicatorElement, String code, String kLineDate) {
        if (indicatorElement == null || StringUtils.isBlank(code)) {
            return;
        }

        LocalDateTime startDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(-180).atStartOfDay();
        LocalDateTime endDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(91).atStartOfDay();

        DongChaiDataManager manager = new DongChaiDataManagerImpl();
        DongChaiHolderIncreaseDTO holderIncreaseDTO = manager.getMaxHolderIncreaseDTO(code, startDateTime, endDateTime);
        if (holderIncreaseDTO != null && (holderIncreaseDTO.getChange_num() >50 || holderIncreaseDTO.getAfter_change_rate() >0.05)) {
            indicatorElement.setHolderIncreaseDTO(holderIncreaseDTO);
        }
    }

    /**
     * 解禁数据（前3个月，后6个月）
     *
     * @param indicatorElement
     * @param code
     */
    private void assembleFreeShareElement(AnalyseIndicatorElement indicatorElement, String code, String kLineDate) {
        if (indicatorElement == null || StringUtils.isBlank(code) || StringUtils.isBlank(kLineDate)) {
            return;
        }

        LocalDateTime startDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(-90).atStartOfDay();
        LocalDateTime endDateTime = DateUtil.parseLocalDate(kLineDate, DateUtil.DATE_FORMAT).plusDays(180).atStartOfDay();

        DongChaiDataManager manager = new DongChaiDataManagerImpl();
        DongChaiFreeShareDTO freeShareDTO = manager.getMaxFreeShareDTO(code, startDateTime, endDateTime);
        if (freeShareDTO != null && (freeShareDTO.getFree_share_num() >100 || freeShareDTO.getTotal_ratio() >0.1)) {
            indicatorElement.setFreeShareDTO(freeShareDTO);
        }
    }


    @Override
    public void queryAndSaveNorthHoldShares(List<String> stockCodeList, String queryDate) {
        if (CollectionUtils.isEmpty(stockCodeList) || StringUtils.isBlank(queryDate)) {
            return;
        }

        // 查询最新的沪港通持股数据
        LocalDataManager localDataManager = new LocalDataManagerImpl();
        List<DongChaiNorthHoldShareDTO> holdShareDTOList = localDataManager.queryNorthHoldShares(stockCodeList, queryDate);
        if (CollectionUtils.isEmpty(holdShareDTOList)) {
            return;
        }

        // 个股数据生成结果
        List<String> strHoldSharesList = Lists.newArrayList();
        strHoldSharesList.add(HOLD_SHARE_HEADER);
        for (DongChaiNorthHoldShareDTO holdShareDTO : holdShareDTOList) {
            try {
                // 公司信息
                CompanyDTO companyDTO = localDataManager.getLocalCompanyDTO(holdShareDTO.getCode());
                if(companyDTO !=null){
                    holdShareDTO.setIndName(companyDTO.getInd_name());
                }

                // 沪港通增减持数量和比例
                this.queryHoldIncreaseShares(holdShareDTO);

                // 格式化数据
                StockCalculateUtils.formatNorthHoldShareDTO(holdShareDTO);

                Field[] fields = DongChaiNorthHoldShareDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(holdShareDTO));
                StringBuilder strHoldSharesBuilder = new StringBuilder();
                for (Field field : fields) {
                    Object value = jsonObject.get(field.getName());
                    strHoldSharesBuilder.append(",").append(value);
                }
                strHoldSharesList.add(strHoldSharesBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String resultFileName = String.format(StockConstant.LATEST_HOLD_SHARES_FILE, queryDate, strDateTime);
            FileUtils.writeLines(new File(resultFileName), "UTF-8", strHoldSharesList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndSaveIndustryHoldShares(String queryDate) {
        if(StringUtils.isBlank(queryDate)){
            return ;
        }

        // 查询最新的行业持股数据
        LocalDataManager localDataManager = new LocalDataManagerImpl();
        List<DongChaiIndustryHoldShareDTO> industryHoldShareDTOList = localDataManager.queryIndustryHoldShareDTO(queryDate);
        if (CollectionUtils.isEmpty(industryHoldShareDTOList)) {
            return;
        }

        // 行业数据生成结果
        List<String> strHoldSharesList = Lists.newArrayList();
        strHoldSharesList.add(IND_HOLD_SHARE_HEADER);
        for (DongChaiIndustryHoldShareDTO indHoldShareDTO : industryHoldShareDTOList) {
            try {
                // by行业持股的比例变化
                this.queryIndustryHoldShareRatioChange(indHoldShareDTO);

                // 格式化数据
                StockCalculateUtils.formatIndNorthHoldShareDTO(indHoldShareDTO);

                Field[] fields = DongChaiIndustryHoldShareDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(indHoldShareDTO));
                StringBuilder strHoldSharesBuilder = new StringBuilder();
                for (Field field : fields) {
                    Object value = jsonObject.get(field.getName());
                    strHoldSharesBuilder.append(",").append(value);
                }
                strHoldSharesList.add(strHoldSharesBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String resultFileName = String.format(StockConstant.LATEST_IND_HOLD_SHARES_FILE, queryDate, strDateTime);
            FileUtils.writeLines(new File(resultFileName), "UTF-8", strHoldSharesList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 补全增持数量和比例
     *
     * @param holdShareDTO
     */
    private void queryHoldIncreaseShares(DongChaiNorthHoldShareDTO holdShareDTO){
        if(holdShareDTO == null || StringUtils.isBlank(holdShareDTO.getCode()) || StringUtils.isBlank(holdShareDTO.getTradeDate())){
            return;
        }

        // 历史沪港通持股数据
        LocalDataManager localDataManager =new LocalDataManagerImpl();
        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
        List<DongChaiNorthHoldShareDTO> historyHoldShareDTOList = localDataManager.queryLocalNorthHoldShareDTOs(holdShareDTO.getCode(), tradeDateTime, 400);
        if(CollectionUtils.isEmpty(historyHoldShareDTOList)){
            return;
        }

        // 7日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay7 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 7);
        if(holdShareDTODay7 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay7.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_7(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay7.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay7.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_7(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay7.getTotalSharesRatio(), 2));
            }
        }

        // 30日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay30 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 30);
        if(holdShareDTODay30 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay30.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_30(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay30.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay30.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_30(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay30.getTotalSharesRatio(), 2));
            }
        }

        // 90日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay90 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 90);
        if(holdShareDTODay90 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay90.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_90(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay90.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay90.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_90(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay90.getTotalSharesRatio(), 2));
            }
        }

        // 180日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay180 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 180);
        if(holdShareDTODay180 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay180.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_180(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay180.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay180.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_180(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay180.getTotalSharesRatio(), 2));
            }
        }

        // 360日前的持股数据
        DongChaiNorthHoldShareDTO holdShareDTODay360 = this.getBeforeNorthHoldShareDTO(holdShareDTO, historyHoldShareDTOList, 360);
        if(holdShareDTODay360 !=null){
            if(holdShareDTO.getHoldShares() !=null && holdShareDTODay360.getHoldShares() !=null){
                holdShareDTO.setIncreaseShares_360(NumberUtil.format(holdShareDTO.getHoldShares() - holdShareDTODay360.getHoldShares(), 1));
            }
            if(holdShareDTO.getTotalSharesRatio() !=null && holdShareDTODay360.getTotalSharesRatio() !=null){
                holdShareDTO.setIncreaseRatio_360(NumberUtil.format(holdShareDTO.getTotalSharesRatio() - holdShareDTODay360.getTotalSharesRatio(), 2));
            }
        }

    }

    /**
     * 获取N天前的最近的北上持股数据
     *
     * @param holdShareDTO
     * @param historyHoldShareDTOList
     * @param beforeDayNum
     * @return
     */
    private DongChaiNorthHoldShareDTO getBeforeNorthHoldShareDTO(DongChaiNorthHoldShareDTO holdShareDTO
            , List<DongChaiNorthHoldShareDTO> historyHoldShareDTOList, Integer beforeDayNum){
        if(holdShareDTO ==null || StringUtils.isBlank(holdShareDTO.getTradeDate())){
            return null;
        }
        if(CollectionUtils.isEmpty(historyHoldShareDTOList) || beforeDayNum ==null){
            return null;
        }

        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);

        LocalDateTime beforeDateTime = tradeDateTime.plusDays(-beforeDayNum);
        DongChaiNorthHoldShareDTO beforeHoldShareDTO = historyHoldShareDTOList.stream()
                .filter(hisHoldShareDTO -> StringUtils.isNotBlank(hisHoldShareDTO.getTradeDate()))
                .filter(hisHoldShareDTO -> {
                    LocalDateTime hisDataTime = DateUtil.parseLocalDateTime(hisHoldShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
                    return hisDataTime.isBefore(beforeDateTime) || hisDataTime.isEqual(beforeDateTime);
                })
                .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                .findFirst()
                .orElse(null);

        return beforeHoldShareDTO;
    }

    /**
     * by行业持股比例变化
     *
     * @param holdShareDTO
     */
    private void queryIndustryHoldShareRatioChange(DongChaiIndustryHoldShareDTO holdShareDTO){
        if(holdShareDTO ==null || StringUtils.isBlank(holdShareDTO.getTradeDate())){
            return;
        }

        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
        if(tradeDateTime ==null){
            return;
        }

        LocalDataManager localDataManager =new LocalDataManagerImpl();

        // 近7天的变化
        LocalDateTime queryDateTime7 = tradeDateTime.plusDays(-7);
        String queryDate7 = DateUtil.formatLocalDateTime(queryDateTime7, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO7 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate7);
        if(queryHoldShareDTO7 !=null && queryHoldShareDTO7.getIndustryRatio() !=null){
            double ratio_change_7 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO7.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_7(ratio_change_7);
        }

        // 近30天的变化
        LocalDateTime queryDateTime30 = tradeDateTime.plusDays(-30);
        String queryDate30 = DateUtil.formatLocalDateTime(queryDateTime30, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO30 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate30);
        if(queryHoldShareDTO30 !=null && queryHoldShareDTO30.getIndustryRatio() !=null){
            double ratio_change_30 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO30.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_30(ratio_change_30);
        }

        // 近90天的变化
        LocalDateTime queryDateTime90 = tradeDateTime.plusDays(-90);
        String queryDate90 = DateUtil.formatLocalDateTime(queryDateTime90, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO90 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate90);
        if(queryHoldShareDTO90 !=null && queryHoldShareDTO90.getIndustryRatio() !=null){
            double ratio_change_90 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO90.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_90(ratio_change_90);
        }

        // 近180天的变化
        LocalDateTime queryDateTime180 = tradeDateTime.plusDays(-180);
        String queryDate180 = DateUtil.formatLocalDateTime(queryDateTime180, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO180 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate180);
        if(queryHoldShareDTO180 !=null && queryHoldShareDTO180.getIndustryRatio() !=null){
            double ratio_change_180 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO180.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_180(ratio_change_180);
        }

        // 近360天的变化
        LocalDateTime queryDateTime360 = tradeDateTime.plusDays(-360);
        String queryDate360 = DateUtil.formatLocalDateTime(queryDateTime360, DateUtil.DATE_TIME_FORMAT);
        DongChaiIndustryHoldShareDTO queryHoldShareDTO360 = localDataManager.queryIndustryHoldShareDTO(holdShareDTO.getIndName(), queryDate360);
        if(queryHoldShareDTO360 !=null && queryHoldShareDTO360.getIndustryRatio() !=null){
            double ratio_change_360 = holdShareDTO.getIndustryRatio() - queryHoldShareDTO360.getIndustryRatio();
            holdShareDTO.setInd_ratio_chg_360(ratio_change_360);
        }
    }

}
