package com.example.mq.wrapper.stock.model.dongchai;

import lombok.Data;

/**
 * @Author: maqiang
 * @CreateTime: 2025-12-17 20:19:31
 * @Description: 质押数据
 */
@Data
public class DongChaiPledgeDataDTO {

    private String code;

    private String name;

    /**
     * 质押日期, 格式：yyyy-MM-dd HH:mm:ss
     */
    private String tradeDate;

    /**
     * 质押比例，4位小数
     */
    private Double pledgeRatio;

}
