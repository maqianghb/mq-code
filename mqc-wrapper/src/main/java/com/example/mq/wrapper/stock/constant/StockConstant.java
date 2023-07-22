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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=57b2a0b86ca3e943ee1ffc69509952639be342b9; xqat=57b2a0b86ca3e943ee1ffc69509952639be342b9; xq_r_token=59c1392434fb1959820e4323bb26fa31dd012ea4; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY5MDMzMTY5OCwiY3RtIjoxNjg4Nzk3MDc4MjI1LCJjaWQiOiJkOWQwbjRBWnVwIn0.Dc6v1KJ2I2LfsPsiM__ls-9Lqi7ulnsY-rjMPkgB1eXreBseL2PqLlqdxBKdgP47_SqaIOQePnIJXdLyRk1UPrdj_fp170uZOls6KiHwnSQ8wn48ZHMz1MMOC3XGIFacpLpcsJ6xO9ZvZRE71sNiud5-rRtuDf5VP7pBvRmuUzpBNtLHlYA506k6LOeIlqMv6T6mJ6QIB3X9DkpoRpWhJXmNDT1A3ArrqTz68JJlUDLa4Pop6p_gfMeYA0DFYCI7rvou3_kFJJ1RwWXg0LVFUDR0gGj1GwBegoXd_fBf8xHr060VZt3j64KrkLQik8Nm00g4JcpJ7ZiDeG0AvDZv_A; u=561688797122017; Hm_lvt_1db88642e346389874251b5a1eded6e3=1686362238,1687072501,1688797123; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1688797129";

    public static final List<ImmutablePair<String, String>> STATISTICS_CODE_LIST = Arrays.asList(
            new ImmutablePair<>("SH000300", "沪深300"),
            new ImmutablePair<>("SH000905", "中证500"),
            new ImmutablePair<>("SZ399673", "创业板50"));

    public static final Integer COUNT =30;
    public static final String FILE_DATE ="20230708";

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

}
