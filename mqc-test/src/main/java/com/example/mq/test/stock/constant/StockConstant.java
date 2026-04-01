package com.example.mq.test.stock.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class StockConstant {

    public static final String K_LINE_URL ="https://stock.xueqiu.com/v5/stock/chart/kline.json";
    public static final String BALANCE_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/balance.json";
    public static final String INCOME_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/income.json";
    public static final String CASH_FLOW_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/cash_flow.json";
    public static final String INDICATOR_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/indicator.json";
    public static final String COMPANY_URL ="https://stock.xueqiu.com/v5/stock/f10/cn/company.json";

    public static final String COOKIE = "u=551703859817291; HMACCOUNT=C04B42F6BEF1AFBB; _c_WBKFRo=zCeQHLhw7OZW9cK1Z0YKLBQmjpWEzLButXn52ozD; _nb_ioWEgULi=; s=bm12pqr4u4; cookiesu=261769608447300; device_id=631f3b7f2c8df1f45644448b974585f1; Hm_lvt_1db88642e346389874251b5a1eded6e3=1773316326; xq_a_token=661c0a951f439e599bc8cadb45d369aa7ac8951b; xqat=661c0a951f439e599bc8cadb45d369aa7ac8951b; xq_r_token=4ae5afaa5bb86d6b8333611fe030f49578d4a018; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTc3NzE2NjM5OSwiY3RtIjoxNzc1MDA5NjcwNjE3LCJjaWQiOiJkOWQwbjRBWnVwIn0.SF9lNjZ6iV4MvavnoTWqLtEvgdO_brZhyCGMs4nB_QrmJfePYxgL11Q-QH0h5SlnVsoF0VBU5i-mjW9PSHLVYtnARs0G3lqKqnN3k26LdqEC3Rj0d3UEjA-7wfqf9_ejv1Qsh13GzCxL9O0Y59Cgubcn4XSF0RdKK_UdG8hpG4eg8jIazwTNAKtxkX-MACJRyZ4oc1Jc6U4J6CFYdOIHypomHerDTiF62X4FGbcnPw0VN9UlziB3KwTo2ejPhO3aZjRlfQqOc6bLYzPS30qTTKIVpnviN9S098wFDcb39F2V8HJdpbMXU3p9GeuGX108fCB1a_OAfJEYJ38RecsdtQ; is_overseas=0;";

    public static final List<String> FOCUS_ETF_CODE_LIST = Arrays.asList(
            "SZ399330,深证100", "SH000300,沪深300", "SH000905,中证500", "SH000852,中证1000", "SZ399673,创业板50",
            "SH000688,科创50", "SH513100,纳指ETF", "SH512480,半导体ETF", "SZ159992,创新药ETF", "SH518880,黄金ETF",
            "SZ160723,嘉实原油");

    public static final List<String> TEST_STOCK_CODE_LIST = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");



    public static final String DONG_CHAI_URL ="https://datacenter-web.eastmoney.com/api/data/v1/get";
    public static final Integer DONG_CHAI_MAX_LIMIT =500;
    public static final Integer FINANCE_REPORT_COUNT =30;
    public static final Integer KLINE_DAY_COUNT =2000;
    public static final List<String> FINANCE_PREDICT_INDICATOR =Arrays.asList("扣除非经常性损益后的净利润");
    public static final List<String> FINANCE_PREDICT_INCREASE =Arrays.asList("减亏", "略增", "扭亏", "续赢", "预增");



    // ****************************** stock信息 *********************************
    public static final String ALL_STOCK_CODE_LIST ="/Users/maqiang/Documents/002-stock_data/001_stock_list/all_stock_code_list.txt";
    public static final String ALL_COMPANY_LIST ="/Users/maqiang/Documents/002-stock_data/001_stock_list/all_company_list.txt";
    public static final String STOCK_LIST_WHITE ="/Users/maqiang/Documents/002-stock_data/001_stock_list/stock_list_white.txt";
    public static final String STOCK_LIST_BLACK ="/Users/maqiang/Documents/002-stock_data/001_stock_list/stock_list_black.txt";
    public static final String FOCUS_CODE_LIST ="/Users/maqiang/Documents/002-stock_data/001_stock_list/focus_code_list.txt";
    public static final String STATISTICS_CODE_LIST ="/Users/maqiang/Documents/002-stock_data/001_stock_list/statistics_code_list.txt";



    // ****************************** 各指标源数据 *********************************
    public static final String KLINE_LIST_DAY ="/Users/maqiang/Documents/002-stock_data/002_xq_k_line/k_line_list_day_%s.txt";
    public static final String BALANCE_LIST ="/Users/maqiang/Documents/002-stock_data/003_xq_finance_balance/xq_balance_%s.txt";
    public static final String INCOME_LIST ="/Users/maqiang/Documents/002-stock_data/004_xq_finance_income/xq_income_%s.txt";
    public static final String INCOME_LIST_Q ="/Users/maqiang/Documents/002-stock_data/005_xq_finance_income_q/xq_income_q_%s.txt";
    public static final String CASH_FLOW_LIST ="/Users/maqiang/Documents/002-stock_data/006_xq_finance_cash_flow/xq_cash_flow_%s.txt";
    public static final String INDICATOR_LIST_XQ ="/Users/maqiang/Documents/002-stock_data/007_xq_finance_indicator/xq_indicator_%s.txt";

    // 东财数据
    public static final String NORTH_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/008_dc_north_hold_shares/north_hold_shares_%s.txt";
    public static final String NORTH_HOLD_SHARES_IND_FILE ="/Users/maqiang/Documents/002-stock_data/009_dc_north_hold_shares_ind/north_hold_shares_ind_%s.txt";



    // ****************************** 分析结果 *********************************
    public static final String INDICATOR_LIST_ANALYSIS ="/Users/maqiang/Documents/analysis_all_%s.csv";
    public static final String INDICATOR_LIST_FILTER ="/Users/maqiang/Documents/analysis_filter_%s.csv";
    public static final String INDICATOR_LIST_PERCENT ="/Users/maqiang/Documents/percent_%s.csv";
    public static final String INDICATOR_LIST_PERCENT_IND ="/Users/maqiang/Documents/percent_ind_%s.csv";
    public static final String STATISTICS_MA_1000_PERCENT_LIST ="/Users/maqiang/Documents/statistics_ma_1000_percent_%s.csv";

    // 预告数据
    public static final String FINANCE_NOTICE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_finance_notice_code_%s.csv";
    public static final String IND_FINANCE_NOTICE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_finance_notice_ind_%s.csv";

    // 增减持数据
    public static final String HOLDER_INCREASE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_holder_increase_%s.csv";
    public static final String LATEST_HOLD_SHARES_FILE ="/Users/maqiang/Documents/north_hold_shares_%s.csv";
    public static final String LATEST_IND_HOLD_SHARES_FILE ="/Users/maqiang/Documents/north_hold_shares_ind_%s.csv";


    public static String getPredictIncreaseType(String predictType){
        switch(predictType){
            case "减亏":
            case "略增":
            case "扭亏":
            case "续盈":
            case "预增":{
                return "好转";
            }
            case "略减":
            case "首亏":
            case "续亏":
            case "预减":
            case "增亏":{
                return "恶化";
            }
        }

        return StringUtils.EMPTY;
    }

}
