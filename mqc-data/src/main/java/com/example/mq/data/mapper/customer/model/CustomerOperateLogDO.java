package com.example.mq.data.mapper.customer.model;

import com.example.mq.data.mapper.BaseDO;
import lombok.Data;

@Data
public class CustomerOperateLogDO extends BaseDO {

    private Long customerId;

    private Integer action;
}
