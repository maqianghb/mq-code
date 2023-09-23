package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.CloseableHttpClientUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.model.XueQiuStockBalanceDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFinanceNoticeDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

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
import java.util.stream.Stream;


public class DongChaiLocalDataManager {

    private static List<DongChaiHolderIncreaseDTO> holderIncreaseDTOList =Lists.newArrayList();
    private static List<DongChaiFreeShareDTO> freeShareDTOList =Lists.newArrayList();

    private static final String NOTICE_HEADER ="编码,名称,预告时间,报告期,预告指标,预告类型,预告值(亿),预告值(亿),同比变动,同比变动," +
            "环比变动,环比变动,上年同期值(亿),变动原因";
    private static final String HOLDER_INCREASE_HEADER ="编码,名称,预告时间,增减类型,增减数量(万股),减持开始时间,减持结束时间,变动比例(%)," +
            "变动后持股比例(%)";

    public static void main(String[] args) {
        DongChaiLocalDataManager manager =new DongChaiLocalDataManager();

//        String reportDate ="2023-06-30";
//        List<DongChaiFinanceNoticeDTO> noticeDTOList = manager.queryAndSaveFinanceNotice(reportDate);
//        System.out.println("noticeDTOList: " + JSON.toJSONString(noticeDTOList));
//
//        List<DongChaiHolderIncreaseDTO> increaseDTOList = manager.queryAndSaveHolderIncreaseList();
//        System.out.println("increaseDTOList: " + JSON.toJSONString(increaseDTOList));

//        List<DongChaiFreeShareDTO> freeShareDTOList = manager.queryFreeShareDTOList();
//        System.out.println("freeShareDTOList: " + JSON.toJSONString(freeShareDTOList));

//        manager.queryAndSaveHoldShareDTOList();

        System.out.println("end.");
    }

    /**
     * 查询业绩预告数据
     *
     * @return
     */
    public List<DongChaiFinanceNoticeDTO> queryAndSaveFinanceNotice(String reportDate) {
        Integer totalNum = this.queryFinanceNoticeNum(reportDate);
        if(totalNum ==null || totalNum <=0){
            return Lists.newArrayList();
        }

        List<DongChaiFinanceNoticeDTO> noticeDTOList =Lists.newArrayList();
        for(int pageNum =1; ; pageNum++){
            int currentIndex =(pageNum-1) * StockConstant.DONG_CHAI_MAX_LIMIT;
            if(currentIndex >=totalNum){
                break;
            }

            int currentPageSize = totalNum -currentIndex < StockConstant.DONG_CHAI_MAX_LIMIT
                    ? totalNum -currentIndex : StockConstant.DONG_CHAI_MAX_LIMIT;
            String url = new StringBuilder().append(StockConstant.DONG_CHAI_URL)
//                .append("?callback=").append("jQuery1123093599956891065_1689992754007")
                    .append("?sortColumns=").append("NOTICE_DATE%2CSECURITY_CODE")
                    .append("&sortTypes=").append("-1%2C-1")
                    .append("&pageSize=").append(currentPageSize)
                    .append("&pageNumber=").append(pageNum)
                    .append("&reportName=").append("RPT_PUBLIC_OP_NEWPREDICT")
                    .append("&columns=").append("ALL")
                    .append("&filter=").append("(REPORT_DATE%3D%27").append(reportDate).append("%27)")
                    .toString();

            String strResult = CloseableHttpClientUtil.doGet(url, StringUtils.EMPTY);
            List<String> columnList = Optional.ofNullable(JSONObject.parseObject(strResult))
                    .map(jsonResult -> jsonResult.getJSONObject("result"))
                    .map(jsonResult -> JSON.parseArray(jsonResult.getString("data"), String.class))
                    .orElse(Lists.newArrayList());
            if(CollectionUtils.isEmpty(columnList)){
               break;
            }

            List<DongChaiFinanceNoticeDTO> tmpNoticeDTOList = columnList.stream()
                    .map(strColumn -> {
                        JSONObject jsonColumn = JSON.parseObject(strColumn);
                        DongChaiFinanceNoticeDTO noticeDTO = new DongChaiFinanceNoticeDTO();
                        if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_CODE"))) {
                            String code =jsonColumn.getString("SECURITY_CODE");
                            if(code.startsWith("6")){
                                code ="SH" + code;
                            }else if(code.startsWith("0") || code.startsWith("3")){
                                code ="SZ" + code;
                            }
                            noticeDTO.setCode(code);
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_NAME_ABBR"))) {
                            noticeDTO.setName(jsonColumn.getString("SECURITY_NAME_ABBR"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("NOTICE_DATE"))) {
                            noticeDTO.setNotice_date(jsonColumn.getString("NOTICE_DATE"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("REPORT_DATE"))) {
                            noticeDTO.setReport_date(jsonColumn.getString("REPORT_DATE"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("PREDICT_FINANCE"))) {
                            noticeDTO.setPredict_indicator(jsonColumn.getString("PREDICT_FINANCE"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("PREDICT_TYPE"))) {
                            noticeDTO.setPredict_type(jsonColumn.getString("PREDICT_TYPE"));
                        }
                        if (jsonColumn.getLong("PREDICT_AMT_LOWER") !=null) {
                            double predict_amt_lower = jsonColumn.getLong("PREDICT_AMT_LOWER") / (1.0 * 10000 * 10000);
                            noticeDTO.setPredict_amount_low(NumberUtil.format(predict_amt_lower, 2));
                        }
                        if (jsonColumn.getLong("PREDICT_AMT_UPPER") !=null) {
                            double predict_amt_upper = jsonColumn.getLong("PREDICT_AMT_UPPER") / (1.0 * 10000 * 10000);
                            noticeDTO.setPredict_amount_up(NumberUtil.format(predict_amt_upper, 2));
                        }
                        if (jsonColumn.getDouble("ADD_AMP_LOWER") !=null) {
                            double add_amp_lower = jsonColumn.getDouble("ADD_AMP_LOWER") / (1.0 * 100);
                            noticeDTO.setAdd_amp_low(NumberUtil.format(add_amp_lower, 3));
                        }
                        if (jsonColumn.getDouble("ADD_AMP_UPPER") !=null) {
                            double add_amp_upper = jsonColumn.getDouble("ADD_AMP_UPPER") / (1.0 * 100);
                            noticeDTO.setAdd_amp_up(NumberUtil.format(add_amp_upper, 3));
                        }
                        if (jsonColumn.getDouble("PREDICT_RATIO_LOWER") !=null) {
                            double predict_ratio_low = jsonColumn.getDouble("PREDICT_RATIO_LOWER") / (1.0 * 100);
                            noticeDTO.setPredict_ratio_low(NumberUtil.format(predict_ratio_low, 3));
                        }
                        if (jsonColumn.getDouble("PREDICT_RATIO_UPPER") !=null) {
                            double predict_ratio_up = jsonColumn.getDouble("PREDICT_RATIO_UPPER") / (1.0 * 100);
                            noticeDTO.setPredict_ratio_up(NumberUtil.format(predict_ratio_up, 3));
                        }
                        if (jsonColumn.getLong("PREYEAR_SAME_PERIOD") !=null) {
                            double pre_year_same_period = jsonColumn.getLong("PREYEAR_SAME_PERIOD") / (1.0 * 10000 * 10000);
                            noticeDTO.setPre_year_same_period(NumberUtil.format(pre_year_same_period, 2));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("CHANGE_REASON_EXPLAIN"))) {
                            String reason = jsonColumn.getString("CHANGE_REASON_EXPLAIN");
                            noticeDTO.setReason(StringUtils.replace(reason, ",", "."));
                        }

                        return noticeDTO;
                    })
                    .collect(Collectors.toList());

            noticeDTOList.addAll(tmpNoticeDTOList);
        }

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return Lists.newArrayList();
        }

        List<String> strNoticeList =Lists.newArrayList();
        strNoticeList.add(NOTICE_HEADER);
        for(DongChaiFinanceNoticeDTO noticeDTO : noticeDTOList){
            if(!stockCodeList.contains(noticeDTO.getCode())){
                continue;
            }

            try {
                Field[] fields = DongChaiFinanceNoticeDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(noticeDTO));
                StringBuilder noticeBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName ());
                    noticeBuilder.append(",").append(value);
                }
                strNoticeList.add(noticeBuilder.toString().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 记录结果
        try {
            DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strDateTime = df.format(localDateTime);//格式化为字符串
            String filterListName =String.format(StockConstant.FINANCE_NOTICE_LIST, reportDate, strDateTime);
            FileUtils.writeLines(new File(filterListName), "UTF-8", strNoticeList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return noticeDTOList;
    }

    /**
     * 查询业绩预告数据条数
     *
     * @return
     */
    public Integer queryFinanceNoticeNum(String reportDate) {
        String url = new StringBuilder().append(StockConstant.DONG_CHAI_URL)
//                .append("?callback=").append("jQuery1123093599956891065_1689992754007")
                .append("?sortColumns=").append("NOTICE_DATE%2CSECURITY_CODE")
                .append("&sortTypes=").append("-1%2C-1")
                .append("&pageSize=").append(100)
                .append("&pageNumber=").append(1)
                .append("&reportName=").append("RPT_PUBLIC_OP_NEWPREDICT")
                .append("&columns=").append("ALL")
                .append("&filter=").append("(REPORT_DATE%3D%27").append(reportDate).append("%27)")
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StringUtils.EMPTY);
        return Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("result"))
                .map(jsonResult -> jsonResult.getInteger("count"))
                .orElse(0);
    }

    /**
     * 查询并保存股东增减持数据
     *
     * @return
     */
    public List<DongChaiHolderIncreaseDTO> queryAndSaveHolderIncreaseList() {
        List<DongChaiHolderIncreaseDTO> increaseDTOList = this.queryHolderIncreaseList();
        if(CollectionUtils.isEmpty(increaseDTOList)){
            return Lists.newArrayList();
        }

        List<String> strIncreaseList =Lists.newArrayList();
        strIncreaseList.add(HOLDER_INCREASE_HEADER);
        for(DongChaiHolderIncreaseDTO increaseDTO : increaseDTOList){
            try {
                Field[] fields = DongChaiHolderIncreaseDTO.class.getDeclaredFields();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(increaseDTO));
                StringBuilder increaseBuilder =new StringBuilder();
                for(Field field : fields){
                    Object value = jsonObject.get(field.getName ());
                    increaseBuilder.append(",").append(value);
                }
                strIncreaseList.add(increaseBuilder.toString().substring(1));
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
            FileUtils.writeLines(new File(filterListName), "UTF-8", strIncreaseList, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return increaseDTOList;
    }


    /**
     * 查询股东增减持条数
     *
     * @return
     */
    public List<DongChaiHolderIncreaseDTO> queryHolderIncreaseList() {
        int totalNum =5000;

        List<DongChaiHolderIncreaseDTO> increaseDTOList =Lists.newArrayList();
        for(int pageNum =1; ; pageNum++) {
            int currentIndex = (pageNum - 1) * StockConstant.DONG_CHAI_MAX_LIMIT;
            if (currentIndex >= totalNum) {
                break;
            }

            int currentPageSize = totalNum - currentIndex < StockConstant.DONG_CHAI_MAX_LIMIT
                    ? totalNum - currentIndex : StockConstant.DONG_CHAI_MAX_LIMIT;

            String url = new StringBuilder().append(StockConstant.DONG_CHAI_URL)
//                .append("?callback=").append("jQuery1123093599956891065_1689992754007")
                    .append("?sortColumns=").append("END_DATE%2CSECURITY_CODE%2CEITIME")
                    .append("&sortTypes=").append("-1%2C-1%2C-1")
                    .append("&pageSize=").append(currentPageSize)
                    .append("&pageNumber=").append(pageNum)
                    .append("&reportName=").append("RPT_SHARE_HOLDER_INCREASE")
                    .append("&quoteColumns=").append("f2~01~SECURITY_CODE~NEWEST_PRICE%2Cf3~01~SECURITY_CODE~CHANGE_RATE_QUOTES")
                    .append("&quoteType=").append("0")
                    .append("&columns=").append("ALL")
                    .append("&source=").append("WEB")
                    .append("&client=").append("WEB")
                    .append("&filter=").append(StringUtils.EMPTY)
                    .toString();

            String strResult = CloseableHttpClientUtil.doGet(url, StringUtils.EMPTY);
            List<String> columnList = Optional.ofNullable(JSONObject.parseObject(strResult))
                    .map(jsonResult -> jsonResult.getJSONObject("result"))
                    .map(jsonResult -> JSON.parseArray(jsonResult.getString("data"), String.class))
                    .orElse(Lists.newArrayList());
            if(CollectionUtils.isEmpty(columnList)){
                break;
            }

            List<DongChaiHolderIncreaseDTO> tmpIncreaseDTOList = columnList.stream()
                    .map(strColumn -> {
                        JSONObject jsonColumn = JSON.parseObject(strColumn);
                        DongChaiHolderIncreaseDTO increaseDTO = new DongChaiHolderIncreaseDTO();
                        if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_CODE"))) {
                            String code =jsonColumn.getString("SECURITY_CODE");
                            if(code.startsWith("6")){
                                code ="SH" + code;
                            }else if(code.startsWith("0") || code.startsWith("3")){
                                code ="SZ" + code;
                            }
                            increaseDTO.setCode(code);
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_NAME_ABBR"))) {
                            increaseDTO.setName(jsonColumn.getString("SECURITY_NAME_ABBR"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("NOTICE_DATE"))) {
                            increaseDTO.setNotice_date(jsonColumn.getString("NOTICE_DATE"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("DIRECTION"))) {
                            increaseDTO.setDirection(jsonColumn.getString("DIRECTION"));
                        }
                        if (jsonColumn.getDouble("CHANGE_NUM") != null) {
                            increaseDTO.setChange_num(NumberUtil.format(jsonColumn.getDouble("CHANGE_NUM"), 2));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("START_DATE"))) {
                            increaseDTO.setStart_date(jsonColumn.getString("START_DATE"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("END_DATE"))) {
                            increaseDTO.setEnd_date(jsonColumn.getString("END_DATE"));
                        }
                        if (jsonColumn.getDouble("AFTER_CHANGE_RATE") != null) {
                            increaseDTO.setAfter_change_rate(NumberUtil.format(jsonColumn.getDouble("AFTER_CHANGE_RATE"), 2));
                        }
                        if (jsonColumn.getDouble("HOLD_RATIO") != null) {
                            increaseDTO.setHold_ratio(NumberUtil.format(jsonColumn.getDouble("HOLD_RATIO"), 2));
                        }

                        return increaseDTO;
                    })
                    .collect(Collectors.toList());

            increaseDTOList.addAll(tmpIncreaseDTOList);
        }

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return Lists.newArrayList();
        }

        List<DongChaiHolderIncreaseDTO> resultIncreaseDTOList =Lists.newArrayList();
        for(DongChaiHolderIncreaseDTO increaseDTO : increaseDTOList){
            if(stockCodeList.contains(increaseDTO.getCode())){
                resultIncreaseDTOList.add(increaseDTO);
            }
        }

        return increaseDTOList;
    }

    /**
     * 查询解禁信息
     *
     * @return
     */
    private List<DongChaiFreeShareDTO> queryFreeShareDTOList(){
        int totalNum =5000;
        LocalDate nowLocalDate = LocalDate.now();
        String startDate =nowLocalDate.plusMonths(-6).format(DateTimeFormatter.ISO_DATE);
        String endDate =nowLocalDate.plusMonths(6).format(DateTimeFormatter.ISO_DATE);

        List<DongChaiFreeShareDTO> freeShareDTOList =Lists.newArrayList();
        for(int pageNum =1; ; pageNum++) {
            int currentIndex = (pageNum - 1) * StockConstant.DONG_CHAI_MAX_LIMIT;
            if (currentIndex >= totalNum) {
                break;
            }

            int currentPageSize = totalNum - currentIndex < StockConstant.DONG_CHAI_MAX_LIMIT
                    ? totalNum - currentIndex : StockConstant.DONG_CHAI_MAX_LIMIT;

            String url = new StringBuilder().append(StockConstant.DONG_CHAI_URL)
//                .append("?callback=").append("jQuery1123093599956891065_1689992754007")
                    .append("?sortColumns=").append("FREE_DATE%2CCURRENT_FREE_SHARES")
                    .append("&sortTypes=").append("-1%2C-1")
                    .append("&pageSize=").append(currentPageSize)
                    .append("&pageNumber=").append(pageNum)
                    .append("&reportName=").append("RPT_LIFT_STAGE")
                    .append("&columns=").append("ALL")
                    .append("&source=").append("WEB")
                    .append("&client=").append("WEB")
                    .append("&filter=").append("(FREE_DATE%3E%3D%27")
                    .append(startDate).append("%27)(FREE_DATE%3C%3D%27")
                    .append(endDate).append("%27)")
                    .toString();

            String strResult = CloseableHttpClientUtil.doGet(url, StringUtils.EMPTY);
            List<String> columnList = Optional.ofNullable(JSONObject.parseObject(strResult))
                    .map(jsonResult -> jsonResult.getJSONObject("result"))
                    .map(jsonResult -> JSON.parseArray(jsonResult.getString("data"), String.class))
                    .orElse(Lists.newArrayList());
            if(CollectionUtils.isEmpty(columnList)){
                break;
            }

            List<DongChaiFreeShareDTO> tmpFreeShareDTOList = columnList.stream()
                    .map(strColumn -> {
                        JSONObject jsonColumn = JSON.parseObject(strColumn);
                        DongChaiFreeShareDTO freeShareDTO = new DongChaiFreeShareDTO();
                        if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_CODE"))) {
                            String code =jsonColumn.getString("SECURITY_CODE");
                            if(code.startsWith("6")){
                                code ="SH" + code;
                            }else if(code.startsWith("0") || code.startsWith("3")){
                                code ="SZ" + code;
                            }
                            freeShareDTO.setCode(code);
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_NAME_ABBR"))) {
                            freeShareDTO.setName(jsonColumn.getString("SECURITY_NAME_ABBR"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("FREE_DATE"))) {
                            freeShareDTO.setFree_date(jsonColumn.getString("FREE_DATE"));
                        }
                        if (StringUtils.isNotBlank(jsonColumn.getString("FREE_SHARES_TYPE"))) {
                            freeShareDTO.setFree_type(jsonColumn.getString("FREE_SHARES_TYPE"));
                        }
                        if (jsonColumn.getDouble("ABLE_FREE_SHARES") != null) {
                            freeShareDTO.setFree_share_num(NumberUtil.format(jsonColumn.getDouble("ABLE_FREE_SHARES"), 1));
                        }
                        if (jsonColumn.getDouble("FREE_RATIO") != null) {
                            freeShareDTO.setFree_ratio(NumberUtil.format(jsonColumn.getDouble("FREE_RATIO") * 100, 2));
                        }
                        if (jsonColumn.getDouble("TOTAL_RATIO") != null) {
                            freeShareDTO.setTotal_ratio(NumberUtil.format(jsonColumn.getDouble("TOTAL_RATIO") * 100, 2));
                        }

                        return freeShareDTO;
                    })
                    .collect(Collectors.toList());

            freeShareDTOList.addAll(tmpFreeShareDTOList);
        }

        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return Lists.newArrayList();
        }

        List<DongChaiFreeShareDTO> resultFreeShareDTOList =Lists.newArrayList();
        for(DongChaiFreeShareDTO freeShareDTO : freeShareDTOList){
            if(stockCodeList.contains(freeShareDTO.getCode())){
                resultFreeShareDTOList.add(freeShareDTO);
            }
        }

        return resultFreeShareDTOList;
    }

    /**
     * 查询并保存沪港通持股数据
     */
    private void queryAndSaveHoldShareDTOList(){
        LocalStockDataManager localStockDataManager =new LocalStockDataManager();
        List<String> stockCodeList = localStockDataManager.getStockCodeList();
        if(CollectionUtils.isEmpty(stockCodeList)){
            return ;
        }

        // 查询沪港通持股数据
        for(String stockCode : stockCodeList){
            try {
                // 查询数据
                List<DongChaiNorthHoldShareDTO> tmpHoldShareDTOList = this.queryNorthHoldShareDTOList(stockCode.substring(2));
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
                    if(!holdShareDTOMap.containsKey(holdShareDTO.getTradeDate())){
                        holdShareDTOMap.put(holdShareDTO.getTradeDate(), holdShareDTO);
                    }
                }

                // 重新写入本地文件
                List<String> strDataList = holdShareDTOMap.values().stream()
                        .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                        .map(holdShareDTO -> JSON.toJSONString(holdShareDTO))
                        .collect(Collectors.toList());
                if(!Objects.equals(strDataList.size(), strList.size())){
                    FileUtils.writeLines(new File(fileName), strDataList, false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 查询沪港通持股数据
     *
     * @return
     */
    private List<DongChaiNorthHoldShareDTO> queryNorthHoldShareDTOList(String simpleCode){
        LocalDate nowLocalDate = LocalDate.now();
        String startDate =nowLocalDate.plusYears(-1).format(DateTimeFormatter.ISO_DATE);

        String url = new StringBuilder().append(StockConstant.DONG_CHAI_URL)
//                .append("?callback=").append("jQuery1123093599956891065_1689992754007")
                .append("?sortColumns=").append("TRADE_DATE")
                .append("&sortTypes=").append("-1")
                .append("&pageSize=").append(StockConstant.DONG_CHAI_MAX_LIMIT)
                .append("&pageNumber=").append(1)
                .append("&reportName=").append("RPT_MUTUAL_HOLDSTOCKNORTH_STA")
                .append("&columns=").append("ALL")
                .append("&source=").append("WEB")
                .append("&client=").append("WEB")
                .append("&filter=").append("(SECURITY_CODE%3D%22")
                .append(simpleCode).append("%22)(TRADE_DATE%3E%3D%27")
                .append(startDate).append("%27)")
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StringUtils.EMPTY);
        List<String> columnList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("result"))
                .map(jsonResult -> JSON.parseArray(jsonResult.getString("data"), String.class))
                .orElse(Lists.newArrayList());
        if(CollectionUtils.isEmpty(columnList)){
            return Lists.newArrayList();
        }

        List<DongChaiNorthHoldShareDTO> northHoldShareDTOList = columnList.stream()
                .map(strColumn -> {
                    JSONObject jsonColumn = JSON.parseObject(strColumn);
                    DongChaiNorthHoldShareDTO northHoldShareDTO = new DongChaiNorthHoldShareDTO();
                    if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_CODE"))) {
                        String code = jsonColumn.getString("SECURITY_CODE");
                        if (code.startsWith("6")) {
                            code = "SH" + code;
                        } else if (code.startsWith("0") || code.startsWith("3")) {
                            code = "SZ" + code;
                        }
                        northHoldShareDTO.setCode(code);
                    }
                    if (StringUtils.isNotBlank(jsonColumn.getString("SECURITY_NAME"))) {
                        northHoldShareDTO.setName(jsonColumn.getString("SECURITY_NAME"));
                    }
                    if (StringUtils.isNotBlank(jsonColumn.getString("TRADE_DATE"))) {
                        northHoldShareDTO.setTradeDate(jsonColumn.getString("TRADE_DATE"));
                    }
                    if (jsonColumn.getInteger("HOLD_SHARES") != null) {
                        double hold_shares = NumberUtil.format(jsonColumn.getInteger("HOLD_SHARES") / 10000, 1);
                        northHoldShareDTO.setHoldShares(hold_shares);
                    }
                    if (jsonColumn.getDouble("TOTAL_SHARES_RATIO") != null) {
                        double total_shares_ratio = NumberUtil.format(jsonColumn.getDouble("TOTAL_SHARES_RATIO"), 2);
                        northHoldShareDTO.setTotalSharesRatio(total_shares_ratio);
                    }

                    return northHoldShareDTO;
                })
                .collect(Collectors.toList());

        // 补全增持数量和比例
        this.assembleHoldIncreaseShares(northHoldShareDTOList);

        return northHoldShareDTOList;
    }

    /**
     * 补全增持数量和比例
     *
     * @param northHoldShareDTOList
     */
    private void assembleHoldIncreaseShares(List<DongChaiNorthHoldShareDTO> northHoldShareDTOList){
        if(CollectionUtils.isEmpty(northHoldShareDTOList)){
            return ;
        }

        for(int i=0; i< northHoldShareDTOList.size(); i++){
            DongChaiNorthHoldShareDTO curHoldShareDTO = northHoldShareDTOList.get(i);

            // 当天增持数据
            Double curIncreaseShares = this.getHoldIncreaseShares(curHoldShareDTO, northHoldShareDTOList, 1);
            if(curIncreaseShares !=null){
                curHoldShareDTO.setCurIncreaseShares(NumberUtil.format(curIncreaseShares, 1));
            }
            Double curIncreaseRatio = this.getHoldIncreaseRatio(curHoldShareDTO, northHoldShareDTOList, 1);
            if(curIncreaseRatio !=null){
                curHoldShareDTO.setCurIncreaseRatio(NumberUtil.format(curIncreaseRatio, 2));
            }

            // 30天增持数据
            Double increaseShares_30 = this.getHoldIncreaseShares(curHoldShareDTO, northHoldShareDTOList, 30);
            if(increaseShares_30 !=null){
                curHoldShareDTO.setIncreaseShares_30(NumberUtil.format(increaseShares_30, 1));
            }
            Double increaseRatio_30 = this.getHoldIncreaseRatio(curHoldShareDTO, northHoldShareDTOList, 30);
            if(increaseRatio_30 !=null){
                curHoldShareDTO.setIncreaseRatio_30(NumberUtil.format(increaseRatio_30, 2));
            }

            // 60天增持数据
            Double increaseShares_60 = this.getHoldIncreaseShares(curHoldShareDTO, northHoldShareDTOList, 60);
            if(increaseShares_60 !=null){
                curHoldShareDTO.setIncreaseShares_60(NumberUtil.format(increaseShares_60, 1));
            }
            Double increaseRatio_60 = this.getHoldIncreaseRatio(curHoldShareDTO, northHoldShareDTOList, 60);
            if(increaseRatio_60 !=null){
                curHoldShareDTO.setIncreaseRatio_60(NumberUtil.format(increaseRatio_60, 2));
            }

            // 90天增持数据
            Double increaseShares_90 = this.getHoldIncreaseShares(curHoldShareDTO, northHoldShareDTOList, 90);
            if(increaseShares_90 !=null){
                curHoldShareDTO.setIncreaseShares_90(NumberUtil.format(increaseShares_90, 1));
            }
            Double increaseRatio_90 = this.getHoldIncreaseRatio(curHoldShareDTO, northHoldShareDTOList, 90);
            if(increaseRatio_90 !=null){
                curHoldShareDTO.setIncreaseRatio_90(NumberUtil.format(increaseRatio_90, 2));
            }
        }

    }

    /**
     * 计算时间段内的增持数量
     *
     * @param northHoldShareDTOList
     * @param days
     */
    private Double getHoldIncreaseShares(DongChaiNorthHoldShareDTO curHoldShareDTO, List<DongChaiNorthHoldShareDTO> northHoldShareDTOList, int days){
        if(curHoldShareDTO ==null || CollectionUtils.isEmpty(northHoldShareDTOList)){
            return null;
        }

        // 当前计算的时间数据
        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endLocalDateTime = LocalDate.parse(curHoldShareDTO.getTradeDate(), df).atStartOfDay();
        long endTimeMillis = endLocalDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        List<DongChaiNorthHoldShareDTO> sortedShareDTOList = northHoldShareDTOList.stream()
                .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                .collect(Collectors.toList());
        for(int i=0; i<sortedShareDTOList.size(); i++){
            DongChaiNorthHoldShareDTO startHoldShareDTO = sortedShareDTOList.get(i);
            LocalDateTime startLocalDateTime = LocalDate.parse(startHoldShareDTO.getTradeDate(), df).atStartOfDay();
            long startTimeMillis = startLocalDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

            long sectionDays = (endTimeMillis - startTimeMillis)/(24*60*60*1000);
            if( sectionDays >=days && sectionDays < days +8){
                // 计算增减持数量
                if(curHoldShareDTO.getHoldShares() !=null && startHoldShareDTO.getHoldShares() !=null){
                    return curHoldShareDTO.getHoldShares() - startHoldShareDTO.getHoldShares();
                }
            }
        }

        return null;
    }

    /**
     * 计算时间段内的增持比例
     *
     * @param northHoldShareDTOList
     * @param days
     */
    private Double getHoldIncreaseRatio(DongChaiNorthHoldShareDTO curHoldShareDTO, List<DongChaiNorthHoldShareDTO> northHoldShareDTOList, int days){
        if(curHoldShareDTO ==null || CollectionUtils.isEmpty(northHoldShareDTOList)){
            return null;
        }

        // 当前计算的时间数据
        DateTimeFormatter df =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endLocalDateTime = LocalDate.parse(curHoldShareDTO.getTradeDate(), df).atStartOfDay();
        long endTimeMillis = endLocalDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        List<DongChaiNorthHoldShareDTO> sortedShareDTOList = northHoldShareDTOList.stream()
                .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                .collect(Collectors.toList());
        for(int i=0; i<sortedShareDTOList.size(); i++){
            DongChaiNorthHoldShareDTO startHoldShareDTO = sortedShareDTOList.get(i);
            LocalDateTime startLocalDateTime = LocalDate.parse(startHoldShareDTO.getTradeDate(), df).atStartOfDay();
            long startTimeMillis = startLocalDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

            long sectionDays = (endTimeMillis - startTimeMillis)/(24*60*60*1000);
            if( sectionDays >=days && sectionDays < days +8){
                // 计算增减持比例
                if(curHoldShareDTO.getTotalSharesRatio() !=null && startHoldShareDTO.getTotalSharesRatio() !=null){
                    return curHoldShareDTO.getTotalSharesRatio() - startHoldShareDTO.getTotalSharesRatio();
                }
            }
        }

        return null;
    }

    /**
     * 增减持信息
     *
     * @param code
     * @return
     */
    public DongChaiHolderIncreaseDTO getHolderIncreaseDTO(String code){
        try {
            if(CollectionUtils.isEmpty(holderIncreaseDTOList)){
                holderIncreaseDTOList = this.queryHolderIncreaseList();
            }

            return Optional.ofNullable(holderIncreaseDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> balanceDTO.getChange_num() >50 || balanceDTO.getAfter_change_rate() >0.05)
                    .sorted(Comparator.comparing(DongChaiHolderIncreaseDTO::getNotice_date).reversed())
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解禁信息
     *
     * @param code
     * @return
     */
    public DongChaiFreeShareDTO getFreeShareDTO(String code){
        try {
            if(CollectionUtils.isEmpty(freeShareDTOList)){
                freeShareDTOList = this.queryFreeShareDTOList();
            }

            return Optional.ofNullable(freeShareDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> balanceDTO.getFree_share_num() >100 || balanceDTO.getTotal_ratio() >0.1)
                    .sorted(Comparator.comparing(DongChaiFreeShareDTO::getTotal_ratio).reversed())
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询最近日期的沪港通数据
     */
    public List<DongChaiNorthHoldShareDTO> queryLatestNorthHoldShares(List<String> stockCodeList){
        if(CollectionUtils.isEmpty(stockCodeList)){
            return Lists.newArrayList();
        }

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
                        .sorted(Comparator.comparing(DongChaiNorthHoldShareDTO::getTradeDate).reversed())
                        .findFirst()
                        .orElse(null);
                if(tmpHoldShareDTO !=null){
                    latestHoldShareDTOList.add(tmpHoldShareDTO);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return latestHoldShareDTOList;
    }

}
