package com.example.mq.test.stock.model;

import lombok.Data;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-30 17:31:04
 * @Description: 东财行业业绩预告指标
 */
@Data
public class DcIndFinanceNoticeDTO {

    /**
     * 查询日期，格式: yyyy-MM-dd
     */
    private String noticeDate;

    /**
     * 报告期，格式: yyyy-MM-dd
     */
    private String reportDate;

    /**
     * 预告指标
     */
    private String predictIndicator;

    /**
     * 行业
     */
    private String indName;

    /**
     * 总数量
     */
    private Integer allNum;

    /**
     * 好转数量
     */
    private Long betterNum;

    /**
     * 好转比例
     */
    private Double betterRate;

    /**
     * 恶化数量
     */
    private Long worseNum;

    /**
     * 恶化比例
     */
    private Double worseRate;

}
