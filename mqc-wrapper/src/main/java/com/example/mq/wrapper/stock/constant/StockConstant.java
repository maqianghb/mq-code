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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=92653cab19163fc842ad5747ac2c2cdee44c935e; xqat=92653cab19163fc842ad5747ac2c2cdee44c935e; xq_r_token=0f90d6ef86e3c742498591af7860096fe2e3fc86; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY4NzczOTYwMiwiY3RtIjoxNjg1MTY4MDI5ODc3LCJjaWQiOiJkOWQwbjRBWnVwIn0.qZQrIAEH712UBG0NQiKZScZhmX8mx2qACkHNeGHS3VrcwpqlEA322aywpDKFQ6bCnGiJcNaP0ZqnoLu6gnC7jJYebj6s0a6uxS_IKs6a9XuxGAnb47Ynjczryk5FXxyOBEg749NFnuPnCqE3ZYojDHskLO_-1ZpGVa2K2_5GMc1svqt-Pmx-dc6UUqHRW0UMGGB069bDlYQVml9DUCUkuRP-lVtd-piPqf9GyapBUrjkbwLk8CmTry-6rvXsX1bEWiJd0ew4Fg4w7tfwYAf-fQBIGAUSd9TZNaLzrO_XR1lylgbzMuHuQdZMOSf9S6itv7Z9pv2Ihreee12HS1Arfg; u=471685168053683; Hm_lvt_1db88642e346389874251b5a1eded6e3=1682838354,1682912972,1682991917,1685168057; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1685168069dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=92653cab19163fc842ad5747ac2c2cdee44c935e; xqat=92653cab19163fc842ad5747ac2c2cdee44c935e; xq_r_token=0f90d6ef86e3c742498591af7860096fe2e3fc86; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY4NzczOTYwMiwiY3RtIjoxNjg1MTY4MDI5ODc3LCJjaWQiOiJkOWQwbjRBWnVwIn0.qZQrIAEH712UBG0NQiKZScZhmX8mx2qACkHNeGHS3VrcwpqlEA322aywpDKFQ6bCnGiJcNaP0ZqnoLu6gnC7jJYebj6s0a6uxS_IKs6a9XuxGAnb47Ynjczryk5FXxyOBEg749NFnuPnCqE3ZYojDHskLO_-1ZpGVa2K2_5GMc1svqt-Pmx-dc6UUqHRW0UMGGB069bDlYQVml9DUCUkuRP-lVtd-piPqf9GyapBUrjkbwLk8CmTry-6rvXsX1bEWiJd0ew4Fg4w7tfwYAf-fQBIGAUSd9TZNaLzrO_XR1lylgbzMuHuQdZMOSf9S6itv7Z9pv2Ihreee12HS1Arfg; u=471685168053683; Hm_lvt_1db88642e346389874251b5a1eded6e3=1682838354,1682912972,1682991917,1685168057; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1685168069";

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
