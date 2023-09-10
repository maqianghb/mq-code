package com.example.mq.wrapper.stock.model.dongchai;

import lombok.Data;

@Data
public class DongChaiFinanceNoticeDTO {

    private String code;

    private String name;

    /**
     * 预告时间
     */
    private String notice_date;

    /**
     * 报告期
     */
    private String report_date;

    /**
     * 预告指标
     */
    private String predict_indicator;

    /**
     * 预告类型
     */
    private String predict_type;

    /**
     * 预告值(亿)
     */
    private Double predict_amount_low;

    /**
     * 预告值(亿)
     */
    private Double predict_amount_up;

    /**
     * 同比变动
     */
    private Double add_amp_low;

    /**
     * 同比变动
     */
    private Double add_amp_up;

    /**
     * 环比变动
     */
    private Double predict_ratio_low;

    /**
     * 环比变动
     */
    private Double predict_ratio_up;

    /**
     * 上年同期值(亿)
     */
    private Double pre_year_same_period;

    /**
     * 变动原因
     */
    private String reason;

}
