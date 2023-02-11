package com.example.mq.core.manager.customer.model;

import com.example.mq.core.manager.BaseDO;
import lombok.Data;

@Data
public class CustomerOperateLogDO extends BaseDO {

    private Long customerId;

    private Integer action;
}
