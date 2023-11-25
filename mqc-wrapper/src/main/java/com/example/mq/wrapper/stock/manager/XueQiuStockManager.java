package com.example.mq.wrapper.stock.manager;

import com.example.mq.wrapper.stock.model.*;

import java.util.List;

public interface XueQiuStockManager {

    /**
     * 查询K线数据
     *
     * @param code 编码
     * @param type K线类型
     * @param count 查询数量
     * @return
     */
    List<XueQiuStockKLineDTO> queryKLineList(String code, String type, Long endTimeStamp, Integer count);

    /**
     * 查询资产负债数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    List<XueQiuStockBalanceDTO> queryBalanceList(String code, Integer count);

    /**
     * 查询利润数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    List<XueQiuStockIncomeDTO> queryIncomeList(String code, Integer count);

    /**
     * 查询现金流数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
     List<XueQiuStockCashFlowDTO> queryCashFlowList(String code, Integer count);

    /**
     * 查询指标数据
     * @param code 编码
     * @param count 数据条数
     * @return
     */
    List<XueQiuStockIndicatorDTO> queryIndicatorList(String code, Integer count);

    /**
     * 查询公司信息
     *
     * @param code 编码
     * @return
     */
    CompanyDTO queryCompanyDTO(String code);

}
