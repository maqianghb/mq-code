package com.example.mq.data.mapper.customer;

import com.example.mq.data.mapper.customer.model.CustomerOperateLogDO;

import java.util.List;


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
