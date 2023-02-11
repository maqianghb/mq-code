package com.example.mq.core.mapper.customer;

import java.util.List;

import com.example.mq.core.manager.customer.model.CustomerOperateLogDO;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */

public interface CustomerOperateLogMapper {

	List<CustomerOperateLogDO> queryCustomerOperateLogList(long customerNo);
}
