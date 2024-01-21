package com.example.mq.data.mapper.order.model;

import com.example.mq.data.mapper.BaseDO;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderDO extends BaseDO {

    private String poCode;

    private Integer orderSource;

    private String purchaser;

    private String supplierCode;

    private String remark;

    private Integer status;

    private String feature;

    private Integer isDeleted;


    private List<Integer> idList;

    private List<String> poCodeList;

}
