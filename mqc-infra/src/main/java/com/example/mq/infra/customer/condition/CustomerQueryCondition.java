package com.example.mq.infra.customer.condition;

import lombok.Data;

import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 15:49:48
 * @Description:
 */
@Data
public class CustomerQueryCondition {

    private String customerNo;

    private List<String> customerNoList;

    /**
     * 页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
