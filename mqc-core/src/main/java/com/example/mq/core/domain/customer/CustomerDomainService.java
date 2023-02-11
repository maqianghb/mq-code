package com.example.mq.core.domain.customer;

import com.example.mq.core.domain.customer.model.Customer;

import java.util.List;

public interface CustomerDomainService {

    List<Customer> queryCustomerList(Customer customer);

}
