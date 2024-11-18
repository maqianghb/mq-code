package com.example.mq.wrapper.stock.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.DateUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.enums.KLineTypeEnum;
import com.example.mq.wrapper.stock.manager.DongChaiDataManager;
import com.example.mq.wrapper.stock.manager.LocalDataManager;
import com.example.mq.wrapper.stock.manager.XueQiuStockManager;
import com.example.mq.wrapper.stock.model.*;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFinanceNoticeDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiIndustryHoldShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class LocalDataManagerImpl implements LocalDataManager {

    private static final String FINANCE_NOTICE_HEADER ="预告时间,报告期,编码,名称,行业,预告指标,预告类型,预告值(亿),预告值(亿)" +
            ",同比变动,同比变动,环比变动,环比变动,上年同期值(亿),变动原因";
    private static final String IND_FINANCE_NOTICE_HEADER ="预告时间,报告期,行业,预告指标,预告类型,行业股票数,预告股票数,占行业比例";
    private static final String HOLDER_INCREASE_HEADER ="编码,名称,预告时间,增减类型,增减数量(万股),减持开始时间,减持结束时间" +
            ",变动比例(%),变动后持股比例(%)";

    private static List<CompanyDTO> companyDTOList =Lists.newArrayList();

    private static List<XueQiuStockBalanceDTO> balanceDTOList =Lists.newArrayList();
    private static List<XueQiuStockIncomeDTO> incomeDTOList =Lists.newArrayList();
    private static List<XueQiuStockCashFlowDTO> cashFlowDTOList =Lists.newArrayList();
    private static List<XueQiuStockIndicatorDTO> xqIndicatorDTOList =Lists.newArrayList();
    private static List<QuarterIncomeDTO> quarterIncomeDTOList =Lists.newArrayList();


    @Override
    public void queryAndSaveCompanyDTO(){
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();

        // 公司信息
        try {
            List<String> strCompanyDTOList = stockCodeList.stream()
                    .map(stockCode -> {
                        CompanyDTO companyDTO = this.getLocalCompanyDTO(stockCode);
                        if(companyDTO !=null){
                            return companyDTO;
                        }

                        return xueQiuStockManager.queryCompanyDTO(stockCode);
                    })
                    .filter(companyDTO -> companyDTO != null)
                    .sorted(Comparator.comparing(CompanyDTO::getCode))
                    .map(balanceDTO -> JSON.toJSONString(balanceDTO))
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(strCompanyDTOList)){
                return ;
            }

            FileUtils.writeLines(new File(StockConstant.COMPANY_LIST), strCompanyDTOList, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndUpdateKLineList() {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();

        // 查询
        for(String stockCode : stockCodeList){
            String kLineFileName =String.format(StockConstant.KLINE_LIST_DAY, stockCode);
            try {
                // 查询新的数据
                List<XueQiuStockKLineDTO> tmpKLineDTOList = xueQiuStockManager.queryKLineList(stockCode, "day"
                        , System.currentTimeMillis(), StockConstant.KLINE_DAY_COUNT);
                if(CollectionUtils.isEmpty(tmpKLineDTOList)){
                    continue;
                }

                // 补全Ma20指标
                this.assembleMa20Value(tmpKLineDTOList);
                // 补全Ma60指标
                this.assembleMa60Value(tmpKLineDTOList);
                // 补全Ma250指标
                this.assembleMa250Value(tmpKLineDTOList);
                // 补全Ma1000指标
                this.assembleMa1000Value(tmpKLineDTOList);

                // 本地的已有数据
                Map<Long, XueQiuStockKLineDTO> kLineDTOMap = Maps.newHashMap();
                File klineFile = new File(kLineFileName);
                if(klineFile.exists()){
                    List<String> strList =FileUtils.readLines(klineFile, Charset.forName("UTF-8"));
                    kLineDTOMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                            .map(str -> JSON.parseObject(str, XueQiuStockKLineDTO.class))
                            .collect(Collectors.toMap(XueQiuStockKLineDTO::getTimestamp, val -> val, (val1, val2) -> val2));
                }

                // 更新数据
                for(XueQiuStockKLineDTO tmpKLineDTO : tmpKLineDTOList){
                    kLineDTOMap.putIfAbsent(tmpKLineDTO.getTimestamp(), tmpKLineDTO);
                }

                List<String> strKLineList = kLineDTOMap.values().stream()
                        .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                        .map(dto -> JSON.toJSONString(dto))
                        .collect(Collectors.toList());
                FileUtils.writeLines(klineFile, strKLineList, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void queryAndUpdateBalanceData() {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();

        // 资产负债数据
        try {
            // 最新的数据
            List<XueQiuStockBalanceDTO> balanceDTOList = stockCodeList.stream()
                    .map(stockCode -> xueQiuStockManager.queryBalanceList(stockCode, StockConstant.FINANCE_REPORT_COUNT))
                    .filter(list -> CollectionUtils.isNotEmpty(list))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(balanceDTOList)){
                return;
            }

            // 本地的已有数据
            Map<String, XueQiuStockBalanceDTO> keyAndBalanceDTOMap = Maps.newHashMap();
            File balanceFile = new File(StockConstant.BALANCE_LIST);
            if(balanceFile.exists()){
                List<String> strList =FileUtils.readLines(balanceFile, Charset.forName("UTF-8"));
                keyAndBalanceDTOMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockBalanceDTO.class))
                        .filter(balanceDTO -> StringUtils.isNotBlank(balanceDTO.getCode())
                                && balanceDTO.getReport_year() !=null && StringUtils.isNoneBlank(balanceDTO.getReport_type()))
                        .collect(Collectors.toMap(balanceDTO -> new StringBuilder().append(balanceDTO.getCode())
                                .append("_").append(balanceDTO.getReport_year())
                                .append("_").append(balanceDTO.getReport_type())
                                .toString(), val -> val, (val1, val2) -> val2));
            }

            // 更新数据
            for(XueQiuStockBalanceDTO balanceDTO : balanceDTOList){
                if(StringUtils.isNotBlank(balanceDTO.getCode()) && balanceDTO.getReport_year() !=null && StringUtils.isNoneBlank(balanceDTO.getReport_type())){
                    String key =new StringBuilder().append(balanceDTO.getCode())
                            .append("_").append(balanceDTO.getReport_year())
                            .append("_").append(balanceDTO.getReport_type())
                            .toString();
                    keyAndBalanceDTOMap.putIfAbsent(key, balanceDTO);
                }
            }

            List<String> strBalanceDTOList = keyAndBalanceDTOMap.values().stream()
                    .sorted(Comparator.comparing(XueQiuStockBalanceDTO::getCode)
                            .thenComparing(XueQiuStockBalanceDTO::getReport_year).reversed())
                    .map(balanceDTO -> JSON.toJSONString(balanceDTO))
                    .collect(Collectors.toList());

            FileUtils.writeLines(balanceFile, strBalanceDTOList, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndUpdateIncomeData() {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();
        try {
            // 最新数据
            List<XueQiuStockIncomeDTO> incomeDTOList = stockCodeList.stream()
                    .map(stockCode -> xueQiuStockManager.queryIncomeList(stockCode, StockConstant.FINANCE_REPORT_COUNT))
                    .filter(list -> CollectionUtils.isNotEmpty(list))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(incomeDTOList)){
                return;
            }

            // 本地的已有数据
            Map<String, XueQiuStockIncomeDTO> keyAndIncomeDTOMap = Maps.newHashMap();
            File incomeFile = new File(StockConstant.INCOME_LIST);
            if(incomeFile.exists()){
                List<String> strList =FileUtils.readLines(incomeFile, Charset.forName("UTF-8"));
                keyAndIncomeDTOMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockIncomeDTO.class))
                        .filter(incomeDTO -> StringUtils.isNotBlank(incomeDTO.getCode())
                                && incomeDTO.getReport_year() !=null && StringUtils.isNoneBlank(incomeDTO.getReport_type()))
                        .collect(Collectors.toMap(incomeDTO -> new StringBuilder().append(incomeDTO.getCode())
                                .append("_").append(incomeDTO.getReport_year())
                                .append("_").append(incomeDTO.getReport_type())
                                .toString(), val -> val, (val1, val2) -> val2));
            }

            // 更新数据
            for(XueQiuStockIncomeDTO incomeDTO : incomeDTOList){
                if(StringUtils.isNotBlank(incomeDTO.getCode()) && incomeDTO.getReport_year() !=null && StringUtils.isNoneBlank(incomeDTO.getReport_type())){
                    String key =new StringBuilder().append(incomeDTO.getCode())
                            .append("_").append(incomeDTO.getReport_year())
                            .append("_").append(incomeDTO.getReport_type())
                            .toString();
                    keyAndIncomeDTOMap.putIfAbsent(key, incomeDTO);
                }
            }
            List<String> strIncomeDTOList = keyAndIncomeDTOMap.values().stream()
                    .sorted(Comparator.comparing(XueQiuStockIncomeDTO::getCode)
                            .thenComparing(XueQiuStockIncomeDTO::getReport_year).reversed())
                    .map(incomeDTO -> JSON.toJSONString(incomeDTO))
                    .collect(Collectors.toList());

            FileUtils.writeLines(incomeFile, strIncomeDTOList, false);

            // 单季利润数据
            this.getAndSaveQuarterIncome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndUpdateCashFlowData() {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();
        try {
            // 最新的数据
            List<XueQiuStockCashFlowDTO> cashFlowDTOList = stockCodeList.stream()
                    .map(stockCode -> xueQiuStockManager.queryCashFlowList(stockCode, StockConstant.FINANCE_REPORT_COUNT))
                    .filter(list -> CollectionUtils.isNotEmpty(list))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(cashFlowDTOList)){
                return;
            }

            // 本地的已有数据
            Map<String, XueQiuStockCashFlowDTO> keyAndCashFlowDTOMap = Maps.newHashMap();
            File cashFlowFile = new File(StockConstant.CASH_FLOW_LIST);
            if(cashFlowFile.exists()){
                List<String> strList =FileUtils.readLines(cashFlowFile, Charset.forName("UTF-8"));
                keyAndCashFlowDTOMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockCashFlowDTO.class))
                        .filter(cashFlowDTO -> StringUtils.isNotBlank(cashFlowDTO.getCode()) && cashFlowDTO.getReport_year() !=null && StringUtils.isNoneBlank(cashFlowDTO.getReport_type()))
                        .collect(Collectors.toMap(cashFlowDTO -> new StringBuilder().append(cashFlowDTO.getCode())
                                .append("_").append(cashFlowDTO.getReport_year())
                                .append("_").append(cashFlowDTO.getReport_type())
                                .toString(), val -> val, (val1, val2) -> val2));
            }

            // 更新数据
            for(XueQiuStockCashFlowDTO cashFlowDTO : cashFlowDTOList){
                if(StringUtils.isNotBlank(cashFlowDTO.getCode()) && cashFlowDTO.getReport_year() !=null && StringUtils.isNoneBlank(cashFlowDTO.getReport_type())){
                    String key =new StringBuilder().append(cashFlowDTO.getCode())
                            .append("_").append(cashFlowDTO.getReport_year())
                            .append("_").append(cashFlowDTO.getReport_type())
                            .toString();
                    keyAndCashFlowDTOMap.putIfAbsent(key, cashFlowDTO);
                }
            }

            List<String> strCashFlowDTOList = keyAndCashFlowDTOMap.values().stream()
                    .sorted(Comparator.comparing(XueQiuStockCashFlowDTO::getCode)
                            .thenComparing(XueQiuStockCashFlowDTO::getReport_year).reversed())
                    .map(cashFlowDTO -> JSON.toJSONString(cashFlowDTO))
                    .collect(Collectors.toList());

            FileUtils.writeLines(cashFlowFile, strCashFlowDTOList, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndUpdateIndicatorData() {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return;
        }

        XueQiuStockManager xueQiuStockManager =new XueQiuStockManagerImpl();
        try {
            // 最新数据
            List<XueQiuStockIndicatorDTO> indicatorDTOList = stockCodeList.stream()
                    .map(stockCode -> xueQiuStockManager.queryIndicatorList(stockCode, StockConstant.FINANCE_REPORT_COUNT))
                    .filter(list -> CollectionUtils.isNotEmpty(list))
                    .flatMap(list -> list.stream())
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(indicatorDTOList)) {
                return;
            }

            // 本地的已有数据
            Map<String, XueQiuStockIndicatorDTO> keyAndIndicatorDTOMap = Maps.newHashMap();
            File indicatorFile = new File(StockConstant.INDICATOR_LIST_XQ);
            if (indicatorFile.exists()) {
                List<String> strList = FileUtils.readLines(indicatorFile, Charset.forName("UTF-8"));
                keyAndIndicatorDTOMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockIndicatorDTO.class))
                        .filter(indicatorDTO -> StringUtils.isNotBlank(indicatorDTO.getCode()) && indicatorDTO.getReport_year() != null && StringUtils.isNoneBlank(indicatorDTO.getReport_type()))
                        .collect(Collectors.toMap(indicatorDTO -> new StringBuilder().append(indicatorDTO.getCode())
                                .append("_").append(indicatorDTO.getReport_year())
                                .append("_").append(indicatorDTO.getReport_type())
                                .toString(), val -> val, (val1, val2) -> val2));
            }

            // 更新数据
            for (XueQiuStockIndicatorDTO indicatorDTO : indicatorDTOList) {
                if (StringUtils.isNotBlank(indicatorDTO.getCode()) && indicatorDTO.getReport_year() != null && StringUtils.isNoneBlank(indicatorDTO.getReport_type())) {
                    String key =new StringBuilder().append(indicatorDTO.getCode())
                            .append("_").append(indicatorDTO.getReport_year())
                            .append("_").append(indicatorDTO.getReport_type())
                            .toString();
                    keyAndIndicatorDTOMap.putIfAbsent(key, indicatorDTO);
                }
            }

            List<String> strIndicatorDTOList = keyAndIndicatorDTOMap.values().stream()
                    .sorted(Comparator.comparing(XueQiuStockIndicatorDTO::getCode)
                            .thenComparing(XueQiuStockIndicatorDTO::getReport_year).reversed())
                    .map(indicatorDTO -> JSON.toJSONString(indicatorDTO))
                    .collect(Collectors.toList());

            FileUtils.writeLines(indicatorFile, strIndicatorDTOList, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndSaveFinanceNotice(String reportDate) {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return ;
        }

        Map<String, String> codeAndIndNameMap = stockCodeList.stream().map(stockCode -> this.getLocalCompanyDTO(stockCode))
                .filter(companyDTO -> companyDTO != null && StringUtils.isNotBlank(companyDTO.getInd_name()))
                .collect(Collectors.toMap(CompanyDTO::getCode, CompanyDTO::getInd_name, (val1, val2) -> val1));

        DongChaiDataManager dongChaiDataManager =new DongChaiDataManagerImpl();
        List<DongChaiFinanceNoticeDTO> noticeDTOList = dongChaiDataManager.queryFinanceNoticeDTO(reportDate);
        List<String> strNoticeList = Optional.ofNullable(noticeDTOList).orElse(Lists.newArrayList()).stream()
                .filter(noticeDTO -> stockCodeList.contains(noticeDTO.getCode()))
                .map(noticeDTO -> {
                    String indName = codeAndIndNameMap.get(noticeDTO.getCode());
                    if(StringUtils.isNotBlank(indName)){
                        noticeDTO.setIndName(indName);
                    }

                    try {
                        Field[] fields = DongChaiFinanceNoticeDTO.class.getDeclaredFields();
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(noticeDTO));
                        StringBuilder noticeBuilder = new StringBuilder();
                        for (Field field : fields) {
                            Object value = jsonObject.get(field.getName());
                            noticeBuilder.append(",").append(value);
                        }
                        return noticeBuilder.toString().substring(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(strNoticeList)){
            return;
        }

        List<String> strDataList =Lists.newArrayList();
        strDataList.add(FINANCE_NOTICE_HEADER);
        strDataList.addAll(strNoticeList);

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String filterListName =String.format(StockConstant.FINANCE_NOTICE_LIST, reportDate, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strDataList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndSaveIndFinanceNotice(String reportDate) {
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return ;
        }

        Map<String, String> codeAndIndNameMap = stockCodeList.stream().map(stockCode -> this.getLocalCompanyDTO(stockCode))
                .filter(companyDTO -> companyDTO != null && StringUtils.isNotBlank(companyDTO.getInd_name()))
                .collect(Collectors.toMap(CompanyDTO::getCode, CompanyDTO::getInd_name, (val1, val2) -> val1));

        Map<String, Integer> indNameAndCodeNumMap =Maps.newHashMap();
        for(Map.Entry<String, String> entry : codeAndIndNameMap.entrySet()){
            Integer num = indNameAndCodeNumMap.getOrDefault(entry.getValue(), 0);
            indNameAndCodeNumMap.put(entry.getValue(), num +1);
        }

        DongChaiDataManager dongChaiDataManager =new DongChaiDataManagerImpl();
        List<DongChaiFinanceNoticeDTO> allFinanceNoticeDTOList = dongChaiDataManager.queryFinanceNoticeDTO(reportDate);
        List<DongChaiFinanceNoticeDTO> financeNoticeDTOList = Optional.ofNullable(allFinanceNoticeDTOList).orElse(Lists.newArrayList()).stream()
                .filter(financeNoticeDTO -> StockConstant.FINANCE_PREDICT_INDICATOR.contains(financeNoticeDTO.getPredict_indicator()))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(financeNoticeDTOList)){
            return ;
        }

        Map<String, Integer> keyAndNoticeNumMap =Maps.newHashMap();
        for(DongChaiFinanceNoticeDTO financeNoticeDTO : financeNoticeDTOList){
            String indName = codeAndIndNameMap.get(financeNoticeDTO.getCode());
            if(StringUtils.isBlank(indName)){
                continue;
            }

            String predictIncreaseType = StockConstant.getPredictIncreaseType(financeNoticeDTO.getPredict_type());
            if(StringUtils.isBlank(predictIncreaseType)){
                continue;
            }

            String key = new StringBuilder().append(indName)
                    .append("_").append(financeNoticeDTO.getPredict_indicator())
                    .append("_").append(predictIncreaseType)
                    .toString();
            Integer num = keyAndNoticeNumMap.getOrDefault(key, 0);
            keyAndNoticeNumMap.put(key, num +1);
        }
        if(MapUtils.isEmpty(keyAndNoticeNumMap)){
            return;
        }

        List<String> strNoticeNumList = Optional.ofNullable(keyAndNoticeNumMap).orElse(Maps.newHashMap()).entrySet().stream()
                .map(keyAndNoticeNumEntry -> {
                    String[] splitList = StringUtils.split(keyAndNoticeNumEntry.getKey(), "_");
                    if(splitList ==null || splitList.length !=3){
                        return StringUtils.EMPTY;
                    }
                    String indName = splitList[0];
                    Integer codeNum = indNameAndCodeNumMap.get(indName);
                    if(codeNum ==null || codeNum ==0){
                        return StringUtils.EMPTY;
                    }

                    String strIndNoticeNum =new StringBuilder().append(financeNoticeDTOList.get(0).getNotice_date())
                            .append(",").append(financeNoticeDTOList.get(0).getReport_date())
                            .append(",").append(indName)
                            .append(",").append(splitList[1])
                            .append(",").append(splitList[2])
                            .append(",").append(indNameAndCodeNumMap.get(indName))
                            .append(",").append(keyAndNoticeNumEntry.getValue())
                            .append(",").append(NumberUtil.format((keyAndNoticeNumEntry.getValue() * 1.0)/codeNum, 3))
                            .toString();

                    return strIndNoticeNum;
                })
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(strNoticeNumList)){
            return;
        }

        List<String> strDataList =Lists.newArrayList();
        strDataList.add(IND_FINANCE_NOTICE_HEADER);
        strDataList.addAll(strNoticeNumList);

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String filterListName =String.format(StockConstant.IND_FINANCE_NOTICE_LIST, reportDate, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strDataList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndSaveHolderIncreaseList() {
        DongChaiDataManager dongChaiDataManager =new DongChaiDataManagerImpl();
        List<DongChaiHolderIncreaseDTO> increaseDTOList = dongChaiDataManager.queryHolderIncreaseList();
        if(CollectionUtils.isEmpty(increaseDTOList)){
            return ;
        }

        List<String> strDataList =Lists.newArrayList();
        strDataList.add(HOLDER_INCREASE_HEADER);
        for(DongChaiHolderIncreaseDTO increaseDTO : increaseDTOList){
            try {
                Field[] fields = DongChaiHolderIncreaseDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(increaseDTO));
                StringBuilder increaseBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName ());
                    increaseBuilder.append(",").append(value);
                }
                strDataList.add(increaseBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String filterListName =String.format(StockConstant.HOLDER_INCREASE_LIST, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strDataList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAndUpdateNorthHoldShareList(){
        List<String> stockCodeList = this.getLocalStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return ;
        }

        // 更新个股的沪港通持股数据
        DongChaiDataManager dongChaiDataManager =new DongChaiDataManagerImpl();
        for(String stockCode : stockCodeList){
            try {
                // 查询数据
                List<DongChaiNorthHoldShareDTO> tmpHoldShareDTOList = dongChaiDataManager.queryNorthHoldShareDTOList(stockCode);
                if(CollectionUtils.isEmpty(tmpHoldShareDTOList)){
                    continue;
                }

                // 本地数据
                List<String> strList =Lists.newArrayList();
                String fileName =String.format(StockConstant.NORTH_HOLD_SHARES_FILE, stockCode);
                File localFile = new File(fileName);
                if(localFile.exists()){
                    strList =FileUtils.readLines(localFile, Charset.forName("UTF-8"));
                }
                Map<String, DongChaiNorthHoldShareDTO> holdShareDTOMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, DongChaiNorthHoldShareDTO.class))
                        .collect(Collectors.toMap(DongChaiNorthHoldShareDTO::getTradeDate, val -> val, (val1, val2) -> val1));

                // 补上查询出来的数据
                for(DongChaiNorthHoldShareDTO holdShareDTO : tmpHoldShareDTOList){
                    holdShareDTOMap.putIfAbsent(holdShareDTO.getTradeDate(), holdShareDTO);
                }

                // 重新写入本地文件
                List<String> strDataList = holdShareDTOMap.values().stream()
                        .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                        .map(holdShareDTO -> JSON.toJSONString(holdShareDTO))
                        .collect(Collectors.toList());
                if(!Objects.equals(strDataList.size(), strList.size())){
                    FileUtils.writeLines(localFile, strDataList, false);
                }

                System.out.println("queryAndUpdateNorthHoldShareList end, stockCode: " + stockCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 更新行业的沪港通数据
        for(int i=0; i<100; i++){
            LocalDateTime queryDateTime = LocalDateTime.now().plusDays(-i);
            this.updateLocalIndustryHoldShares(stockCodeList, queryDateTime);
        }
    }

    @Override
    public List<String> getLocalStockCodeList(){
        try {
            List<String> strList = FileUtils.readLines(new File(StockConstant.STOCK_LIST), Charset.forName("UTF-8"));
            if(CollectionUtils.isEmpty(strList)){
                return Lists.newArrayList();
            }

            List<String> stockCodeList =Lists.newArrayList();
            for (String str : strList){
                String[] split = StringUtils.split(str, ",");
                if(split ==null || split.length <2){
                    continue;
                }

                String simpleCode =split[0].trim();
                if(simpleCode.startsWith("60")){
                    stockCodeList.add("SH" + simpleCode);
                }else if(simpleCode.startsWith("00") || simpleCode.startsWith("30")){
                    stockCodeList.add("SZ" + simpleCode);
                }
            }

            return stockCodeList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Lists.newArrayList();
    }

    @Override
    public List<String> getBlackStockCodeList(){
        List<String> stockCodeList = Lists.newArrayList();
        try {
            List<String> strList = FileUtils.readLines(new File(StockConstant.STOCK_LIST_BLACK), Charset.forName("UTF-8"));
            if(CollectionUtils.isEmpty(strList)){
                return Lists.newArrayList();
            }

            for(String str : strList){
                String[] split = StringUtils.split(str, ",");
                if(split !=null && split.length ==3){
                    stockCodeList.add(split[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    @Override
    public List<String> getWhiteStockCodeList(){
        List<String> stockCodeList = Lists.newArrayList();
        try {
            List<String> strList = FileUtils.readLines(new File(StockConstant.STOCK_LIST_WHITE), Charset.forName("UTF-8"));
            if(CollectionUtils.isEmpty(strList)){
                return Lists.newArrayList();
            }

            for(String str : strList){
                String[] split = StringUtils.split(str, ",");
                if(split !=null && split.length ==3){
                    stockCodeList.add(split[0]);
                }
            }

            return stockCodeList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    @Override
    public CompanyDTO getLocalCompanyDTO(String code){
        try {
            if(CollectionUtils.isEmpty(companyDTOList)){
                String fileName =String.format(StockConstant.COMPANY_LIST);
                List<String> strList = FileUtils.readLines(new File(fileName), Charset.forName("UTF-8"));
                companyDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, CompanyDTO.class))
                        .collect(Collectors.toList());
            }

            CompanyDTO companyDTO = Optional.ofNullable(companyDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(val -> Objects.equals(val.getCode(), code))
                    .findFirst()
                    .orElse(null);

            return companyDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<XueQiuStockKLineDTO> getLocalKLineList(String code, LocalDateTime endKLineDateTime, KLineTypeEnum typeEnum, Integer count){
        String klineListFileName = StringUtils.EMPTY;
        if(Objects.equals(typeEnum.getCode(), KLineTypeEnum.DAY.getCode())){
            klineListFileName = String.format(StockConstant.KLINE_LIST_DAY, code);
        }

        long endDateMills = endKLineDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        File file = new File(klineListFileName);
        if(!file.exists()){
            return Lists.newArrayList();
        }

        try {
            List<String> strList =FileUtils.readLines(file, Charset.forName("UTF-8"));
            List<XueQiuStockKLineDTO> kLineDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, XueQiuStockKLineDTO.class))
                    .filter(klineDTO -> klineDTO.getTimestamp() !=null && klineDTO.getTimestamp() <=endDateMills)
                    .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                    .collect(Collectors.toList());
            if(kLineDTOList.size() <=count){
                return kLineDTOList;
            }else {
                return kLineDTOList.subList(0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Lists.newArrayList();
    }

    @Override
    public XueQiuStockBalanceDTO getLocalBalanceDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            if(CollectionUtils.isEmpty(balanceDTOList)){
                String fileName =String.format(StockConstant.BALANCE_LIST);
                List<String> strList =FileUtils.readLines(new File(fileName), Charset.forName("UTF-8"));
                balanceDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockBalanceDTO.class))
                        .collect(Collectors.toList());
            }

            return Optional.ofNullable(balanceDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_year(), year))
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public XueQiuStockIncomeDTO getLocalIncomeDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            if(CollectionUtils.isEmpty(incomeDTOList)){
                String fileName =String.format(StockConstant.INCOME_LIST);
                List<String> strList =FileUtils.readLines(new File(fileName), Charset.forName("UTF-8"));
                incomeDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockIncomeDTO.class))
                        .collect(Collectors.toList());
            }

            return Optional.ofNullable(incomeDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(incomeDTO -> Objects.equals(incomeDTO.getCode(), code))
                    .filter(incomeDTO -> Objects.equals(incomeDTO.getReport_year(), year))
                    .filter(incomeDTO -> Objects.equals(incomeDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<QuarterIncomeDTO> getLocalQuarterIncomeDTO(String code, List<ImmutablePair<Integer, FinanceReportTypeEnum>> yearAndReportTypeList){
        try {
            if(CollectionUtils.isEmpty(quarterIncomeDTOList)){
                String fileName =String.format(StockConstant.INCOME_LIST_Q);
                List<String> strList =FileUtils.readLines(new File(fileName), Charset.forName("UTF-8"));
                quarterIncomeDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, QuarterIncomeDTO.class))
                        .collect(Collectors.toList());
            }

            List<QuarterIncomeDTO> tmpQuarterIncomeDTOList = Optional.ofNullable(quarterIncomeDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(IncomeDTO -> Objects.equals(IncomeDTO.getCode(), code))
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(tmpQuarterIncomeDTOList)){
                return Lists.newArrayList();
            }

            List<QuarterIncomeDTO> resultIncomeDTOList =Lists.newArrayList();
            for(QuarterIncomeDTO incomeDTO : tmpQuarterIncomeDTOList){
                for(ImmutablePair<Integer, FinanceReportTypeEnum> pair : yearAndReportTypeList){
                    Integer year =pair.getLeft();
                    FinanceReportTypeEnum reportTypeEnum =pair.getRight();

                    if(Objects.equals(incomeDTO.getReport_year(), year)
                            && Objects.equals(incomeDTO.getReport_type(), reportTypeEnum.getCode())){
                        resultIncomeDTOList.add(incomeDTO);
                    }
                }
            }

            return resultIncomeDTOList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Lists.newArrayList();
    }

    @Override
    public XueQiuStockCashFlowDTO getLocalCashFlowDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            if(CollectionUtils.isEmpty(cashFlowDTOList)){
                String fileName =String.format(StockConstant.CASH_FLOW_LIST);
                List<String> strList =FileUtils.readLines(new File(fileName), Charset.forName("UTF-8"));
                cashFlowDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockCashFlowDTO.class))
                        .collect(Collectors.toList());
            }

            return Optional.ofNullable(cashFlowDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(cashFlowDTO -> Objects.equals(cashFlowDTO.getCode(), code))
                    .filter(cashFlowDTO -> Objects.equals(cashFlowDTO.getReport_year(), year))
                    .filter(cashFlowDTO -> Objects.equals(cashFlowDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public XueQiuStockIndicatorDTO getLocalXqIndicatorDTO(String code, Integer year, FinanceReportTypeEnum typeEnum){
        try {
            if(CollectionUtils.isEmpty(xqIndicatorDTOList)){
                String fileName =String.format(StockConstant.INDICATOR_LIST_XQ);
                List<String> strList =FileUtils.readLines(new File(fileName), Charset.forName("UTF-8"));
                xqIndicatorDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, XueQiuStockIndicatorDTO.class))
                        .collect(Collectors.toList());
            }

            XueQiuStockIndicatorDTO stockIndicatorDTO = Optional.ofNullable(xqIndicatorDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(indicatorDTO -> Objects.equals(indicatorDTO.getCode(), code))
                    .filter(indicatorDTO -> Objects.equals(indicatorDTO.getReport_year(), year))
                    .filter(indicatorDTO -> Objects.equals(indicatorDTO.getReport_type(), typeEnum.getCode()))
                    .findFirst()
                    .orElse(null);

            return stockIndicatorDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询指定日期的沪港通数据
     */
    public List<DongChaiNorthHoldShareDTO> queryNorthHoldShares(List<String> stockCodeList, String queryDate){
        if(CollectionUtils.isEmpty(stockCodeList)){
            return Lists.newArrayList();
        }

        LocalDateTime queryDateTime = DateUtil.parseLocalDate(queryDate, DateUtil.DATE_FORMAT).atStartOfDay();
        long queryTimestamp = queryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        // 查询最新的持股数据
        List<DongChaiNorthHoldShareDTO> latestHoldShareDTOList =Lists.newArrayList();
        for(String stockCode : stockCodeList){
            try {
                List<String> strList =Lists.newArrayList();
                String fileName =String.format(StockConstant.NORTH_HOLD_SHARES_FILE, stockCode);
                File localFile = new File(fileName);
                if(localFile.exists()){
                    strList =FileUtils.readLines(localFile, Charset.forName("UTF-8"));
                }
                DongChaiNorthHoldShareDTO tmpHoldShareDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, DongChaiNorthHoldShareDTO.class))
                        .filter(holdShareDTO -> {
                            if(holdShareDTO.getTradeDate() ==null){
                                return false;
                            }

                            LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
                            long tradeTimestamp = tradeDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                            return tradeTimestamp <= queryTimestamp;
                        })
                        .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                        .findFirst()
                        .orElse(null);
                if(tmpHoldShareDTO !=null){
                    latestHoldShareDTOList.add(tmpHoldShareDTO);
                }

                System.out.println(" queryNorthHoldShares end, tmpHoldShareDTO: " + JSON.toJSONString(tmpHoldShareDTO));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return latestHoldShareDTOList;
    }

    @Override
    public List<DongChaiNorthHoldShareDTO> queryLocalNorthHoldShareDTOs(String code, LocalDateTime endTradeTime, Integer count){
        if(StringUtils.isBlank(code) || endTradeTime ==null || count ==null){
            return Lists.newArrayList();
        }

        long endTimeMillis = endTradeTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        // 本地数据
        try {
            List<String> strList =Lists.newArrayList();
            String fileName =String.format(StockConstant.NORTH_HOLD_SHARES_FILE, code);
            File localFile = new File(fileName);
            if(localFile.exists()){
                strList =FileUtils.readLines(localFile, Charset.forName("UTF-8"));
            }
            List<DongChaiNorthHoldShareDTO> holdShareDTOList = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, DongChaiNorthHoldShareDTO.class))
                    .filter(holdShareDTO -> holdShareDTO.getTradeDate() != null)
                    .filter(holdShareDTO -> {
                        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
                        long tradeTimeMillis = tradeDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

                        return tradeTimeMillis <= endTimeMillis;
                    })
                    .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                    .collect(Collectors.toList());
            if(holdShareDTOList.size() <=count){
                return holdShareDTOList;
            }else {
                return holdShareDTOList.subList(0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Lists.newArrayList();
    }

    /**
     * 根据日期查询行业的持股数据
     *
     * @return
     */
    public List<DongChaiIndustryHoldShareDTO> queryIndustryHoldShareDTO(String queryDate){
        List<String> stockCodeList = this.getLocalStockCodeList();
        List<String> indNameList =Optional.ofNullable(stockCodeList).orElse(Lists.newArrayList()).stream()
                .map(stockCode -> this.getLocalCompanyDTO(stockCode))
                .filter(companyDTO -> companyDTO != null && StringUtils.isNotBlank(companyDTO.getInd_name()))
                .map(CompanyDTO::getInd_name).distinct()
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(indNameList)){
            return Lists.newArrayList();
        }

        LocalDateTime queryDateTime = DateUtil.parseLocalDate(queryDate, DateUtil.DATE_FORMAT).atStartOfDay();
        long queryTimestamp = queryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        List<DongChaiIndustryHoldShareDTO> industryHoldShareDTOList =Lists.newArrayList();
        for(String indName : indNameList){
            try {
                List<String> strList = Lists.newArrayList();
                String fileName = String.format(StockConstant.NORTH_HOLD_SHARES_IND_FILE, indName);
                File localFile = new File(fileName);
                if (localFile.exists()) {
                    strList = FileUtils.readLines(localFile, Charset.forName("UTF-8"));
                }

                DongChaiIndustryHoldShareDTO industryHoldShareDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, DongChaiIndustryHoldShareDTO.class))
                        .filter(holdShareDTO -> {
                            if(holdShareDTO.getTradeDate() ==null){
                                return false;
                            }

                            LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
                            long tradeTimestamp = tradeDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                            return tradeTimestamp <= queryTimestamp;
                        })
                        .sorted(Comparator.comparing(DongChaiIndustryHoldShareDTO::getTradeDate).reversed())
                        .findFirst()
                        .orElse(null);

                if(industryHoldShareDTO !=null){
                    industryHoldShareDTOList.add(industryHoldShareDTO);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return industryHoldShareDTOList;
    }

    @Override
    public DongChaiIndustryHoldShareDTO queryIndustryHoldShareDTO(String indName, String queryDate) {
        if(StringUtils.isBlank(indName) || StringUtils.isBlank(queryDate)){
            return null;
        }

        LocalDateTime queryDateTime = DateUtil.parseLocalDateTime(queryDate, DateUtil.DATE_TIME_FORMAT);
        if(queryDateTime ==null){
            return null;
        }

        try {
            List<String> strList = Lists.newArrayList();
            String fileName = String.format(StockConstant.NORTH_HOLD_SHARES_IND_FILE, indName);
            File localFile = new File(fileName);
            if (localFile.exists()) {
                strList = FileUtils.readLines(localFile, Charset.forName("UTF-8"));
            }

            DongChaiIndustryHoldShareDTO industryHoldShareDTO = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                    .map(str -> JSON.parseObject(str, DongChaiIndustryHoldShareDTO.class))
                    .filter(holdShareDTO -> holdShareDTO.getTradeDate() != null)
                    .filter(holdShareDTO -> {
                        LocalDateTime tradeDateTime = DateUtil.parseLocalDateTime(holdShareDTO.getTradeDate(), DateUtil.DATE_TIME_FORMAT);
                        return tradeDateTime !=null && (tradeDateTime.isBefore(queryDateTime) || tradeDateTime.isEqual(queryDateTime));
                    })
                    .sorted(Comparator.comparing(DongChaiIndustryHoldShareDTO::getTradeDate).reversed())
                    .findFirst()
                    .orElse(null);

            return industryHoldShareDTO;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 补全Ma20Value数据
     *
     * @param kLineDTOList
     */
    private void assembleMa20Value(List<XueQiuStockKLineDTO> kLineDTOList){
        if(CollectionUtils.isEmpty(kLineDTOList) || kLineDTOList.size() < 20){
            return ;
        }

        List<XueQiuStockKLineDTO> sortedKLinetDTOList = kLineDTOList.stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        for(int i=0; i<sortedKLinetDTOList.size() -20; i++){
            XueQiuStockKLineDTO currentKLineDTO = sortedKLinetDTOList.get(i);
            List<XueQiuStockKLineDTO> calculateKLineDTOList = sortedKLinetDTOList.subList(i, i + 20);
            double ma_20_value =calculateKLineDTOList.stream().mapToDouble(XueQiuStockKLineDTO::getClose).sum() / 20;
            currentKLineDTO.setMa_20_value(NumberUtil.format(ma_20_value, 2));
        }
    }

    /**
     * 补全Ma60Value数据
     *
     * @param kLineDTOList
     */
    private void assembleMa60Value(List<XueQiuStockKLineDTO> kLineDTOList){
        if(CollectionUtils.isEmpty(kLineDTOList) || kLineDTOList.size() < 60){
            return ;
        }

        List<XueQiuStockKLineDTO> sortedKLinetDTOList = kLineDTOList.stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        for(int i=0; i<sortedKLinetDTOList.size() -60; i++){
            XueQiuStockKLineDTO currentKLineDTO = sortedKLinetDTOList.get(i);
            List<XueQiuStockKLineDTO> calculateKLineDTOList = sortedKLinetDTOList.subList(i, i + 60);
            double ma_60_value =calculateKLineDTOList.stream().mapToDouble(XueQiuStockKLineDTO::getClose).sum() / 60;
            currentKLineDTO.setMa_60_value(NumberUtil.format(ma_60_value, 2));
        }
    }

    /**
     * 补全Ma250Value数据
     *
     * @param kLineDTOList
     */
    private void assembleMa250Value(List<XueQiuStockKLineDTO> kLineDTOList){
        if(CollectionUtils.isEmpty(kLineDTOList) || kLineDTOList.size() < 250){
            return ;
        }

        List<XueQiuStockKLineDTO> sortedKLinetDTOList = kLineDTOList.stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        for(int i=0; i<sortedKLinetDTOList.size() -250; i++){
            XueQiuStockKLineDTO currentKLineDTO = sortedKLinetDTOList.get(i);
            List<XueQiuStockKLineDTO> calculateKLineDTOList = sortedKLinetDTOList.subList(i, i + 250);
            double ma_250_value =calculateKLineDTOList.stream().mapToDouble(XueQiuStockKLineDTO::getClose).sum() / 250;
            currentKLineDTO.setMa_250_value(NumberUtil.format(ma_250_value, 2));
        }
    }

    /**
     * 补全Ma1000Value数据
     *
     * @param kLineDTOList
     */
    private void assembleMa1000Value(List<XueQiuStockKLineDTO> kLineDTOList){
        if(CollectionUtils.isEmpty(kLineDTOList) || kLineDTOList.size() < 1000){
            return ;
        }

        List<XueQiuStockKLineDTO> sortedKLinetDTOList = kLineDTOList.stream()
                .filter(kLineDTO -> kLineDTO.getTimestamp() != null)
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        for(int i=0; i<sortedKLinetDTOList.size() -1000; i++) {
            XueQiuStockKLineDTO currentKLineDTO = sortedKLinetDTOList.get(i);
            List<XueQiuStockKLineDTO> calculateKLineDTOList = sortedKLinetDTOList.subList(i, i + 1000);
            double ma_1000_value = calculateKLineDTOList.stream().mapToDouble(XueQiuStockKLineDTO::getClose).sum() / 1000;
            currentKLineDTO.setMa_1000_value(NumberUtil.format(ma_1000_value, 2));
        }
    }

    /**
     * 更新当天的行业持仓数据
     *
     * @param localDateTime
     */
    private void updateLocalIndustryHoldShares(List<String> stockCodeList, LocalDateTime localDateTime){
        if(CollectionUtils.isEmpty(stockCodeList) || localDateTime ==null){
            return ;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String queryTradeDate = df.format(localDateTime.toLocalDate().atStartOfDay());
        System.out.println(String.format("updateLocalIndustryHoldShares start, queryTradeDate:%s", queryTradeDate));

        Double totalMarketCap =0d;
        Map<String, Double> indNameAndTotalMarketCapMap = Maps.newHashMap();
        Double totalHoldMarketCap =0d;
        Map<String, Double> indNameAndHoldMarketCapMap = new HashMap<>();
        for(String stockCode : stockCodeList){
            CompanyDTO companyDTO = this.getLocalCompanyDTO(stockCode);
            if(companyDTO ==null || StringUtils.isBlank(companyDTO.getInd_name())){
                continue;
            }
            String indName =companyDTO.getInd_name();

            List<DongChaiNorthHoldShareDTO> holdShareDTOList = this.queryLocalNorthHoldShareDTOs(stockCode, localDateTime, 1);
            if(CollectionUtils.isEmpty(holdShareDTOList)){
                continue;
            }

            DongChaiNorthHoldShareDTO holdShareDTO = holdShareDTOList.get(0);
            if(holdShareDTO ==null || !StringUtils.equals(holdShareDTO.getTradeDate(), queryTradeDate)){
                continue;
            }

            // 行业市值数据
            if(holdShareDTO.getHoldMarketCap() !=null && holdShareDTO.getHoldMarketCap() > 0
                    && holdShareDTO.getTotalSharesRatio() !=null && holdShareDTO.getTotalSharesRatio() >0){
                double stockTotalMarketCap = (holdShareDTO.getHoldMarketCap() / holdShareDTO.getTotalSharesRatio()) * 100;
                Double indTotalMarketCap = indNameAndTotalMarketCapMap.getOrDefault(indName, 0d);
                indNameAndTotalMarketCapMap.put(indName, indTotalMarketCap + stockTotalMarketCap);

                totalMarketCap = totalMarketCap + stockTotalMarketCap;
            }

            // 沪港通持股数据
            if(holdShareDTO.getHoldMarketCap() !=null){
                Double indHoldMarketCap = indNameAndHoldMarketCapMap.getOrDefault(indName, 0d);
                indNameAndHoldMarketCapMap.put(indName, indHoldMarketCap + holdShareDTO.getHoldMarketCap());

                totalHoldMarketCap = totalHoldMarketCap + holdShareDTO.getHoldMarketCap();
            }
        }
        indNameAndHoldMarketCapMap.put("全行业", totalHoldMarketCap);

        for(Map.Entry<String, Double> entry : indNameAndHoldMarketCapMap.entrySet()){
            String indName = entry.getKey();
            Double indHoldMarketCap = entry.getValue();
            if(indHoldMarketCap ==0d){
                continue;
            }

            try {
                List<String> strList =Lists.newArrayList();
                String fileName =String.format(StockConstant.NORTH_HOLD_SHARES_IND_FILE, indName);
                File localFile = new File(fileName);
                if(localFile.exists()){
                    strList =FileUtils.readLines(localFile, Charset.forName("UTF-8"));
                }

                Map<String, DongChaiIndustryHoldShareDTO> tradeDateAndHoldShareMap = Optional.ofNullable(strList).orElse(Lists.newArrayList()).stream()
                        .map(str -> JSON.parseObject(str, DongChaiIndustryHoldShareDTO.class))
                        .collect(Collectors.toMap(DongChaiIndustryHoldShareDTO::getTradeDate, val -> val, (val1, val2) -> val1));
                if(!tradeDateAndHoldShareMap.containsKey(queryTradeDate)){
                    DongChaiIndustryHoldShareDTO industryDTO =new DongChaiIndustryHoldShareDTO();
                    industryDTO.setIndName(indName);
                    industryDTO.setTradeDate(queryTradeDate);
                    industryDTO.setHoldMarketCap(NumberUtil.format(indHoldMarketCap, 1));
                    industryDTO.setIndustryRatio(NumberUtil.format(indHoldMarketCap/totalHoldMarketCap, 3));

                    Double indTotalMarketCap = indNameAndTotalMarketCapMap.get(indName);
                    if(indTotalMarketCap !=null && indTotalMarketCap >0 && totalMarketCap > 0){
                        industryDTO.setIndTotalMarketCap(NumberUtil.format(indTotalMarketCap, 1));
                        industryDTO.setIndTotalMarketRatio(NumberUtil.format(indTotalMarketCap/totalMarketCap, 3));
                        industryDTO.setOverHoldRatio(NumberUtil.format(industryDTO.getIndustryRatio() - industryDTO.getIndTotalMarketRatio(), 3));
                    }

                    tradeDateAndHoldShareMap.put(queryTradeDate, industryDTO);
                }

                // 重新写入本地文件
                List<String> strDataList = tradeDateAndHoldShareMap.values().stream()
                        .sorted(Comparator.comparing(DongChaiIndustryHoldShareDTO::getTradeDate).reversed())
                        .map(holdShareDTO -> JSON.toJSONString(holdShareDTO))
                        .collect(Collectors.toList());
                if(!Objects.equals(strDataList.size(), strList.size())){
                    FileUtils.writeLines(localFile, strDataList, false);
                }

                System.out.println(String.format("updateLocalIndustryHoldShares end, indName:%s", indName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算并保存单季利润数据
     */
    private void getAndSaveQuarterIncome(){
        try {
            String incomeFileName =String.format(StockConstant.INCOME_LIST);
            List<String> strList =FileUtils.readLines(new File(incomeFileName), Charset.forName("UTF-8"));
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

                    XueQiuStockIncomeDTO incomeDTO1 = reportTypeMap.get(FinanceReportTypeEnum.QUARTER_1.getCode());
                    XueQiuStockIncomeDTO incomeDTO2 = reportTypeMap.get(FinanceReportTypeEnum.HALF_YEAR.getCode());
                    XueQiuStockIncomeDTO incomeDTO3 = reportTypeMap.get(FinanceReportTypeEnum.QUARTER_3.getCode());
                    XueQiuStockIncomeDTO incomeDTO4 = reportTypeMap.get(FinanceReportTypeEnum.ALL_YEAR.getCode());
                    if(incomeDTO1 !=null){
                        QuarterIncomeDTO quarterIncomeDTO =new QuarterIncomeDTO();
                        BeanUtils.copyProperties(incomeDTO1, quarterIncomeDTO);
                        quarterIncomeDTO.setReport_type(FinanceReportTypeEnum.SINGLE_Q_1.getCode());
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                    if(incomeDTO1 !=null && incomeDTO2 !=null){
                        QuarterIncomeDTO quarterIncomeDTO = this.getQuarterIncomeDTO(incomeDTO1, incomeDTO2);
                        quarterIncomeDTO.setReport_type(FinanceReportTypeEnum.SINGLE_Q_2.getCode());
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                    if(incomeDTO2 !=null && incomeDTO3 !=null){
                        QuarterIncomeDTO quarterIncomeDTO = this.getQuarterIncomeDTO(incomeDTO2, incomeDTO3);
                        quarterIncomeDTO.setReport_type(FinanceReportTypeEnum.SINGLE_Q_3.getCode());
                        quarterIncomeDTOList.add(quarterIncomeDTO);
                    }
                    if(incomeDTO3 !=null && incomeDTO4 !=null){
                        QuarterIncomeDTO quarterIncomeDTO = this.getQuarterIncomeDTO(incomeDTO3, incomeDTO4);
                        quarterIncomeDTO.setReport_type(FinanceReportTypeEnum.SINGLE_Q_4.getCode());
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

            FileUtils.writeLines(new File(StockConstant.INCOME_LIST_Q), strIncomeList, false);

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
