package com.example.mq.wrapper.stock.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.CloseableHttpClientUtil;
import com.example.mq.common.utils.DateUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.manager.DongChaiDataManager;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFinanceNoticeDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiFreeShareDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiHolderIncreaseDTO;
import com.example.mq.wrapper.stock.model.dongchai.DongChaiNorthHoldShareDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DongChaiDataManagerImpl implements DongChaiDataManager {

    private static List<DongChaiHolderIncreaseDTO> holderIncreaseDTOList =Lists.newArrayList();
    private static List<DongChaiFreeShareDTO> freeShareDTOList =Lists.newArrayList();

    @Override
    public List<DongChaiFinanceNoticeDTO> queryFinanceNoticeDTO(String reportDate) {
        Integer totalNum = this.queryFinanceNoticeNum(reportDate);
        if(totalNum ==null || totalNum <=0){
            return Lists.newArrayList();
        }

        List<String> allColumnList =Lists.newArrayList();
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
            if(CollectionUtils.isNotEmpty(columnList)){
                allColumnList.addAll(columnList);
            }
        }

        List<DongChaiFinanceNoticeDTO> noticeDTOList = Optional.ofNullable(allColumnList).orElse(Lists.newArrayList()).stream()
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

        return noticeDTOList;
    }

    @Override
    public List<DongChaiHolderIncreaseDTO> queryHolderIncreaseList() {
        int totalNum =5000;

       List<String> allColumnList =Lists.newArrayList();
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
            if(CollectionUtils.isNotEmpty(columnList)){
                allColumnList.addAll(columnList);
            }
        }

        List<DongChaiHolderIncreaseDTO> increaseDTOList = Optional.ofNullable(allColumnList).orElse(Lists.newArrayList()).stream()
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

        return increaseDTOList;
    }

    @Override
    public List<DongChaiFreeShareDTO> queryFreeShareDTOList(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if(startDateTime ==null || endDateTime ==null){
            return Lists.newArrayList();
        }

        String startDate =startDateTime.format(DateTimeFormatter.ISO_DATE);
        String endDate =endDateTime.format(DateTimeFormatter.ISO_DATE);

        int totalNum =5000;
        List<String> allColumnList =Lists.newArrayList();
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
            if(CollectionUtils.isNotEmpty(columnList)){
                allColumnList.addAll(columnList);
            }
        }

        List<DongChaiFreeShareDTO> freeShareDTOList = Optional.ofNullable(allColumnList).orElse(Lists.newArrayList()).stream()
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

        return freeShareDTOList;
    }

    @Override
    public List<DongChaiNorthHoldShareDTO> queryNorthHoldShareDTOList(String stockCode) {
        String url = new StringBuilder().append(StockConstant.DONG_CHAI_URL)
//                .append("?callback=").append("jQuery1123093599956891065_1689992754007")
                .append("?sortColumns=").append("TRADE_DATE")
                .append("&sortTypes=").append("-1")
                .append("&pageSize=").append(StockConstant.DONG_CHAI_MAX_LIMIT)
                .append("&pageNumber=").append(1)
                .append("&reportName=").append("RPT_MUTUAL_HOLDSTOCKNDATE_STA")
                .append("&columns=").append("ALL")
                .append("&source=").append("WEB")
                .append("&client=").append("WEB")
                .append("&filter=").append("(SECUCODE%3D%22")
                .append(stockCode.substring(2)).append(".").append(stockCode.substring(0,2)).append("%22)(INTERVAL_TYPE%3D%221%22)")
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
                    if(jsonColumn.getDouble("HOLD_MARKET_CAP") !=null){
                        double hold_market_cap = NumberUtil.format(jsonColumn.getDouble("HOLD_MARKET_CAP") / (1 *10000 * 10000), 2);
                        northHoldShareDTO.setHoldMarketCap(hold_market_cap);
                    }
                    if (jsonColumn.getDouble("TOTAL_SHARES_RATIO") != null) {
                        double total_shares_ratio = NumberUtil.format(jsonColumn.getDouble("TOTAL_SHARES_RATIO"), 2);
                        northHoldShareDTO.setTotalSharesRatio(total_shares_ratio);
                    }

                    return northHoldShareDTO;
                })
                .collect(Collectors.toList());

        return northHoldShareDTOList;
    }

    @Override
    public DongChaiHolderIncreaseDTO getMaxHolderIncreaseDTO(String code, LocalDateTime startDateTime, LocalDateTime endDateTime){
        if(StringUtils.isBlank(code) || startDateTime ==null || endDateTime ==null){
            return null;
        }

        try {
            if(CollectionUtils.isEmpty(holderIncreaseDTOList)){
                holderIncreaseDTOList = this.queryHolderIncreaseList();
            }

            return Optional.ofNullable(holderIncreaseDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(balanceDTO -> Objects.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO ->{
                        if(StringUtils.isBlank(balanceDTO.getNotice_date())){
                            return false;
                        }

                        LocalDateTime noticeDateTime = DateUtil.parseLocalDateTime(balanceDTO.getNotice_date(), DateUtil.DATE_TIME_FORMAT);

                        return (noticeDateTime.isAfter(startDateTime) || noticeDateTime.isEqual(startDateTime))
                                && noticeDateTime.isBefore(endDateTime);
                    })
                    .sorted(Comparator.comparing(DongChaiHolderIncreaseDTO::getChange_num).reversed())
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public DongChaiFreeShareDTO getMaxFreeShareDTO(String code, LocalDateTime startDateTime, LocalDateTime endDateTime){
        if(StringUtils.isBlank(code) || startDateTime ==null || endDateTime ==null){
            return null;
        }

        try {
            if(CollectionUtils.isEmpty(freeShareDTOList)){
                freeShareDTOList = this.queryFreeShareDTOList(startDateTime, endDateTime);
            }

            return Optional.ofNullable(freeShareDTOList).orElse(Lists.newArrayList()).stream()
                    .filter(balanceDTO -> StringUtils.equals(balanceDTO.getCode(), code))
                    .filter(balanceDTO -> balanceDTO.getTotal_ratio() !=null)
                    .sorted(Comparator.comparing(DongChaiFreeShareDTO::getTotal_ratio).reversed())
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询业绩预告数据条数
     *
     * @return
     */
    private Integer queryFinanceNoticeNum(String reportDate) {
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
    
}
