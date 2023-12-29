package com.example.mq.wrapper.stock.constant;

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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; cookiesu=611692494183709; xq_a_token=4fda997cf0d3bc4ef43eba42532cf38a54bcbc00; xqat=4fda997cf0d3bc4ef43eba42532cf38a54bcbc00; xq_r_token=a440894245f0f9be071ea5c41d674edb42789120; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTcwMzI5MTg4MiwiY3RtIjoxNzAwODg1ODY0NzMwLCJjaWQiOiJkOWQwbjRBWnVwIn0.PxpzCz1Qux9_3DE7emSZa4LxibCIrO58ELzKJ4tqy4xy4XdCnCXCnHCqJ3Kr3U7qaHts3B7AGryMGX0UnXcRO6pwN8ZX6mgwbUr_Apovnex_DA2ZvIQbnt87jhkzC3lp8b3PUsbiyyA8GeTZTztw9-HaUGNaTPh1TDVyErBXfcAg2M4NHVzJybHPHgMgFx_WyF0PH3UoaKiKEJ09Qu2FMyP7p5Pf0af9Y5oqsJ3pIILKPTGuoB7VBprxjnNR_2JKdtbtdSJreZtNQxpf62WYuA26ggpVnII2BWoiaglSVrdHqCwXCDU1ZwuuERDZRy6YVP9XTcACF85XrcgHYjemsA; u=611692494183709; Hm_lvt_1db88642e346389874251b5a1eded6e3=1698754480,1699063763,1700656578,1700885899; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1700885959";

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

    public static final Integer FINANCE_REPORT_COUNT =30;
    public static final Integer KLINE_DAY_COUNT =2000;
    public static final String FILE_DATE ="20231125";

    public static final String STOCK_LIST ="/Users/maqiang/Documents/002-stock_data/stock_list.txt";
    public static final String COMPANY_LIST ="/Users/maqiang/Documents/002-stock_data/company_list.txt";

    public static final String STOCK_LIST_WHITE ="/Users/maqiang/Documents/002-stock_data/stock_list_white.txt";
    public static final String STOCK_LIST_BLACK ="/Users/maqiang/Documents/002-stock_data/stock_list_black.txt";

    public static final String KLINE_LIST_DAY ="/Users/maqiang/Documents/002-stock_data/xq_k_line/k_line_list_day_%s.txt";

    public static final String BALANCE_LIST ="/Users/maqiang/Documents/002-stock_data/xq_finance/xq_balance_list.txt";
    public static final String INCOME_LIST ="/Users/maqiang/Documents/002-stock_data/xq_finance/xq_income_list.txt";
    public static final String CASH_FLOW_LIST ="/Users/maqiang/Documents/002-stock_data/xq_finance/xq_cash_flow_list.txt";
    public static final String INDICATOR_LIST_XQ ="/Users/maqiang/Documents/002-stock_data/xq_finance/xq_indicator_list.txt";
    public static final String INCOME_LIST_Q ="/Users/maqiang/Documents/002-stock_data/xq_finance/xq_income_list_q.txt";

    public static final String INDICATOR_LIST_ANALYSIS ="/Users/maqiang/Documents/002-stock_data/analysis_%s_%s.csv";
    public static final String INDICATOR_LIST_PERCENT ="/Users/maqiang/Documents/002-stock_data/percent_%s_%s.csv";
    public static final String INDICATOR_LIST_FILTER ="/Users/maqiang/Documents/002-stock_data/filter_%s_%d_%s.csv";

    public static final String DONG_CHAI_URL ="https://datacenter-web.eastmoney.com/api/data/v1/get";
    public static final Integer DONG_CHAI_MAX_LIMIT =500;

    public static final String FINANCE_NOTICE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_finance_%s_%s.csv";
    public static final String HOLDER_INCREASE_LIST ="/Users/maqiang/Documents/002-stock_data/dc_holder_increase_%s.csv";


    public static final String NORTH_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/dc_north_hold_shares/north_hold_shares_%s.txt";
    public static final String NORTH_HOLD_SHARES_IND_FILE ="/Users/maqiang/Documents/002-stock_data/dc_north_hold_shares_ind/north_hold_shares_ind_%s.txt";

    public static final String LATEST_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/north_hold_shares_%s_%s.csv";
    public static final String LATEST_IND_HOLD_SHARES_FILE ="/Users/maqiang/Documents/002-stock_data/industry_north_hold_shares_%s_%s.csv";
}
