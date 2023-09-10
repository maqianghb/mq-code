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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; cookiesu=611692494183709; xq_a_token=a0f5e0d91bc0846f43452e89ae79e08167c42068; xqat=a0f5e0d91bc0846f43452e89ae79e08167c42068; xq_r_token=76ed99965d5bffa08531a6a47501f096f61108e8; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY5NTUxNTc5NCwiY3RtIjoxNjkzNTcwMDM4NDgwLCJjaWQiOiJkOWQwbjRBWnVwIn0.SJYDlqq3dOC-y7RZC-KpivPU5HdvuQzBol_XQb-jIFfdp6zOxzm9nvRp8-hZtY5S6_QNm3cUN-LIHrv-veBLpAz2R-7mRt9RSEPwbDj3MwL3azYUb_F7WQeETQ-50Lp5FPM0Q6ZygaAiTEA2tINNI_cnz_ONQXOzApCicwb9YG9GeEoM4aVDsEiswEJ_Fec7GUMAJNyDyMPwaIJ5etOCcgM4_ZIGy7DybX_EGStooqg_-JbgchxLBXuYJrRAaJk5jWC-28uL7muurQ5tq9X_5_ZEMoPKrN9vtit10gY7ep9MwNfdIK31JObq-_zdonwLv1687i7ihZe4f7VktvgHOQ; u=611692494183709; is_overseas=0; Hm_lvt_1db88642e346389874251b5a1eded6e3=1691803896,1692494183,1693114179,1693570096; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1693570096";

    public static final List<ImmutablePair<String, String>> STATISTICS_CODE_LIST = Arrays.asList(
            new ImmutablePair<>("SH000016", "上证50"),
            new ImmutablePair<>("SH000300", "沪深300"),
            new ImmutablePair<>("SH000905", "中证500"),
            new ImmutablePair<>("SZ399673", "创业板50"),
            new ImmutablePair<>("SH512880", "证券ETF"),

            new ImmutablePair<>("SZ002001", "新和成"),
            new ImmutablePair<>("SZ000895", "双汇发展"),
            new ImmutablePair<>("SZ000858", "五粮液"),
            new ImmutablePair<>("SZ002415", "海康威视"),
            new ImmutablePair<>("SZ000333", "美的集团"),
            new ImmutablePair<>("SH600585", "海螺水泥"));

    public static final List<String> TEST_STOCK_CODE_LIST = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");

    public static final Integer FINANCE_REPORT_COUNT =30;
    public static final Integer KLINE_DAY_COUNT =2000;
    public static final String FILE_DATE ="20230901";

    public static final String SH_STOCK_LIST ="E:/stock_data/stock_list_1_sh.txt";
    public static final String SZ_STOCK_LIST ="E:/stock_data/stock_list_2_sz.txt";
    public static final String CYB_STOCK_LIST ="E:/stock_data/stock_list_3_cyb.txt";

    public static final String SH_STOCK_CODE_LIST ="E:/stock_data/stock_code_1_sh.txt";
    public static final String SZ_STOCK_CODE_LIST ="E:/stock_data/stock_code_2_sz.txt";
    public static final String CYB_STOCK_CODE_LIST ="E:/stock_data/stock_code_3_cyb.txt";

    public static final String COMPANY_LIST ="E:/stock_data/company_list.txt";

    public static final String STOCK_LIST_WHITE ="E:/stock_data/stock_list_white.txt";
    public static final String STOCK_LIST_BLACK ="E:/stock_data/stock_list_black.txt";

    public static final String INDICATOR_LIST_ANALYSIS ="E:/stock_data/analysis_%s_%s.csv";
    public static final String INDICATOR_LIST_PERCENT ="E:/stock_data/percent_%s_%s.csv";
    public static final String INDICATOR_LIST_FILTER ="E:/stock_data/filter_%s_%d_%s.csv";

    public static final String KLINE_LIST_DAY ="E:/stock_data/xq_k_line_%s/k_line_list_day_%s.txt";

    public static final String BALANCE_LIST ="E:/stock_data/xq_finance_%s/xq_balance_list.txt";
    public static final String INCOME_LIST ="E:/stock_data/xq_finance_%s/xq_income_list.txt";
    public static final String CASH_FLOW_LIST ="E:/stock_data/xq_finance_%s/xq_cash_flow_list.txt";
    public static final String INDICATOR_LIST_XQ ="E:/stock_data/xq_finance_%s/xq_indicator_list.txt";
    public static final String INCOME_LIST_Q ="E:/stock_data/xq_finance_%s/xq_income_list_q.txt";


    public static final String DONG_CHAI_URL ="https://datacenter-web.eastmoney.com/api/data/v1/get";
    public static final Integer DONG_CHAI_MAX_LIMIT =500;

    public static final String FINANCE_NOTICE_LIST ="E:/stock_data/dc_finance_%s_%s.csv";
    public static final String HOLDER_INCREASE_LIST ="E:/stock_data/dc_holder_increase_%s.csv";


    public static final String NORTH_HOLD_SHARES_FILE ="E:/stock_data/dc_north_hold_shares/north_hold_shares_%s.txt";

}
