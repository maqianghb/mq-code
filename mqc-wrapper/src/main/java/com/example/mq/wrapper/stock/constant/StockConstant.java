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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=bf4ca35131318f0118658f3f4790584a66d8bb83; xqat=bf4ca35131318f0118658f3f4790584a66d8bb83; xq_r_token=3374a327172eff6197f4933bdfe11278fc6234ee; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY4NTE0NzQzMywiY3RtIjoxNjgyNzMyMTUyMDY2LCJjaWQiOiJkOWQwbjRBWnVwIn0.ElqnfFbOmc_Pggmwtl2SNoqRR-kdlcL_x7yq2KzuHULCHKEswgZpdNXgddPVS6HQSH6eDiAq_qWTjpI6TO1OlB6f4o8FOZR1qeW0A1T3Y8aTvv080o47iD8sDZrqlf4GZkuXG-bU-eH2mg09g9nbDiH1fhW3tNW6KJCRs7WrsdtDrc_zXRrJWQ-5Uk3x8afFqr2o5NUfYP2WSF3aD2ASzioII85NTg4dsnwOdYfroHbrmgxk8o3_0ZXgO1fHANNTGElgWZA6SPA8knXMhm_M0RHH9MALHacuUv9O6BCWpwwD4Mc9e6oyZAotBOOg-N4cXJIMLmVOMQX9HwainmRhtw; u=501682732185549; Hm_lvt_1db88642e346389874251b5a1eded6e3=1680861460,1680926109,1681522938,1682732186; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1682732193";

    public static final List<ImmutablePair<String, String>> STATISTICS_CODE_LIST = Arrays.asList(
            new ImmutablePair<>("SH000300", "沪深300"),
            new ImmutablePair<>("SH000905", "中证500"),
            new ImmutablePair<>("SZ399673", "创业板50"));

    public static final Integer COUNT =30;
    public static final String FILE_DATE ="20230428";

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
