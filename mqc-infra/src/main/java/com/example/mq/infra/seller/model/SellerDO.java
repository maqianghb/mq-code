package com.example.mq.infra.seller.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mq.infra.base.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "platform_seller")
public class SellerDO extends BaseDO {

    private String sellerNo;

    private String sellerName;

    private String sellerDesc;

    private String sellerType;

    private Integer sellerAge;

    private String topTenCustomers;

    private String extendInfo;

}
