package com.example.mq.core.domain.order.model;


import com.example.mq.core.domain.base.BaseDomain;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrder extends BaseDomain {

    private String poCode;

    private Integer orderSource;

    private String purchaser;

    private String supplierCode;

    private String remark;

    private Integer status;

    private String feature;

    private Integer isDeleted;

    /**
     * 查询用
     */
    private List<Long> idList;

    private List<String> poCodeList;

}
