package com.example.mq.core.mapper.customer;

import com.example.mq.core.manager.customer.model.CustomerDO;

import java.util.List;

/**
 * @program: mq-code
 * @description: customer映射接口
 * @author: maqiang
 * @create: 2018/9/19
 *
 */
public interface CustomerMapper {

	Integer countCustomer(CustomerDO customerDO);

	List<CustomerDO> queryCustomerList(CustomerDO customerDO);

	boolean batchAddCustomer(List<CustomerDO> customerDOList);

	boolean batchUpdateCustomer(List<CustomerDO> customerDOList);

}
