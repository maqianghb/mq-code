package com.example.mq.wrapper.stock.manager;

import com.example.mq.wrapper.stock.enums.FinanceReportTypeEnum;
import com.example.mq.wrapper.stock.model.AnalyseIndicatorElement;

/**
 * @Author: maqiang
 * @CreateTime: 2025-09-20 11:01:18
 * @Description:
 */
public interface IndicatorElementManager {

    /**
     * 查询指标源数据
     *
     * @param code
     * @param year
     * @param typeEnum
     * @param kLineDate
     * @return
     */
     AnalyseIndicatorElement queryIndicatorElement(String code, Integer year, FinanceReportTypeEnum typeEnum, String kLineDate);

}
