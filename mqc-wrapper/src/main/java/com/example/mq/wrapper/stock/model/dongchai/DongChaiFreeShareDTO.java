package com.example.mq.wrapper.stock.model.dongchai;

import lombok.Data;

@Data
public class DongChaiFreeShareDTO {

    private String code;

    private String name;

    /**
     * 解禁时间
     */
    private String free_date;

    /**
     * 解禁类型
     */
    private String free_type;

    /**
     * 解禁数量(万股)
     */
    private Double free_share_num;

    /**
     * 占流通市值的比例(%)
     */
    private Double free_ratio;

    /**
     * 占总市值的比例(%)
     */
    private Double total_ratio;

}
