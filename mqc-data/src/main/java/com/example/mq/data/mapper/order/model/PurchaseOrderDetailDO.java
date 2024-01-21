package com.example.mq.data.mapper.order.model;

import com.example.mq.data.mapper.BaseDO;
import lombok.Data;

import java.util.List;

/**
 * 采购单明细
 */
@Data
public class PurchaseOrderDetailDO extends BaseDO {

    private String poCode;

    private Long materialId;

    private String supplierCode;

    private Integer planNum;

    private Integer deliveryNum;

    private Integer receiveNum;

    private Integer version;

    private Integer isDeleted;

    private List<String> poCodeList;

}
