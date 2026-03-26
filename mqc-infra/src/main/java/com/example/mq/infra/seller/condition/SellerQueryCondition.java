package com.example.mq.infra.seller.condition;

import lombok.Data;

import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 17:45:26
 * @Description:
 */
@Data
public class SellerQueryCondition {

    private String sellerNo;

    private List<String> sellerNoList;

    /**
     * 页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;

}
