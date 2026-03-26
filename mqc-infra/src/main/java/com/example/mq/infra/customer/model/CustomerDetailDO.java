package com.example.mq.infra.customer.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mq.infra.base.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-25 15:53:07
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "platform_customer_detail")
public class CustomerDetailDO extends BaseDO {

    private String detailNo;

    private String customerNo;

}
