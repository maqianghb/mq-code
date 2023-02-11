package com.example.mq.service.customer;

import com.example.mq.core.domain.customer.model.Customer;

import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:24
 */
public interface CustomerDomainService {

	Customer countCustomer(Customer customer);

    List<Customer> queryCustomerByPage(Customer customer);

    List<Customer> queryCustomerList(Customer customer);

}
