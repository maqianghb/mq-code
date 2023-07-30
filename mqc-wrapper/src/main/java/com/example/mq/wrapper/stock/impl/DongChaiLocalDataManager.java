package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.CloseableHttpClientUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.model.AnalyseIndicatorDTO;
import com.example.mq.wrapper.stock.model.DongChaiFinanceNoticeDTO;
import com.example.mq.wrapper.stock.model.DongChaiHolderIncreaseDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class DongChaiLocalDataManager {

    private static final String NOTICE_HEADER ="编码,名称,预告时间,报告期,预告指标,预告类型,预告值(亿),预告值(亿),同比变动,同比变动," +
            "环比变动,环比变动,上年同期值(亿),变动原因";
    private static final String HOLDER_INCREASE_HEADER ="编码,名称,预告时间,增减类型,增减数量(万股),减持开始时间,减持结束时间,变动比例(%)," +
            "变动后持股比例(%)";

    public static void main(String[] args) {
        DongChaiLocalDataManager manager =new DongChaiLocalDataManager();

        String reportDate ="2023-06-30";
        List<DongChaiFinanceNoticeDTO> noticeDTOList = manager.queryAndSaveFinanceNotice(reportDate);
        System.out.println("noticeDTOList: " + JSON.toJSONString(noticeDTOList));

        List<DongChaiHolderIncreaseDTO> increaseDTOList = manager.queryHolderIncreaseList();
        System.out.println("increaseDTOList: " + JSON.toJSONString(increaseDTOList));

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
                            noticeDTO.setCode(jsonColumn.getString("SECURITY_CODE"));
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
        stockCodeList = Optional.ofNullable(stockCodeList).orElse(Lists.newArrayList()).stream()
                .map(tmpStockCode -> tmpStockCode.substring(2))
                .collect(Collectors.toList());
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
                            increaseDTO.setCode(jsonColumn.getString("SECURITY_CODE"));
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
        stockCodeList = Optional.ofNullable(stockCodeList).orElse(Lists.newArrayList()).stream()
                .map(tmpStockCode -> tmpStockCode.substring(2))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(stockCodeList)){
            return Lists.newArrayList();
        }

        List<String> strIncreaseList =Lists.newArrayList();
        strIncreaseList.add(HOLDER_INCREASE_HEADER);
        for(DongChaiHolderIncreaseDTO increaseDTO : increaseDTOList){
            if(!stockCodeList.contains(increaseDTO.getCode())){
                continue;
            }

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

}
