package com.example.mq.wrapper.stock.model.dongchai;

import lombok.Data;

@Data
public class DongChaiHolderIncreaseDTO {

    private String code;

    private String name;

    /**
     * 预告时间, 格式：yyyy-MM-dd HH:mm:ss
     */
    private String notice_date;

    /**
     * 增减持类型
     */
    private String direction;

    /**
     * 增减数量(万股)
     */
    private Double change_num;

    /**
     * 减持开始时间
     */
    private String start_date;

    /**
     * 减持结束时间
     */
    private String end_date;

    /**
     * 变动比例(%)
     */
    private Double after_change_rate;

    /**
     * 变动后持股比例(%)
     */
    private Double hold_ratio;

}
