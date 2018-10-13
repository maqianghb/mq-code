package com.example.mq.service.customer;

import com.example.mq.service.bean.Customer;
import com.example.mq.service.common.PageResult;

import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:24
 */
public interface CustomerService {

    Customer queryByCustomerId(String customerId) throws Exception;

    PageResult<Customer> pageQuery(Integer pageNum, Integer pageSize) throws Exception;

    List<Customer> queryAll() throws Exception;

    Integer insert(Customer customer) throws Exception;

    Integer updateByCustomerId(Customer customerId) throws Exception;

    Integer deleteByCustomerId(String CustomerId) throws Exception;

}
