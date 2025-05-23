package com.example.mq.wrapper.stock.constant;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Arrays;
import java.util.List;

public class StockConstant {

    public static final String K_LINE_URL ="https://stock.xueqiu.com/v5/stock/chart/kline.json";
    public static final String BALANCE_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/balance.json";
    public static final String INCOME_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/income.json";
    public static final String CASH_FLOW_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/cash_flow.json";
    public static final String INDICATOR_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/indicator.json";
    public static final String COMPANY_URL ="https://stock.xueqiu.com/v5/stock/f10/cn/company.json";

    public static final String COOKIE ="s=c117ssz7g3; u=551703859817291; HMACCOUNT=C04B42F6BEF1AFBB; cookiesu=781737983540813; device_id=631f3b7f2c8df1f45644448b974585f1; xq_a_token=9773bacc11404cb5ac8b0847c564eda3730e6b61; xqat=9773bacc11404cb5ac8b0847c564eda3730e6b61; xq_r_token=caf75c71460c20a680e6e3c455cadcca5c1be14a; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTc0NzM1NzE4MywiY3RtIjoxNzQ0ODk3MDYxODIxLCJjaWQiOiJkOWQwbjRBWnVwIn0.i4_HRt-z7SzRcNnDwT67Y4DkV137bx85p8RK3bVHBV9KA5kWRitQKNkEiKXkps7PjkDIBtieRjosMVaIP2SHXHoducGLANkVSMhqPIpFhSQGb-hv8GC4RQtPQKFZPc_a1tXYykjx7B9Cc3VigndliHOzkLVNyTzOMP6S0fLQ9PJUuKaMjQByTZ2RceyCyKb1pIEyLBUN4Ik65SSnWtblFufm_T0DvBDGQG95ojKSqUnuRCBc1n2wKTDMg37u29_qX6ajtvxAC9BE9QE2iEb6jSIzFWfjT8WgqRy-ajAKWd5C9w485XiLWTISBzV1eDgVUU1UbLf4SzQv1GIx4qkNYQ; Hm_lvt_1db88642e346389874251b5a1eded6e3=1744897093; is_overseas=0;";

    public static final List<ImmutablePair<String, String>> STATISTICS_CODE_LIST = Arrays.asList(
            new ImmutablePair<>("SH000016", "上证50"),
            new ImmutablePair<>("SH000300", "沪深300"),
            new ImmutablePair<>("SZ399330", "深证100"),
            new ImmutablePair<>("SH000905", "中证500"),
            new ImmutablePair<>("SH000852", "中证1000"),
            new ImmutablePair<>("SZ399673", "创业板50"),
            new ImmutablePair<>("SH512880", "证券ETF"),

            new ImmutablePair<>("SZ002001", "新和成"),
            new ImmutablePair<>("SZ000895", "双汇发展"),
            new ImmutablePair<>("SZ000858", "五粮液"),
            new ImmutablePair<>("SZ002415", "海康威视"),
            new ImmutablePair<>("SZ000333", "美的集团"),
            new ImmutablePair<>("SH600585", "海螺水泥"),
            new ImmutablePair<>("SZ000725", "京东方A"));

    public static final List<String> TEST_STOCK_CODE_LIST = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");

    public static final List<String> FINANCE_PREDICT_INDICATOR =Arrays.asList("营业收入", "扣除非经常性损益后的净利润");

    public static final List<String> FINANCE_PREDICT_INCREASE =Arrays.asList("减亏", "略增", "扭亏", "续赢", "预增");

    public static final Integer FINANCE_REPORT_COUNT =30;
    public static final Integer KLINE_DAY_COUNT =2000;

    public static final String STOCK_LIST ="/Users/maqiang/Documents/002-stock_data/001_stock_list/stock_list.txt";
    public static final String COMPANY_LIST ="/Users/maqiang/Documents/002-stock_data/001_stock_list/company_list.txt";

    public static final String STOCK_LIST_WHITE ="/Users/maqiang/Documents/002-stock_data/001_stock_list/stock_list_white.txt";
    public static final String STOCK_LIST_BLACK ="/Users/maqiang/Documents/002-stock_data/001_stock_list/stock_list_black.txt";

    public static final String KLINE_LIST_DAY ="/Users/maqiang/Documents/002-stock_data/002_xq_k_line/k_line_list_day_%s.txt";

    public static final String BALANCE_LIST ="/Users/maqiang/Documents/002-stock_data/003_xq_finance_balance/xq_balance_%s.txt";
    public static final String INCOME_LIST ="/Users/maqiang/Documents/002-stock_data/004_xq_finance_income/xq_income_%s.txt";
    public static final String CASH_FLOW_LIST ="/Users/maqiang/Documents/002-stock_data/006_xq_finance_cash_flow/xq_cash_flow_%s.txt";
    public static final String INDICATOR_LIST_XQ ="/Users/maqiang/Documents/002-stock_data/007_xq_finance_indicator/xq_indicator_%s.txt";
    public static final String INCOME_LIST_Q ="/Users/maqiang/Documents/002-stock_data/005_xq_finance_income_q/xq_income_q_%s.txt";

    public static final String INDICATOR_LIST_ANALYSIS ="/Users/maqiang/Documents/002-stock_data/analysis_all_%s_%s.csv";

    public static final String INDICATOR_LIST_FILTER ="/Users/maqiang/Documents/002-stock_data/analysis_filter_%s_%d_%s.csv";

    public static final String INDICATOR_LIST_PERCENT ="/Users/maqiang/Documents/002-stock_data/percent_%s_%s.csv";

    public static final String INDICATOR_LIST_PERCENT_IND ="/Users/maqiang/Documents/002-stock_data/percent_ind_%s_%s.csv";


    public static final String DONG_CHAI_URL ="https://datacenter-web.eastmoney.com/api/data/v1/get";
    public static final Integer DONG_CHAI_MAX_LIMIT =500;

    public static final String FINANCE_NOTICE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_finance_%s_%s.csv";

    public static final String IND_FINANCE_NOTICE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_finance_ind_%s_%s.csv";
    public static final String HOLDER_INCREASE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_holder_increase_%s.csv";


    public static final String NORTH_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/008_dc_north_hold_shares/north_hold_shares_%s.txt";
    public static final String NORTH_HOLD_SHARES_IND_FILE ="/Users/maqiang/Documents/002-stock_data/009_dc_north_hold_shares_ind/north_hold_shares_ind_%s.txt";

    public static final String LATEST_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/north_hold_shares_%s_%s.csv";
    public static final String LATEST_IND_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/north_hold_shares_ind_%s_%s.csv";

    public static final String STATISTICS_MA_1000_PERCENT_LIST ="/Users/maqiang/Documents/002-stock_data/statistics_ma_1000_percent_%s_%s.csv";

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
