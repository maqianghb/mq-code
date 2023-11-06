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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; cookiesu=611692494183709; xq_a_token=e2f0876e8fd368a0be2b6d38a49ed2dd5eec7557; xqat=e2f0876e8fd368a0be2b6d38a49ed2dd5eec7557; xq_r_token=2a5b753b2db675b4ac36c938d20120660651116d; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTcwMDY5OTg3NSwiY3RtIjoxNjk5MDYzNzA2MTI4LCJjaWQiOiJkOWQwbjRBWnVwIn0.W3WTAUqnluWprvRgiMVPgyjSyU5_HkX007vwyMzdY84ZvfmXSGlynNGlr57LDJNDzGbDMnevU2I_JDisutAOj-5wpBh0EjnNQt3YIccwI1wnUToPj94MgBjc5S0FG86M63Yn9AGIsBZyeLrxrFGEzOVsHKwnRm-Pd6vu0MKrxwaFrLssel-Kz74KqJmB3016gmCEF_y_xYYL_G1v2DpYaLPL-pTMpxnSJGs7b8eG4EVTZ_9XE-6JclLai90y1IvVzRUeqCwluBbP8Mh855YmcxG5qpAWCZ5X90CFxK8-MctDesvZwrjXEIfCCecDAeEtBi0x-bW6DTiCD1sQrND3bA; u=611692494183709; is_overseas=0; Hm_lvt_1db88642e346389874251b5a1eded6e3=1698408979,1698544630,1698754480,1699063763; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1699063763";

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
            new ImmutablePair<>("SH600585", "海螺水泥"),
            new ImmutablePair<>("SZ000725", "京东方A"));

    public static final List<String> TEST_STOCK_CODE_LIST = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");

    public static final Integer FINANCE_REPORT_COUNT =30;
    public static final Integer KLINE_DAY_COUNT =2000;
    public static final String FILE_DATE ="20231104";

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
    public static final String LATEST_HOLD_SHARES_FILE ="E:/stock_data/north_hold_shares_%s_%s.csv";

}
