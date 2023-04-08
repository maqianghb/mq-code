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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=ed22783ba339eb1ffc67ec307758bcb3a61b82dd; xqat=ed22783ba339eb1ffc67ec307758bcb3a61b82dd; xq_r_token=8c8e9c78536d2e07227d66416144fd494874bda0; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY4MjU1NTI0MywiY3RtIjoxNjgwODYxNDE5MjMwLCJjaWQiOiJkOWQwbjRBWnVwIn0.fGK55FziI5WtXhnSJ_8berIlt1Xnq7Cu-CZNVvkJ9lT1WxyPnVXKvNHiuncOT6MIiQ6U-HsD0eOEzxxxBcM3QsCSvRVO-urpFcWLxvPKC71hmlmfrny8gVArYDKyOWtUg-qnJZ-DYVJW1piIT67EuBmuRUtEi5aHGanaSzKw90hv0VBY5jusHguNcGt1VRRxugz4A-GxUfXPHijS07GjBuEREYwd4MFUngylKYiqsPH8oQjCKiUy05iFKdOr4k4T9aFzLDU8DQsFzElSi849hmU05341x00f_tvq9knAyJfRBCK1ct_CvoP-0IZr7t7y2EM1lu9m_21T24oBYhE8ng; u=911680861459759; Hm_lvt_1db88642e346389874251b5a1eded6e3=1679662315,1680431353,1680689280,1680861460; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1680861480";

    public static final List<ImmutablePair<String, String>> STATISTICS_CODE_LIST = Arrays.asList(
            new ImmutablePair<>("SH000300", "沪深300"),
            new ImmutablePair<>("SH000905", "中证500"),
            new ImmutablePair<>("SZ399673", "创业板50"));

    public static final Integer COUNT =30;
    public static final String FILE_DATE ="20230402";

    public static final String SH_STOCK_LIST ="E:/stock_data/stock_list_1_sh.txt";
    public static final String SZ_STOCK_LIST ="E:/stock_data/stock_list_2_sz.txt";
    public static final String CYB_STOCK_LIST ="E:/stock_data/stock_list_3_cyb.txt";

    public static final String SH_STOCK_CODE_LIST ="E:/stock_data/stock_code_1_sh.txt";
    public static final String SZ_STOCK_CODE_LIST ="E:/stock_data/stock_code_2_sz.txt";
    public static final String CYB_STOCK_CODE_LIST ="E:/stock_data/stock_code_3_cyb.txt";

    public static final String INDICATOR_LIST_ANALYSIS ="E:/stock_data/analysis_%s_%s.csv";

    public static final String KLINE_LIST_DAY ="E:/stock_data/xq_k_line_%s/k_line_list_day_%s.txt";

    public static final String BALANCE_LIST ="E:/stock_data/xq_finance_%s/xq_balance_list.txt";
    public static final String INCOME_LIST ="E:/stock_data/xq_finance_%s/xq_income_list.txt";
    public static final String CASH_FLOW_LIST ="E:/stock_data/xq_finance_%s/xq_cash_flow_list.txt";
    public static final String INDICATOR_LIST_XQ ="E:/stock_data/xq_finance_%s/xq_indicator_list.txt";
    public static final String INCOME_LIST_Q ="E:/stock_data/xq_finance_%s/xq_income_list_q.txt";

}
