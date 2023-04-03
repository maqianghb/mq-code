package com.example.mq.wrapper.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.CloseableHttpClientUtil;
import com.example.mq.wrapper.stock.constant.StockConstant;
import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class XueQiuStockManager {

    /**
     * 查询K线数据
     *
     * @param code 编码
     * @param type K线类型
     * @param count 查询数量
     * @return
     */
    public List<XueQiuStockKLineDTO> queryKLineList(String code, String type, Long endTimeStamp, Integer count) {
        String url =new StringBuilder().append(StockConstant.K_LINE_URL)
                .append("?symbol=").append(code)
                .append("&begin=").append(endTimeStamp)
                .append("&period=").append(type)
                .append("&type=").append("before")
                .append("&count=").append(-count)
                .append("&indicator=").append("kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance")
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StockConstant.COOKIE);
        List<String> columnList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(jsonResult -> JSON.parseArray(jsonResult.getString("column"), String.class))
                .orElse(Lists.newArrayList());
        if(CollectionUtils.isEmpty(columnList)){
            return Lists.newArrayList();
        }

        List<List<Double>> itemValueList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("item"))
                .map(strList -> JSON.parseArray(strList, String.class))
                .orElse(Lists.newArrayList()).stream()
                .map(strValueList -> JSON.parseArray(strValueList, Double.class))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(itemValueList)){
            return Lists.newArrayList();
        }

        List<XueQiuStockKLineDTO> kLineDTOList =Lists.newArrayList();
        for(List<Double> valueList : itemValueList){
            JSONObject jsonValue =new JSONObject();
            if(Objects.equals(columnList.size(), valueList.size())){
                for(int i=0; i<columnList.size(); i++){
                    jsonValue.put(columnList.get(i), valueList.get(i));
                }
            }

            XueQiuStockKLineDTO kLineDTO =JSON.parseObject(JSON.toJSONString(jsonValue), XueQiuStockKLineDTO.class);
            kLineDTO.setCode(code);
            kLineDTO.setType(type);
            kLineDTOList.add(kLineDTO);
        }

        List<XueQiuStockKLineDTO> sortedKLineDTOList = kLineDTOList.stream()
                .sorted(Comparator.comparing(XueQiuStockKLineDTO::getTimestamp).reversed())
                .collect(Collectors.toList());

        return sortedKLineDTOList;
    }

    /**
     * 查询资产负债数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    public List<XueQiuStockBalanceDTO> queryBalanceList(String code, Integer count) {
        String url =new StringBuilder().append(StockConstant.BALANCE_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(StringUtils.EMPTY)
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StockConstant.COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockBalanceDTO> balanceDTOList =Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockBalanceDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockBalanceDTO balanceDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockBalanceDTO.class);
                    return balanceDTO;
                })
                .collect(Collectors.toList());
        balanceDTOList.forEach(balanceDTO -> {
                    balanceDTO.setCode(code);
                    balanceDTO.setName(stockName);
                });
        return balanceDTOList;
    }

    /**
     * 查询利润数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    public List<XueQiuStockIncomeDTO> queryIncomeList(String code, Integer count) {
        String url =new StringBuilder().append(StockConstant.INCOME_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(StringUtils.EMPTY)
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StockConstant.COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockIncomeDTO> incomeDTOList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockIncomeDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockIncomeDTO incomeDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockIncomeDTO.class);
                    return incomeDTO;
                })
                .collect(Collectors.toList());
        incomeDTOList.forEach(incomeDTO -> {
                    incomeDTO.setCode(code);
                    incomeDTO.setName(stockName);
                });

        return incomeDTOList;
    }

    /**
     * 查询现金流数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    public List<XueQiuStockCashFlowDTO> queryCashFlowList(String code, Integer count) {
        String url =new StringBuilder().append(StockConstant.CASH_FLOW_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(StringUtils.EMPTY)
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StockConstant.COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockCashFlowDTO> cashFlowDTOList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockCashFlowDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockCashFlowDTO cashFlowDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockCashFlowDTO.class);
                    return cashFlowDTO;
                })
                .collect(Collectors.toList());
        cashFlowDTOList.forEach(cashFlowDTO -> {
            cashFlowDTO.setCode(code);
            cashFlowDTO.setName(stockName);
        });

        return cashFlowDTOList;
    }

    /**
     * 查询指标数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    public List<XueQiuStockIndicatorDTO> queryIndicatorList(String code, Integer count) {
        String url =new StringBuilder().append(StockConstant.INDICATOR_URL)
                .append("?symbol=").append(code)
                .append("&type=").append("all")
                .append("&is_detail=").append("true")
                .append("&count=").append(count)
                .append("&timestamp=").append(System.currentTimeMillis())
                .toString();

        String strResult = CloseableHttpClientUtil.doGet(url, StockConstant.COOKIE);
        String stockName =this.getStockName(strResult);

        List<XueQiuStockIndicatorDTO> indicatorDTOList = Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(reportJson -> {
                    List<Field> fields = Arrays.asList(XueQiuStockIndicatorDTO.class.getDeclaredFields());
                    JSONObject formatJson = this.getFieldValue(reportJson, fields);
                    XueQiuStockIndicatorDTO indicatorDTO = JSONObject.parseObject(JSON.toJSONString(formatJson), XueQiuStockIndicatorDTO.class);
                    return indicatorDTO;
                })
                .collect(Collectors.toList());
        indicatorDTOList.forEach(indicatorDTO -> {
            indicatorDTO.setCode(code);
            indicatorDTO.setName(stockName);
        });

        return indicatorDTOList;
    }

    private String getStockName(String strResult){
        return Optional.ofNullable(JSONObject.parseObject(strResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("quote_name"))
                .orElse(StringUtils.EMPTY);
    }

    private JSONObject getFieldValue(JSONObject jsonReport, List<Field> fields){
        JSONObject resultJson =new JSONObject();

        Long reportDateMillis = jsonReport.getLong("report_date");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(reportDateMillis).toInstant(), ZoneId.systemDefault());
        Integer year = localDateTime.getYear();
        resultJson.put("report_year", year);

        Integer month = localDateTime.getMonthValue();
        String reportType =StringUtils.EMPTY;
        switch (month){
            case 3:
                reportType = FinanceReportTypeEnum.QUARTER_1.getCode();
                break;
            case 6:
                reportType = FinanceReportTypeEnum.HALF_YEAR.getCode();
                break;
            case 9:
                reportType = FinanceReportTypeEnum.QUARTER_3.getCode();
                break;
            case 12:
                reportType = FinanceReportTypeEnum.ALL_YEAR.getCode();
                break;
            default:
                break;
        }
        if(StringUtils.isNotBlank(reportType)){
            resultJson.put("report_type", reportType);
        }

        for(Field field : fields){
            String key = field.getName();
            if(jsonReport.containsKey(key)){
                List<Double> valueList = JSON.parseArray(jsonReport.getString(key), Double.class);
                if(CollectionUtils.isNotEmpty(valueList)){
                    resultJson.put(key, valueList.get(0));
                }
            }
        }

        return resultJson;
    }

}
