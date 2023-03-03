package com.example.mq.wrapper.stock.constant;

public class StockConstant {

    public static final String K_LINE_URL ="https://stock.xueqiu.com/v5/stock/chart/kline.json";
    public static final String BALANCE_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/balance.json";
    public static final String INCOME_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/income.json";
    public static final String CASH_FLOW_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/cash_flow.json";
    public static final String INDICATOR_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/indicator.json";

    public static final String COOKIE ="device_id=dda0a4a2c4c9181929b82ccadaa4ec1d; s=cg11wwxa5w; xq_a_token=7da3658c0a79fd9ef135510bc5189429ce0e3035; xqat=7da3658c0a79fd9ef135510bc5189429ce0e3035; xq_r_token=c4e290f788b8c24ec35bd4b893dc8fa427e1f229; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY3OTk2MjkxMiwiY3RtIjoxNjc3ODQ0NDkxMDk4LCJjaWQiOiJkOWQwbjRBWnVwIn0.E9EgWsEW-1lq-6kwCW6dHCdBfPR0lDOG79210akFJb3JNpgvf47lDivqcMyiOV6zDoJw9n4grFHaTeKoCzKmPdO2-hyt69V9bu2u6xCpQymirmyHYg7SU14M_K2Sp4XvXyGzpb2NV3YZdnwCnvkAI1B4KjukOhwFEXssBPCyTzKYM-xkwIP2RhSitmawnMz57a663vu9s9f0tzBVmebmxlU2eWN3vNS106hUn2alsXGataOIGBplZrV9vFWey0F49-AfMUAIfn9q4M2LGOI2aLDeMmAeyMuAnObXM0LKKf5dKuWNfYtdEdJMsjCkJHEFMq819cDjN8OlgfghhLEEZA; u=861677844525180; Hm_lvt_1db88642e346389874251b5a1eded6e3=1677241321,1677315433,1677682483,1677844525; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1677844542";

    public static final Integer COUNT =20;

    public static final String SH_STOCK_CODE_LIST ="E:/stock_data/stock_code_1_sh.txt";
    public static final String SZ_STOCK_CODE_LIST ="E:/stock_data/stock_code_2_sz.txt";

    public static final String BALANCE_LIST ="E:/stock_data/xq_balance_list.txt";
    public static final String INCOME_LIST ="E:/stock_data/xq_income_list.txt";
    public static final String CASH_FLOW_LIST ="E:/stock_data/xq_cash_flow_list.txt";
    public static final String INDICATOR_LIST_XQ ="E:/stock_data/xq_indicator_list.txt";
    public static final String KLINE_LIST_DAY ="E:/stock_data/xq_k_line/k_line_list_day_%s.txt";

    public static final String INCOME_LIST_Q ="E:/stock_data/xq_income_list_q.txt";
    public static final String INDICATOR_LIST_ANALYSIS ="E:/stock_data/indicator_list_analysis.csv";

}
