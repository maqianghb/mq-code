package com.example.mq.service.customer;

import com.example.mq.base.common.PageResult;
import com.example.mq.base.common.User;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.bean.CustomerQueryCondition;

import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:24
 */
public interface CustomerService {

	Customer queryById(long id) throws Exception;

    Customer queryByCustomerNo(long customerNo) throws Exception;

    List<Customer> queryAll(CustomerQueryCondition condition) throws Exception;

    PageResult<Customer> pageQuery(CustomerQueryCondition condition, int pageNum, int pageSize) throws Exception;

    long add(Customer customer, User user) throws Exception;

    long updateById(Customer customer, User user) throws Exception;

    long deleteById(long id, User user) throws Exception;

}
