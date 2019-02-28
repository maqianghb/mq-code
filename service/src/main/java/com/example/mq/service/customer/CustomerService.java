package com.example.mq.service.customer;

import com.example.mq.data.common.PageResult;
import com.example.mq.data.common.User;
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

    Customer queryByCustomerNo(long customerNo) throws Exception;

    PageResult<Customer> pageQuery(CustomerQueryCondition condition, int pageNum, int pageSize) throws Exception;

    long add(Customer customer, User user) throws Exception;

    long updateByCustomerNo(Customer customer, User user) throws Exception;

    long deleteByCustomerNo(long customerNo, User user) throws Exception;

}
