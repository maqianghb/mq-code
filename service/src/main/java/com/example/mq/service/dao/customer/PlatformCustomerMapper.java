package com.example.mq.service.dao.customer;

import com.example.mq.service.bean.Customer;

import java.util.List;

/**
 * @program: crules-management
 * @description: customer映射接口
 * @author: maqiang
 * @create: 2018/9/19
 *
 */
public interface PlatformCustomerMapper {

	int insert(Customer customer);

	int update(Customer customer);

	Customer selectByCustomerId(Long customerId);

	List<Customer> selectAll();

	int deleteByCustomerId(Long customerId);
}
