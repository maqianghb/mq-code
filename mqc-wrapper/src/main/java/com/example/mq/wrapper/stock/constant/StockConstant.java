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

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=173d1b86b97861e4a0ecbe2d031fbd057d337248; xqat=173d1b86b97861e4a0ecbe2d031fbd057d337248; xq_r_token=ee8e80a187bf70af8a22704223d871770297dd64; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY4Mzg1MTMyOSwiY3RtIjoxNjgxNTIyODkyODgwLCJjaWQiOiJkOWQwbjRBWnVwIn0.KcA9nm1rQ5manGvKhN-SMvmG5hDiFoCy3802cu-34cdBZRVLVIc22uRRqKMNpa-ODCIuAC6b-gdgv0eiCBviFzqcmwb55UGgR0NfAeqjNLhmP4LpUX-V6wMoRvTyO8Zy12FFDihEqvwG03tUtSXV67VLE_bKZabmFUEfFNGONlixZIGYhrp1LetXjvIThMPvUk5Q9P-h_93unPYzhxnD_p5AZFPZiP3yVYEhmQiKS7d1tQqkKqobCd-IQDFpwb6kGm-IgwqIzAm-RrTnVdoOt13wVaLVjJkL4ZqI-gVykX7ispwlHUhz9tC2GyPLXBqSYPV2xhiCBD3CZsS_6Cie-A; u=931681522938737; is_overseas=0; Hm_lvt_1db88642e346389874251b5a1eded6e3=1680689280,1680861460,1680926109,1681522938; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1681522938";

    public static final List<ImmutablePair<String, String>> STATISTICS_CODE_LIST = Arrays.asList(
            new ImmutablePair<>("SH000300", "沪深300"),
            new ImmutablePair<>("SH000905", "中证500"),
            new ImmutablePair<>("SZ399673", "创业板50"));

    public static final Integer COUNT =30;
    public static final String FILE_DATE ="20230414";

    public static final String SH_STOCK_LIST ="E:/stock_data/stock_list_1_sh.txt";
    public static final String SZ_STOCK_LIST ="E:/stock_data/stock_list_2_sz.txt";
    public static final String CYB_STOCK_LIST ="E:/stock_data/stock_list_3_cyb.txt";

    public static final String SH_STOCK_CODE_LIST ="E:/stock_data/stock_code_1_sh.txt";
    public static final String SZ_STOCK_CODE_LIST ="E:/stock_data/stock_code_2_sz.txt";
    public static final String CYB_STOCK_CODE_LIST ="E:/stock_data/stock_code_3_cyb.txt";

    public static final String INDICATOR_LIST_ANALYSIS ="E:/stock_data/analysis_%s_%s.csv";
    public static final String INDICATOR_LIST_PERCENT ="E:/stock_data/percent_%s_%s.csv";
    public static final String INDICATOR_LIST_FILTER ="E:/stock_data/filter_%s_%s.csv";

    public static final String KLINE_LIST_DAY ="E:/stock_data/xq_k_line_%s/k_line_list_day_%s.txt";

    public static final String BALANCE_LIST ="E:/stock_data/xq_finance_%s/xq_balance_list.txt";
    public static final String INCOME_LIST ="E:/stock_data/xq_finance_%s/xq_income_list.txt";
    public static final String CASH_FLOW_LIST ="E:/stock_data/xq_finance_%s/xq_cash_flow_list.txt";
    public static final String INDICATOR_LIST_XQ ="E:/stock_data/xq_finance_%s/xq_indicator_list.txt";
    public static final String INCOME_LIST_Q ="E:/stock_data/xq_finance_%s/xq_income_list_q.txt";

}
