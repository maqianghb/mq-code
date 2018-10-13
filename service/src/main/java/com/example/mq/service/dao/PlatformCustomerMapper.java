package com.example.mq.service.dao;

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

	int insert(Customer feature);

	int update(Customer feature);

	Customer selectByCustomerId(String customerId);

	List<Customer> selectAll();

	int deleteByCustomerId(String customerId);
}
