package com.example.mq.service.dao.customer;

import com.example.mq.service.bean.Customer;
import com.example.mq.service.bean.CustomerQueryCondition;

import java.util.List;

/**
 * @program: mq-code
 * @description: customer映射接口
 * @author: maqiang
 * @create: 2018/9/19
 *
 */
public interface PlatformCustomerMapper {

	Customer selectByCustomerNo(long customerNo) throws Exception;

	List<Customer> selectAll() throws Exception;

	List<Customer> selectByCondition(CustomerQueryCondition condition) throws Exception;

	long insert(Customer customer) throws Exception;

	long updateByCustomerNo(Customer customer) throws Exception;

	long deleteByCustomerNo(long customerNo) throws Exception;
}
